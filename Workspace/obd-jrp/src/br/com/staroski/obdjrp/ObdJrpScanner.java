package br.com.staroski.obdjrp;

import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.io.IO;

public final class ObdJrpScanner {

	private static final String SHOW_CURRENT_DATA = "01";
	private static final String PROMPT_CHARACTER = ">";

	private static String formatResult(String mode, String pid, String result) {
		String text = result;
		String responseHeader = responseHeader(mode, pid);
		int offset = text.indexOf(responseHeader);
		if (offset != -1) {
			text = text.substring(offset).trim();
		}
		offset = text.indexOf(PROMPT_CHARACTER);
		if (offset != -1) {
			text = text.substring(0, offset).trim();
		}
		return text;
	}

	private static String responseHeader(String mode, String pid) {
		String response = "4" + mode.charAt(mode.length() - 1);
		return response + pid;
	}

	private final EventMulticaster eventMulticaster;
	private final ELM327 elm327;
	private final ScanLoop scanLoop;

	private List<String> supportedPIDs;

	public ObdJrpScanner(IO connection) throws IOException {
		ObdJrpProperties properties = new ObdJrpProperties();
		this.elm327 = new ELM327(connection, properties.isLogEML327());
		this.eventMulticaster = new EventMulticaster();
		this.scanLoop = new ScanLoop(this);
		addListener(new FileHandler());
	}

	public void addListener(ObdJrpListener listener) {
		eventMulticaster.addListener(listener);
	}

	public void removeListener(ObdJrpListener listener) {
		eventMulticaster.removeListener(listener);
	}

	public void startScanning() throws IOException {
		elm327.exec("ATZ"); // reset
		elm327.exec("ATE0"); // desligando echo
		elm327.exec("ATH0"); // desligando envio dos cabeçalhos
		elm327.exec("ATS0"); // desligando espaços em branco
		elm327.exec("ATSP0"); // definindo detecção automática de protocolo
		loadSupportedPIDs();
		scanLoop.start();
	}

	public void stopScanning() throws IOException {
		scanLoop.stop();
		elm327.exec("ATPC"); // encerra o protocolo atual
	}

	private String execute(String mode, String pid) throws IOException {
		String result = elm327.exec(mode + pid);
		return formatResult(mode, pid, result);
	}

	private void loadSupportedPIDs() {
		try {
			supportedPIDs = new ArrayList<>();
			List<String> reservedPIDs = Arrays.asList(new String[] { "00", "20", "40", "60", "80", "A0", "C0", "E0" });
			List<String> validPIDs = Arrays.asList(reservedPIDs.get(0));
			for (String pid : reservedPIDs) {
				if (!validPIDs.contains(pid)) {
					break;
				}
				String bitmask = execute(SHOW_CURRENT_DATA, pid);
				validPIDs = processBitmask(pid, bitmask);
				supportedPIDs.addAll(validPIDs);
			}
			supportedPIDs.removeAll(reservedPIDs);
		} catch (Throwable t) {
			t.printStackTrace();
			supportedPIDs = new ArrayList<>();
		}
	}

	private List<String> processBitmask(String pid, String bytes) throws IOException {
		List<String> pids = new ArrayList<>();
		try {
			int offset = Integer.parseInt(pid, 16) + 1;
			char[] bitmask = ObdJrpUtils.hexaToBinary(bytes, 32).toCharArray();
			for (int i = 0, value = offset; i < bitmask.length; i++, value++) {
				if (bitmask[i] == '1') {
					pids.add(ObdJrpUtils.decimalToHexa(value, 8));
				}
			}
			return pids;
		} catch (NumberFormatException e) {
			return new ArrayList<>();
		}
	}

	private String removeHeader(String result, String header) {
		int offset = result.indexOf(header);
		if (offset != -1) {
			result = result.substring(offset + header.length());
		}
		return result;
	}

	void notifyError(Throwable error) {
		eventMulticaster.onError(error);
	}

	void notifyFinishPackage(Package dataPackage) {
		eventMulticaster.onFinishPackage(dataPackage);
	}

	void notifyScanned(Scan scannedData) {
		eventMulticaster.onScanned(scannedData);
	}

	void notifyStartPackage(Package dataPackage) {
		eventMulticaster.onStartPackage(dataPackage);
	}

	Scan scan() throws IOException {
		Scan scan = new Scan(System.currentTimeMillis());
		List<Data> dataList = scan.getData();
		for (String pid : supportedPIDs) {
			String result = execute(SHOW_CURRENT_DATA, pid);
			if (!isEmpty(result)) {
				String header = responseHeader(SHOW_CURRENT_DATA, pid);
				result = removeHeader(result, header);
				dataList.add(new Data(pid, result));
			}
		}
		return scan;
	}
}

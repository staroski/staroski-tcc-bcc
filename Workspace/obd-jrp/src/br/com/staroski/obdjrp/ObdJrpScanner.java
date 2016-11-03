package br.com.staroski.obdjrp;

import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.io.IO;

public final class ObdJrpScanner {

	private static final String SHOW_CURRENT_DATA = "01";

	private static final String PROMPT_CHARACTER = ">";

	private static String formatResponse(String mode, String pid, String response) {
		String text = response;
		String header = responseHeader(mode, pid);
		int offset = text.indexOf(header);
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

	private final List<String> supportedPIDs;

	public ObdJrpScanner(IO connection) throws IOException, ELM327Error {
		ObdJrpProperties properties = new ObdJrpProperties();
		elm327 = new ELM327(connection, properties.isLogEML327());
		elm327.execute("ATZ"); // reset
		elm327.execute("ATE0"); // desligando echo
		elm327.execute("ATH0"); // desligando envio dos cabeçalhos
		elm327.execute("ATS0"); // desligando espaços em branco
		elm327.execute("ATSP0"); // definindo detecção automática de protocolo
		supportedPIDs = loadSupportedPIDs();

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
		scanLoop.start();
	}

	public void stopScanning() throws IOException {
		scanLoop.stop();
		elm327.disconnect();
	}

	private String execute(String mode, String pid) throws IOException {
		String response = elm327.execute(mode + pid).trim();
		ELM327Error error = ELM327Error.getError(response);
		if (error != null) {
			error.raise();
		}
		return formatResponse(mode, pid, response);
	}

	private List<String> loadSupportedPIDs() throws IOException, ELM327Error {
		final List<String> reservedPIDs = Arrays.asList(new String[] { //
				"00", // return range 01-20
				"20", // return range 21-40
				"40", // return range 41-60
				"60", // return range 61-80
				"80", // return range 81-A0
				"A0", // return range A1-C0
				"C0", // return range C1-E0
				"E0", // return range E1-FF
		});
		List<String> supportedPIDs = new ArrayList<>();
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
		return supportedPIDs;
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
		List<ELM327Error> errors = new ArrayList<>();
		for (String pid : supportedPIDs) {
			try {
				String response = execute(SHOW_CURRENT_DATA, pid);
				if (!isEmpty(response)) {
					String header = responseHeader(SHOW_CURRENT_DATA, pid);
					response = removeHeader(response, header);
					dataList.add(new Data(pid, response));
				}
			} catch (ELM327Error error) {
				System.out.printf("%s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				errors.add(error);
			}
		}
		if (!errors.isEmpty() && errors.size() == supportedPIDs.size()) {
			stopScanning();
			notifyError(errors.get(0));
		}
		return scan;
	}
}

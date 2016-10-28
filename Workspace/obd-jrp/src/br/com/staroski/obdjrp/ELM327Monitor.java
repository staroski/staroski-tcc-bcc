package br.com.staroski.obdjrp;

import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;

public final class ELM327Monitor {

	private static final String SHOW_CURRENT_DATA = "01";
	private static final String REQUEST_VEHICLE_INFORMATION = "09";
	private static final String PROMPT_CHARACTER = ">";
	private static final int RESPONSES_TO_WAIT = 1;

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
		text = removeSpaces(text);
		return text;
	}

	private static String removeSpaces(String string) {
		StringBuilder text = new StringBuilder(string);
		for (int i = 0; i < text.length(); i++) {
			switch (text.charAt(i)) {
				case ' ':
				case '\r':
					text.deleteCharAt(i);
					i--;
					continue;
			}
		}
		return text.toString();
	}

	private static String responseHeader(String mode, String pid) {
		String response = "4" + mode.charAt(mode.length() - 1);
		return response + pid;
	}

	private final EventMulticaster eventMulticaster;
	private final List<String> supportedPIDs;
	private final ELM327 elm327;
	private final ScanLoop scanLoop;

	private boolean initialized;
	private String vin;

	public ELM327Monitor(ELM327 elm327) {
		this.elm327 = elm327;
		this.eventMulticaster = new EventMulticaster();
		this.supportedPIDs = new ArrayList<>();
		this.scanLoop = new ScanLoop(this);
		addListener(new FileHandler());
	}

	public void addListener(ObdJrpListener listener) {
		eventMulticaster.addListener(listener);
	}

	public void removeListener(ObdJrpListener listener) {
		eventMulticaster.removeListener(listener);
	}

	public void start() {
		scanLoop.start();
	}

	public void stop() {
		scanLoop.stop();
	}

	private void checkState() throws IOException {
		synchronized (this) {
			if (initialized) {
				return;
			}
			String result = execute(REQUEST_VEHICLE_INFORMATION, "02", 0);
			vin = processVIN(result);

			List<String> reservedPids = Arrays.asList(new String[] { "00", "20", "40", "60", "80", "A0", "C0", "E0" });
			for (String pid : reservedPids) {
				result = execute(SHOW_CURRENT_DATA, pid, RESPONSES_TO_WAIT);
				supportedPIDs.addAll(processBitmask(pid, result));
			}
			supportedPIDs.removeAll(reservedPids);
			initialized = true;
		}
	}

	private String execute(String mode, String pid, int responsesToWait) throws IOException {
		StringBuilder cmd = new StringBuilder(mode).append(" ").append(pid);
		if (responsesToWait > 0) {
			cmd.append(" ").append(responsesToWait);
		}
		String result = elm327.exec(cmd.toString());
		return formatResult(mode, pid, result);
	}

	private List<String> getSupportedPIDs() throws IOException {
		checkState();
		return supportedPIDs;
	}

	private List<String> processBitmask(String pid, String bytes) throws IOException {
		List<String> pids = new ArrayList<>();
		try {
			char[] bitmask = ObdJrpUtils.hexaToBinary(bytes, 32).toCharArray();
			int offset = Integer.parseInt(pid, 16) + 1;
			for (int i = 0, value = offset; i < bitmask.length; i++, value++) {
				if (bitmask[i] == '1') {
					pids.add(ObdJrpUtils.decimalToHexa(value, 8));
				}
			}
		} catch (NumberFormatException e) {
			// ignorar
		}
		return pids;
	}

	private String processVIN(String value) {
		value = value.substring(4); // remover cabeçalho de retorno
		String[] lines = value.split(String.valueOf(ELM327.RETURN));
		StringBuilder text = new StringBuilder();
		for (String line : lines) {
			if (line.startsWith("4902")) {
				line = line.substring(4);
			}
			line = line.substring(2); // remover indice
			text.append(line);
		}
		value = text.toString();
		value = ObdJrpUtils.hexaToASCII(value);
		return value.trim();
	}

	String getVIN() throws IOException {
		checkState();
		return vin;
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
		List<String> pids = getSupportedPIDs();
		for (String pid : pids) {
			String result = execute(SHOW_CURRENT_DATA, pid, RESPONSES_TO_WAIT);
			if (!isEmpty(result)) {
				result = result.substring(4); // remover cabeçalho de retorno
				dataList.add(new Data(pid, result));
			}
		}
		return scan;
	}
}

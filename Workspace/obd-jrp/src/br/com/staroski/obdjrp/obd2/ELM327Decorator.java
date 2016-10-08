package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.staroski.obdjrp.utils.Convert;

public final class ELM327Decorator {

	private static final String SHOW_CURRENT_DATA = "01";
	private static final String REQUEST_VEHICLE_INFORMATION = "09";
	private static final int RESPONSES_TO_WAIT = 1;

	private static String responseHeader(String mode, String pid) {
		String response = "4" + mode.charAt(mode.length() - 1);
		return response + pid;
	}

	private static String resultBytes(String mode, String pid, String result) {
		String text = result;
		String responseHeader = responseHeader(mode, pid);
		int offset = text.indexOf(responseHeader);
		if (offset != -1) {
			text = text.substring(offset).trim();
		}
		if (text.endsWith(">")) {
			text = text.substring(0, text.length() - 1).trim();
		}
		return text;
	}

	private final OBD2EventMulticaster eventMulticaster;
	private final List<String> supportedPIDs;
	private final ELM327 elm327;
	private final ScanLoop scanLoop;

	private boolean initialized;
	private String vin;

	public ELM327Decorator(ELM327 elm327) {
		this.elm327 = elm327;
		this.eventMulticaster = new OBD2EventMulticaster();
		this.supportedPIDs = new ArrayList<>();
		this.scanLoop = new ScanLoop(this);
		addListener(new FileHandler());
	}

	public void addListener(OBD2Listener listener) {
		eventMulticaster.addListener(listener);
	}

	public void removeListener(OBD2Listener listener) {
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

			String[] pids = new String[] { "00", "20", "40", "60", "80", "A0", "C0", "E0" };
			for (String pid : pids) {
				result = execute(SHOW_CURRENT_DATA, pid, RESPONSES_TO_WAIT);
				supportedPIDs.addAll(processBitmask(pid, result));
			}
			initialized = true;
		}
	}

	private String execute(String mode, String pid, int responses) throws IOException {
		String result = elm327.exec(mode + " " + pid + (responses < 1 ? "" : " " + responses));
		return resultBytes(mode, pid, result);
	}

	private List<String> getSupportedPIDs() throws IOException {
		checkState();
		return supportedPIDs;
	}

	private List<String> processBitmask(String pid, String bytes) throws IOException {
		List<String> pids = new ArrayList<>();
		try {
			char[] bitmask = Convert.hexaToBinary(bytes, 32).toCharArray();
			int offset = Integer.parseInt(pid, 16) + 1;
			for (int i = 0, value = offset; i < bitmask.length; i++, value++) {
				if (bitmask[i] == '1') {
					pids.add(Convert.decimalToHexa(value, 8));
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
			line = line.substring(2); // remover indice
			text.append(line);
		}
		value = text.toString();
		value = Convert.hexaToASCII(value);
		return value;
	}

	String getVIN() throws IOException {
		checkState();
		return vin;
	}

	void notifyError(Throwable error) {
		eventMulticaster.onError(error);
	}

	void notifyFinishPackage(OBD2Package dataPackage) {
		eventMulticaster.onFinishPackage(dataPackage);
	}

	void notifyScanned(OBD2Scan scannedData) {
		eventMulticaster.onScanned(scannedData);
	}

	void notifyStartPackage(OBD2Package dataPackage) {
		eventMulticaster.onStartPackage(dataPackage);
	}

	OBD2Scan scan() throws IOException {
		OBD2Scan dataPackage = new OBD2Scan();
		List<OBD2Data> dataList = dataPackage.getData();
		List<String> pids = getSupportedPIDs();
		for (String pid : pids) {
			String result = execute(SHOW_CURRENT_DATA, pid, RESPONSES_TO_WAIT);
			result = result.substring(4); // remover cabeçalho de retorno
			dataList.add(new OBD2Data(pid, result));
		}
		return dataPackage;
	}
}

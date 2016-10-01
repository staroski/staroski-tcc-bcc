package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.staroski.obdjrp.utils.Convert;

public final class OBD2Monitor {

	private class ScanLoop implements Runnable {

		private static final int ONE_SECOND = 1000;

		@Override
		public void run() {
			while (scanning) {
				try {
					OBD2DataPackage dataPackage = new OBD2DataPackage(getVIN(), System.currentTimeMillis());
					List<OBD2DataScan> scannedList = dataPackage.getScannedData();
					notifyStartPackage(dataPackage);
					for (int i = 0; i < 10 && scanning; i++) {
						long begin = System.currentTimeMillis();
						OBD2DataScan scannedData = scan();
						scannedList.add(scannedData);
						notifyScanned(scannedData);
						long end = System.currentTimeMillis();
						long elapsed = end - begin;
						if (elapsed < ONE_SECOND) {
							Thread.sleep(ONE_SECOND - elapsed);
						} else {
							Thread.yield();
						}
					}
					notifyFinishPackage(dataPackage);
				} catch (IOException e) {
					e.printStackTrace();
					scanning = false;
					notifyError(e);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final String MODE_SHOW_CURRENT_DATA = "01";
	private static final String MODE_REQUEST_VEHICLE_INFORMATION = "09";
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

	private final OBD2EventMulticaster eventMulticaster = new OBD2EventMulticaster();
	private final ELM327 elm327;

	private boolean scanning;

	private boolean initialized;

	private String vin;
	private final List<String> supportedPIDs = new ArrayList<>();

	public OBD2Monitor(ELM327 elm327) {
		this.elm327 = elm327;
	}

	public void addListener(OBD2Listener listener) {
		eventMulticaster.addListener(listener);
	}

	public void removeListener(OBD2Listener listener) {
		eventMulticaster.removeListener(listener);
	}

	public void start() {
		if (!scanning) {
			scanning = true;
			new Thread(new ScanLoop()).start();
		}
	}

	public void stop() {
		scanning = false;
	}

	private void checkState() throws IOException {
		synchronized (this) {
			if (initialized) {
				return;
			}
			String result = execute(MODE_REQUEST_VEHICLE_INFORMATION, "02", 0);
			vin = processVIN(result);

			String[] pids = new String[] { "00", "20", "40", "60", "80", "A0", "C0", "E0" };
			for (String pid : pids) {
				result = execute(MODE_SHOW_CURRENT_DATA, pid, RESPONSES_TO_WAIT);
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

	private String getVIN() throws IOException {
		checkState();
		return vin;
	}

	private void notifyError(Exception error) {
		eventMulticaster.onError(error);
	}

	private void notifyFinishPackage(OBD2DataPackage dataPackage) {
		eventMulticaster.onFinishPackage(dataPackage);
	}

	private void notifyScanned(OBD2DataScan scannedData) {
		eventMulticaster.onScanned(scannedData);
	}

	private void notifyStartPackage(OBD2DataPackage dataPackage) {
		eventMulticaster.onStartPackage(dataPackage);
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

	private OBD2DataScan scan() throws IOException {
		OBD2DataScan dataPackage = new OBD2DataScan();
		List<OBD2Data> dataList = dataPackage.getDataList();
		List<String> pids = getSupportedPIDs();
		for (String pid : pids) {
			String result = execute(MODE_SHOW_CURRENT_DATA, pid, RESPONSES_TO_WAIT);
			result = result.substring(4); // remover cabeçalho de retorno
			dataList.add(new OBD2Data(pid, result));
		}
		return dataPackage;
	}
}

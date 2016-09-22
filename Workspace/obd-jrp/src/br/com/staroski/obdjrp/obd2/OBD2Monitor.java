package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.staroski.obdjrp.utils.Base;

public final class OBD2Monitor {

	private class ScanLoop implements Runnable {

		private static final int ONE_SECOND = 1000;

		@Override
		public void run() {
			while (scanning) {
				try {
					long begin = System.currentTimeMillis();
					List<OBD2Data> data = scan();
					notifyUpdate(data);
					long end = System.currentTimeMillis();
					long elapsed = end - begin;
					if (elapsed < ONE_SECOND) {
						Thread.sleep(ONE_SECOND - elapsed);
					} else {
						Thread.yield();
					}
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
	private static final String RESPONSES_TO_WAIT = "1";

	private static String responseHeader(String mode, String pid) {
		String response = "4" + mode.charAt(mode.length() - 1);
		return response + pid;
	}

	private static String resultBytes(String mode, String pid, String result) {
		String text = result;
		String responseHeader = responseHeader(mode, pid);
		int offset = text.indexOf(responseHeader);
		if (offset != -1) {
			text = text.substring(offset + 4).trim();
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
			String result = execute(MODE_REQUEST_VEHICLE_INFORMATION, "02");
			vin = processVIN(result);

			String[] pids = new String[] { "00", "20", "40", "60", "80", "A0", "C0", "E0" };
			for (String pid : pids) {
				result = execute(MODE_SHOW_CURRENT_DATA, pid);
				supportedPIDs.addAll(processBitmask(pid, result));
			}
			//
			// String mode = MODE_SHOW_CURRENT_DATA;
			// supportedPIDs.addAll(getSupportedPIDs(mode, "00")); // 00 [01-20]
			// supportedPIDs.addAll(getSupportedPIDs(mode, "20")); // 20 [21-40]
			// supportedPIDs.addAll(getSupportedPIDs(mode, "40")); // 40 [41-60]
			// supportedPIDs.addAll(getSupportedPIDs(mode, "60")); // 60 [61-80]
			// supportedPIDs.addAll(getSupportedPIDs(mode, "80")); // 80 [81-A0]
			// supportedPIDs.addAll(getSupportedPIDs(mode, "A0")); // A0 [A1-C0]
			// supportedPIDs.addAll(getSupportedPIDs(mode, "C0")); // C0 [C1-E0]
			// supportedPIDs.addAll(getSupportedPIDs(mode, "E0")); // E0 [E1-20]

			initialized = true;
		}
	}

	private String execute(String mode, String pid) throws IOException {
		String result = elm327.exec(mode + " " + pid + " " + RESPONSES_TO_WAIT);
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

	private void notifyUpdate(List<OBD2Data> data) {
		eventMulticaster.onUpdate(data);
	}

	private List<String> processBitmask(String pid, String bytes) throws IOException {
		List<String> pids = new ArrayList<>();
		try {
			char[] bitmask = Base.hexaToBin(bytes, 32).toCharArray();
			int offset = Integer.parseInt(pid, 16) + 1;
			for (int i = 0, value = offset; i < bitmask.length; i++, value++) {
				if (bitmask[i] == '1') {
					pids.add(Base.decToHexa(value, 8));
				}
			}
		} catch (NumberFormatException e) {
			// ignorar
		}
		return pids;
	}

	private String processVIN(String value) {
		value = value.replaceAll("\n", "");
		value = value.replaceAll("\r", "");
		value = Base.hexaToASCII(value);
		return value;
	}

	private List<OBD2Data> scan() throws IOException {
		List<OBD2Data> data = new LinkedList<>();
		List<String> pids = getSupportedPIDs();
		for (String pid : pids) {
			String result = execute(MODE_SHOW_CURRENT_DATA, pid);
			data.add(new OBD2Data(pid, result));
		}
		return data;
	}
}

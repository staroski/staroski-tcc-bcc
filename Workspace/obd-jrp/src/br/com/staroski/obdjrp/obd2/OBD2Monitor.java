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

	private static final String OBD_MODE = "01";
	private static final String RESPONSE_HEADER = "41";
	private static final String RESPONSES_TO_WAIT = "1";

	private static String resultBytes(String pid, String result) {
		String text = result;
		int offset = text.indexOf((RESPONSE_HEADER + pid));
		if (offset != -1) {
			text = text.substring(offset + 4).trim();
		}
		if (text.endsWith(">")) {
			text = text.substring(0, text.length() - 1).trim();
		}
		return text;
	}

	private final ELM327 elm327;

	private final OBD2Listener listener;

	private boolean scanning;

	private final List<String> supportedPIDs = new ArrayList<>();

	public OBD2Monitor(ELM327 elm327, OBD2Listener listener) {
		this.elm327 = elm327;
		this.listener = listener;
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

	private String execute(String pid) throws IOException {
		String result = elm327.exec(OBD_MODE + " " + pid + " " + RESPONSES_TO_WAIT);
		return resultBytes(pid, result);
	}

	private OBD2Data getRpm() throws IOException {
		String description = "Engine RPM";
		String pid = "0C";
		String result = execute(pid);
		int a = Integer.parseInt(result, 16);
		int value = a / 4;
		return new OBD2Data(pid, result, description, value);
	}

	private OBD2Data getSpeed() throws IOException {
		String description = "Vehicle speed";
		String pid = "0D";
		String result = execute(pid);
		int value = Integer.parseInt(result, 16);
		return new OBD2Data(pid, result, description, value);
	}

	private List<String> getSupportedPIDs() throws IOException {
		if (supportedPIDs.isEmpty()) {
			supportedPIDs.addAll(getSupportedPIDs("00")); // 00 [01-20]
			supportedPIDs.addAll(getSupportedPIDs("20")); // 20 [21-40]
			supportedPIDs.addAll(getSupportedPIDs("40")); // 40 [41-60]
			supportedPIDs.addAll(getSupportedPIDs("60")); // 60 [61-80]
			supportedPIDs.addAll(getSupportedPIDs("80")); // 80 [81-A0]
			supportedPIDs.addAll(getSupportedPIDs("A0")); // A0 [A1-C0]
			supportedPIDs.addAll(getSupportedPIDs("C0")); // C0 [C1-E0]
			supportedPIDs.addAll(getSupportedPIDs("E0")); // E0 [E1-20]
		}
		return supportedPIDs;
	}

	private List<String> getSupportedPIDs(String pid) throws IOException {
		List<String> pids = new ArrayList<>();
		char[] binary = new char[0];
		String result = execute(pid);
		try {
			binary = Base.hexaToBin(result, 32).toCharArray();
		} catch (NumberFormatException e) {
			// ignorar
		}
		if (binary.length > 0) {
			int offset = Integer.parseInt(pid, 16) + 1;
			for (int i = 0, value = offset; i < binary.length; i++, value++) {
				if (binary[i] == '1') {
					pids.add(Base.decToHexa(value, 8));
				}
			}
		}
		return pids;
	}

	private void notifyError(Exception error) {
		listener.onError(error);
	}

	private void notifyUpdate(List<OBD2Data> data) {
		listener.onUpdate(data);
	}

	private List<OBD2Data> scan() throws IOException {
		List<OBD2Data> data = new LinkedList<>();
		List<String> pids = getSupportedPIDs();
		int item = 0;
		for (String pid : pids) {
			String description = "item " + item;
			String result = execute(pid);
			long value = Long.parseLong(result, 16);
			data.add(new OBD2Data(pid, result, description, value));
			item++;
		}
		return data;
	}
}

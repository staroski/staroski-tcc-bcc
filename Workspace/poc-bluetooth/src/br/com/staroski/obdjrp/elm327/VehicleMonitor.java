package br.com.staroski.obdjrp.elm327;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public final class VehicleMonitor {

	private class Loop implements Runnable {

		private static final int ONE_SECOND = 1000;

		@Override
		public void run() {
			while (scanning) {
				try {
					long begin = System.currentTimeMillis();
					List<VehicleData> data = scanData();
					notifyData(data);
					long end = System.currentTimeMillis();
					long elapsed = end - begin;
					if (elapsed < ONE_SECOND) {
						Thread.sleep(ONE_SECOND - elapsed);
					} else {
						Thread.yield();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private final Elm327 elm327;
	private final VehicleListener listener;

	private boolean scanning;

	public VehicleMonitor(Elm327 elm327, VehicleListener listener) {
		this.elm327 = elm327;
		this.listener = listener;
	}

	public void start() {
		if (!scanning) {
			scanning = true;
			new Thread(new Loop()).start();
		}
	}

	public void stop() {
		scanning = false;
	}

	private VehicleData getRpm() {
		String name = "RPM";
		int value = -1;
		try {
			String string = elm327.exec("01 0C 1");
			String text = string;
			if (text.startsWith("41")) {
				text = text.substring(4).trim();
			}
			if (text.endsWith(">")) {
				text = text.substring(0, text.length() - 1).trim();
			}
			int a = Integer.parseInt(text, 16);
			value = a / 4;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Data(name, value);
	}

	private VehicleData getSpeed() {
		String name = "Velocidade";
		int value = -1;
		try {
			String string = elm327.exec("01 0D 1");
			String text = string;
			if (text.startsWith("41")) {
				text = text.substring(4).trim();
			}
			if (text.endsWith(">")) {
				text = text.substring(0, text.length() - 1).trim();
			}
			value = Integer.parseInt(text, 16);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Data(name, value);
	}

	private void notifyData(List<VehicleData> data) {
		listener.onDataReceived(data);
	}

	private List<VehicleData> scanData() {
		List<VehicleData> data = new LinkedList<>();
		data.add(getSpeed());
		data.add(getRpm());
		return data;
	}
}

package br.com.staroski.obdjrp;

import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import br.com.staroski.bluetooth.Bluetooth;
import br.com.staroski.bluetooth.DeviceFilter;

public class ExemploOBDJRP {

	public static void main(String[] args) {
		try {
			new ExemploOBDJRP().execute();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private static final DeviceFilter MOTO_G_STAROSKI = new DeviceFilter() {

		@Override
		public boolean accept(RemoteDevice device) {
			return "F8E079DAE781".equals(device.getBluetoothAddress());
		}
	};

	private final Elm327 elm327;

	private ExemploOBDJRP() throws IOException {
		RemoteDevice device = Bluetooth.get.device(MOTO_G_STAROSKI);
		elm327 = Elm327.connect(device);
	}

	private void execute() throws IOException {
		String[] result = send("01", "00");
		receive(result);
	}

	private String[] send(String mode, String pid, String... morePids) throws IOException {
		System.out.print("send: " + mode + " " + pid);
		for (int i = 0, n = morePids.length; i < n; i++) {
			System.out.print(" " + morePids[i]);
		}
		System.out.println();
		return elm327.send(mode, pid, morePids);
	}

	private void receive(String... values) {
		System.out.print("received: ");
		for (int i = 0, n = values.length; i < n; i++) {
			if (i > 0) {
				System.out.print(" ");
			}
			System.out.print(values[i]);
		}
		System.out.println();
	}
}

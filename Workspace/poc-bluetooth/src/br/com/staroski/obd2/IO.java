package br.com.staroski.obd2;

import java.io.Closeable;
import java.io.IOException;

public interface IO extends Closeable {

	public static final class Factory {

		private Factory() {
		}

		public IO bluetooth(String deviceAddress, String serviceName) throws IOException {
			return new Bluetooth(deviceAddress, serviceName);
		}

		public IO serial(String port) throws IOException {
			return new Serial(port);
		}
	}

	public static final Factory factory = new Factory();

	public byte[] read() throws IOException;

	public void write(byte[] bytes) throws IOException;
}

package br.com.staroski.obd2;

import java.io.Closeable;
import java.io.IOException;

public interface IO extends Closeable {

	public static final class Provider {

		private Provider() {}

		public IO bluetooth(String address) throws IOException {
			return new Bluetooth(address);
		}

		public IO serial(String port) throws IOException {
			return new Serial(port);
		}
	}

	public static final Provider connect = new Provider();

	public byte[] read() throws IOException;

	public void write(byte[] bytes) throws IOException;
}

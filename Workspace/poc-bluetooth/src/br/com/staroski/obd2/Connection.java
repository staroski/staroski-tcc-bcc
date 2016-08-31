package br.com.staroski.obd2;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends Closeable {

	public static final class Builder {

		private Builder() {}

		public Connection bluetooth(String deviceAddress, String serviceName) throws IOException {
			return new Bluetooth(deviceAddress, serviceName);
		}

		public Connection serial(String port) throws IOException {
			return new Serial(port);
		}
	}

	public static final Builder open = new Builder();

	public byte[] read() throws IOException;

	public void write(byte[] bytes) throws IOException;
}

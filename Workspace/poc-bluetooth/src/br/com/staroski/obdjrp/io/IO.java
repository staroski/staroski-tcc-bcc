package br.com.staroski.obdjrp.io;

import java.io.Closeable;
import java.io.IOException;

import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

public interface IO extends Closeable {

	public static final class Builder {

		private Builder() {}

		public IO bluetooth(String deviceAddress, String serviceName) throws IOException {
			return new Bluetooth(new Setup(deviceAddress, serviceName));
		}
	}

	public static final Builder connect = new Builder();

	public int available() throws IOException;

	public int read(byte[] buffer, int offset, int length) throws IOException;

	public void write(byte[] buffer, int offset, int length) throws IOException;
}

package br.com.staroski.obdjrp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import br.com.staroski.obdjrp.bluetooth.Bluetooth;

final class ConnectionBluetooth implements ObdJrpConnection {

	private final String device;
	private final String service;

	private boolean open;
	private Connection connection;
	private InputStream input;
	private OutputStream output;

	ConnectionBluetooth(ObdJrpProperties properties) throws IOException {
		device = properties.checkProperty(ObdJrpProperties.BLUETOOTH_DEVICE);
		service = properties.checkProperty(ObdJrpProperties.BLUETOOTH_SERVICE);
	}

	@Override
	public ObdJrpConnection close() {
		if (open) {
			try {
				input.close();
				output.close();
				connection.close();
				open = false;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return this;
	}

	@Override
	public InputStream getInput() {
		return input;
	}

	@Override
	public OutputStream getOutput() {
		return output;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public ObdJrpConnection open() throws IOException {
		if (!open) {
			connection = Bluetooth.connect(device, service);
			input = ((InputConnection) connection).openInputStream();
			output = ((OutputConnection) connection).openOutputStream();
			open = true;
		}
		return this;
	}

	@Override
	public String toString() {
		return String.format("%s device: \"%s\" service: \"%s\"", //
				ObdJrpProperties.BLUETOOTH, //
				device, //
				service);
	}
}

package br.com.staroski.obd2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

final class Bluetooth implements Connection {

	private final InputStream input;
	private final OutputStream output;

	Bluetooth(String deviceAddress, String serviceName) throws IOException {
		RemoteDevice device = DeviceScanner.find(deviceAddress);
		ServiceRecord service = ServiceScanner.find(device, serviceName);
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		Object connection = Connector.open(url);
		input = ((InputConnection) connection).openInputStream();
		output = ((OutputConnection) connection).openOutputStream();
	}

	@Override
	public void close() throws IOException {
		input.close();
		output.close();
	}

	@Override
	public byte[] read() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		int value = -1;
		while ((value = input.read()) != -1) {
			bytes.write(value);
			if (value == '>') {
				break;
			}
		}
		return bytes.toByteArray();
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		output.write(bytes);
	}
}

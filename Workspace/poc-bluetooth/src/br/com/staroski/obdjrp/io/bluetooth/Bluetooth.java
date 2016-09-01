package br.com.staroski.obdjrp.io.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.io.Settings;

public final class Bluetooth implements IO {

	private final InputStream input;
	private final OutputStream output;

	public Bluetooth(Settings parameters) throws IOException {
		String deviceAddress = parameters.next();
		String serviceName = parameters.next();
		RemoteDevice device = DeviceScanner.find(deviceAddress);
		ServiceRecord service = ServiceScanner.find(device, serviceName);
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		Object connection = Connector.open(url);
		input = ((InputConnection) connection).openInputStream();
		output = ((OutputConnection) connection).openOutputStream();
	}

	@Override
	public int available() throws IOException {
		return input.available();
	}

	@Override
	public void close() throws IOException {
		input.close();
		output.close();
	}

	public int read(byte[] buffer, int offset, int length) throws IOException {
		return input.read(buffer, offset, length);
	}

	public void write(byte[] buffer, int offset, int length) throws IOException {
		output.write(buffer, offset, length);
	}
}

package br.com.staroski.obdjrp.io.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import br.com.staroski.obdjrp.io.IO;

public final class Bluetooth implements IO {

	public static boolean PRINT_DEBUG_INFO = true;

	public static IO connect(RemoteDevice device, ServiceRecord service) throws IOException {
		return new Bluetooth(device, service);
	}

	public static IO connect(String deviceAddress, String serviceName) throws IOException {
		RemoteDevice device = Devices.find(deviceAddress);
		ServiceRecord service = Services.find(device, serviceName);
		return connect(device, service);
	}

	public static List<RemoteDevice> getDevices() throws IOException {
		return Devices.list();
	}

	public static String getName(ServiceRecord service) {
		return Services.getName(service);
	}

	public static List<ServiceRecord> getServices(RemoteDevice device) throws IOException {
		return Services.list(device);
	}

	public static List<ServiceRecord> getServices(String deviceAddress) throws IOException {
		RemoteDevice device = Devices.find(deviceAddress);
		return getServices(device);
	}

	private final InputStream input;
	private final OutputStream output;

	private Bluetooth(RemoteDevice device, ServiceRecord service) throws IOException {
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

	@Override
	public void flush() throws IOException {
		output.flush();
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		return input.read(buffer, offset, length);
	}

	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {
		output.write(buffer, offset, length);
	}
}

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

	public static Bluetooth connect(RemoteDevice device, ServiceRecord service) throws IOException {
		return new Bluetooth(device, service);
	}

	public static Bluetooth connect(String deviceAddress, String serviceName) throws IOException {
		RemoteDevice device = DeviceScanner.find(deviceAddress);
		ServiceRecord service = ServiceScanner.find(device, serviceName);
		return connect(device, service);
	}

	public static String getName(ServiceRecord service) {
		return ServiceScanner.getName(service);
	}

	public static List<RemoteDevice> listDevices() throws IOException {
		return DeviceScanner.scan();
	}

	public static List<ServiceRecord> listServices(RemoteDevice device) throws IOException {
		return ServiceScanner.scan(device);
	}

	public static List<ServiceRecord> listServices(String deviceAddress) throws IOException {
		RemoteDevice device = DeviceScanner.find(deviceAddress);
		return listServices(device);
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

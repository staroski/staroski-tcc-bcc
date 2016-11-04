package br.com.staroski.obdjrp.io.bluetooth;

import java.io.IOException;
import java.util.List;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import br.com.staroski.obdjrp.io.IO;

public final class Bluetooth {

	/**
	 * Atributo correspondente ao nome do serviço (<code>0x0100</code>)
	 */
	static final short ATTRIBUTE_SERVICE_NAME = 0x0100;

	/**
	 * UUID correspondente ao SPP (<code>0000</code><B><code>1101</code></B><code>00001000800000805F9B34FB</code>)
	 */
	static final UUID UUID_SERIAL_PORT_PROFILE = BaseUUID.merge16bits((short) 0x1101);

	public static IO connect(RemoteDevice device, ServiceRecord service) throws IOException {
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		Connection connection = Connector.open(url);
		return new IO(connection);
	}

	public static IO connect(String deviceAddress, String serviceName) throws IOException {
		RemoteDevice device = DeviceScanner.find(deviceAddress);
		ServiceRecord service = ServiceScanner.find(device, serviceName);
		return connect(device, service);
	}

	public static String getName(ServiceRecord service) {
		return ServiceScanner.getName(service);
	}

	public static List<RemoteDevice> getRemoteDevices() throws IOException {
		return DeviceScanner.list();
	}

	public static List<ServiceRecord> getServices(RemoteDevice device) throws IOException {
		return ServiceScanner.list(device);
	}

	public static List<ServiceRecord> getServices(String deviceAddress) throws IOException {
		RemoteDevice device = DeviceScanner.find(deviceAddress);
		return getServices(device);
	}

	private Bluetooth() {}
}

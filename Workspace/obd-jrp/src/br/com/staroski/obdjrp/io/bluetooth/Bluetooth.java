package br.com.staroski.obdjrp.io.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import br.com.staroski.obdjrp.io.IO;

public final class Bluetooth {

	// listener para descoberta de dispositivos
	private static class DeviceDiscovery extends DiscoveryAdapter {

		private final List<RemoteDevice> devices;

		public DeviceDiscovery(List<RemoteDevice> devices) {
			this.devices = devices;
		}

		@Override
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			devices.add(btDevice);
		}

		@Override
		public void inquiryCompleted(int discType) {
			synchronized (LOCK) {
				LOCK.notifyAll();
			}
		}
	}

	// listener para descoberta de servicos
	private static class ServiceDiscovery extends DiscoveryAdapter {

		private final List<ServiceRecord> services;

		public ServiceDiscovery(List<ServiceRecord> services) {
			this.services = services;
		}

		@Override
		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			for (ServiceRecord service : servRecord) {
				services.add(service);
			}
		}

		@Override
		public void serviceSearchCompleted(int transID, int respCode) {
			synchronized (LOCK) {
				LOCK.notifyAll();
			}
		}
	}

	// Atributo correspondente ao nome do servico
	private static final short ATTRIBUTE_SERVICE_NAME = 0x0100;

	// UUID do SPP
	private static final UUID UUID_SERIAL_PORT_PROFILE = BaseUUID.merge16bits((short) 0x1101);

	// objeto utilizado para sincronizacao
	private static final Object LOCK = new Object();

	public static IO connect(RemoteDevice device, ServiceRecord service) throws IOException {
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		Connection connection = Connector.open(url);
		return new IO(connection);
	}

	public static IO connect(String deviceAddress, String serviceName) throws IOException {
		RemoteDevice device = findDevice(deviceAddress);
		ServiceRecord service = findService(device, serviceName);
		return connect(device, service);
	}

	public static RemoteDevice findDevice(String deviceAddress) throws IOException {
		List<RemoteDevice> devices = listDevices();
		if (devices.isEmpty()) {
			throw new IllegalStateException(String.format("No devices found!"));
		}
		for (RemoteDevice device : devices) {
			if (deviceAddress.equals(device.getBluetoothAddress())) {
				return device;
			}
		}
		throw new IllegalStateException(String.format("Device %s not found!", deviceAddress));
	}

	public static ServiceRecord findService(final RemoteDevice device, String serviceName) throws IOException {
		List<ServiceRecord> services = listServices(device);
		if (services.isEmpty()) {
			throw new IllegalStateException(String.format("There is no service called \"%s\" running on device %s", serviceName, device.getBluetoothAddress()));
		}
		for (ServiceRecord service : services) {
			String name = getServiceName(service);
			if (serviceName.equals(name)) {
				return service;
			}
		}
		throw new IllegalStateException(String.format("Service \"%s\" not found on device %s", serviceName, device.getBluetoothAddress()));
	}

	public static String getServiceName(ServiceRecord service) {
		DataElement attribute = service.getAttributeValue(Bluetooth.ATTRIBUTE_SERVICE_NAME);
		String name = attribute == null ? null : (String) attribute.getValue();
		return name == null ? null : name.trim();
	}

	public static List<RemoteDevice> listDevices() throws IOException {
		final List<RemoteDevice> devices = new ArrayList<>();
		synchronized (LOCK) {
			DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
			boolean started = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, new DeviceDiscovery(devices));
			if (started) {
				try {
					LOCK.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return devices;
	}

	public static List<ServiceRecord> listServices(RemoteDevice device) throws IOException {
		List<ServiceRecord> services = new ArrayList<>();
		synchronized (LOCK) {
			int[] attributes = new int[] { Bluetooth.ATTRIBUTE_SERVICE_NAME };
			UUID[] uuids = new UUID[] { Bluetooth.UUID_SERIAL_PORT_PROFILE };
			int transactionID = LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attributes, uuids, device, new ServiceDiscovery(services));
			if (transactionID > 0) {
				try {
					LOCK.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return services;
	}

	private Bluetooth() {}
}

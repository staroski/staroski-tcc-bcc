package br.com.staroski.obdjrp.bluetooth;

import java.io.IOException;
import java.util.LinkedList;
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

import br.com.staroski.obdjrp.utils.Conversions;
import br.com.staroski.obdjrp.utils.Lock;

public final class Bluetooth {

	// listener para descoberta de dispositivos
	private static final DeviceDiscovery DEVICE_LISTENER = new DeviceDiscovery();

	private static class DeviceDiscovery extends DiscoveryAdapter {

		private final List<RemoteDevice> devices = new LinkedList<>();

		@Override
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			devices.add(btDevice);
		}

		public List<RemoteDevice> getDevices() {
			return devices;
		}

		@Override
		public void inquiryCompleted(int discType) {
			LOCK.unlock();
		}

		public void reset() {
			devices.clear();
		}
	}

	// listener para descoberta de servicos
	private static final ServiceDiscovery SERVICE_LISTENER = new ServiceDiscovery();

	private static class ServiceDiscovery extends DiscoveryAdapter {

		private final List<ServiceRecord> services = new LinkedList<>();

		private int transactionID;

		public List<ServiceRecord> getServices() {
			return services;
		}

		public int getTransactionID() {
			return transactionID;
		}

		public void reset() {
			services.clear();
		}

		@Override
		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			for (ServiceRecord service : servRecord) {
				services.add(service);
			}
		}

		@Override
		public void serviceSearchCompleted(int transID, int respCode) {
			LOCK.unlock();
		}

		public void setTransactionID(int transactionID) {
			this.transactionID = transactionID;
		}
	}

	// Atributo correspondente ao nome do servico
	private static final short NAME = 0x0100;

	private static final String SDP_BASE_UUID_MASK = "0000xxxx00001000800000805F9B34FB";

	// UUID do SPP
	private static final UUID SPP = merge16bits((short) 0x1101);

	// objeto utilizado para sincronizacao
	private static final Lock LOCK = new Lock();

	public static Connection connect(RemoteDevice device, ServiceRecord service) throws IOException {
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		return Connector.open(url);
	}

	public static Connection connect(String deviceAddress, String serviceName) throws IOException {
		RemoteDevice device = findDevice(deviceAddress);
		ServiceRecord service = findService(device, serviceName);
		return connect(device, service);
	}

	public static RemoteDevice findDevice(String deviceAddress) throws IOException {
		List<RemoteDevice> devices = listDevices();
		if (devices.isEmpty()) {
			String message = String.format("No devices found!");
			throw new IOException(message);
		}
		for (RemoteDevice device : devices) {
			if (deviceAddress.equals(device.getBluetoothAddress())) {
				return device;
			}
		}
		String message = String.format("Device %s not found!", deviceAddress);
		throw new IOException(message);
	}

	public static ServiceRecord findService(final RemoteDevice device, String serviceName) throws IOException {
		List<ServiceRecord> services = listServices(device);
		if (services.isEmpty()) {
			String message = String.format("There is no service called \"%s\" running on device %s", serviceName, device.getBluetoothAddress());
			throw new IOException(message);
		}
		for (ServiceRecord service : services) {
			String name = getServiceName(service);
			if (serviceName.equals(name)) {
				return service;
			}
		}
		String message = String.format("Service \"%s\" not found on device %s", serviceName, device.getBluetoothAddress());
		throw new IOException(message);
	}

	public static String getServiceName(ServiceRecord service) {
		DataElement attribute = service.getAttributeValue(Bluetooth.NAME);
		String name = attribute == null ? null : (String) attribute.getValue();
		return name == null ? null : name.trim();
	}

	public static List<RemoteDevice> listDevices() throws IOException {
		synchronized (LOCK) {
			DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
			agent.cancelInquiry(DEVICE_LISTENER);
			DEVICE_LISTENER.reset();
			if (agent.startInquiry(DiscoveryAgent.GIAC, DEVICE_LISTENER)) {
				LOCK.lock();
			}
		}
		return DEVICE_LISTENER.getDevices();
	}

	public static List<ServiceRecord> listServices(RemoteDevice device) throws IOException {
		synchronized (LOCK) {
			int[] attributes = new int[] { Bluetooth.NAME };
			UUID[] uuids = new UUID[] { Bluetooth.SPP };
			DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
			agent.cancelServiceSearch(SERVICE_LISTENER.getTransactionID());
			SERVICE_LISTENER.reset();
			int id = agent.searchServices(attributes, uuids, device, SERVICE_LISTENER);
			SERVICE_LISTENER.setTransactionID(id);
			if (id > 0) {
				LOCK.lock();
			}
		}
		return SERVICE_LISTENER.getServices();
	}

	/**
	 * A 16-bit Attribute UUID replaces the x’s in the following:
	 * 
	 * <pre>
	 * 00000000-0000-1000-8000-00805F9B34FB // BASE UUID
	 * 0000xxxx-0000-1000-8000-00805F9B34FB
	 * </pre>
	 */
	public static UUID merge16bits(short uuid) {
		String merged = SDP_BASE_UUID_MASK.replace("xxxx", Conversions.decimalToHexa(uuid, 16));
		return new UUID(merged, false);
	}

	private Bluetooth() {}
}

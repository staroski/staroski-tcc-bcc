package br.com.staroski.obdjrp.io.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

final class ServiceScanner extends DiscoveryAdapter {

	private static final ServiceScanner SCANNER = new ServiceScanner();

	public static ServiceRecord find(final RemoteDevice device, String serviceName) throws IOException {
		List<ServiceRecord> services = list(device);
		if (services.isEmpty()) {
			throw new IllegalStateException(String.format("There is no service called \"%s\" running on device %s", serviceName, device.getBluetoothAddress()));
		}
		for (ServiceRecord service : services) {
			String name = getName(service);
			if (serviceName.equals(name)) {
				return service;
			}
		}
		throw new IllegalStateException(String.format("Service \"%s\" not found on device %s", serviceName, device.getBluetoothAddress()));
	}

	public static String getName(ServiceRecord service) {
		String name = (String) service.getAttributeValue(Bluetooth.ATTRIBUTE_SERVICE_NAME).getValue();
		return name.trim();
	}

	public static List<ServiceRecord> list(final RemoteDevice device) throws IOException {
		return SCANNER.listServices(device);
	}

	private final Object LOCK = new Object();

	private final List<ServiceRecord> services = new ArrayList<>();

	private ServiceScanner() {}

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

	private List<ServiceRecord> listServices(final RemoteDevice device) throws BluetoothStateException {
		try {
			synchronized (LOCK) {
				services.clear();
				int[] attributes = new int[] { Bluetooth.ATTRIBUTE_SERVICE_NAME };
				UUID[] uuids = new UUID[] { Bluetooth.UUID_SERIAL_PORT_PROFILE };
				int transactionID = LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attributes, uuids, device, this);
				if (transactionID > 0) {
					LOCK.wait();
				}
				return services;
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}

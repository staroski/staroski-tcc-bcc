package br.com.staroski.obdjrp.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

final class Services implements DiscoveryListener {

	public static final short ATTRIBUTE_SERVICE_NAME = 0x0100;
	public static final short UUID_SERIAL_PORT_PROFILE = 0x1101;

	private static final Services SCANNER = new Services();

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
		String name = (String) service.getAttributeValue(ATTRIBUTE_SERVICE_NAME).getValue();
		return name.trim();
	}

	public static List<ServiceRecord> list(final RemoteDevice device) throws IOException {
		return showServices(SCANNER.listServices(device));
	}

	private static List<ServiceRecord> showServices(List<ServiceRecord> services) {
		if (services.isEmpty()) {
			System.out.println("no services found!");
			return services;
		}
		System.out.println("services {");
		for (ServiceRecord service : services) {
			String address = service.getHostDevice().getBluetoothAddress();
			System.out.println("    " + address + " {");
			String name = getName(service);
			System.out.println("        name: \"" + name + "\"");
			System.out.println("    }");
		}
		System.out.println("}");
		return services;
	}

	private final Object LOCK = new Object();

	private final List<ServiceRecord> services = new ArrayList<>();

	private Services() {}

	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {}

	@Override
	public void inquiryCompleted(int discType) {}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for (ServiceRecord service : servRecord) {
			services.add(service);
		}
	}

	public void serviceSearchCompleted(int transID, int respCode) {
		synchronized (LOCK) {
			LOCK.notifyAll();
		}
	}

	private List<ServiceRecord> listServices(final RemoteDevice device) throws BluetoothStateException {
		try {
			synchronized (LOCK) {
				services.clear();
				int[] attributes = new int[] { ATTRIBUTE_SERVICE_NAME };
				UUID[] uuids = new UUID[] { BaseUUID.merge16bits(UUID_SERIAL_PORT_PROFILE) };
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
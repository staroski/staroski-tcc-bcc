package br.com.staroski.obdjrp.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

final class Devices implements DiscoveryListener {

	private static final Devices SCANNER = new Devices();

	public static RemoteDevice find(String deviceAddress) throws IOException {
		List<RemoteDevice> devices = list();
		if (devices.isEmpty()) {
			throw new IllegalStateException(String.format("No paired devices found!"));
		}
		for (RemoteDevice device : devices) {
			if (deviceAddress.equals(device.getBluetoothAddress())) {
				return device;
			}
		}
		throw new IllegalStateException(String.format("Device %s not paired!", deviceAddress));
	}

	public static List<RemoteDevice> list() throws IOException {
		return showDevices(SCANNER.listDevices());
	}

	private static List<RemoteDevice> showDevices(List<RemoteDevice> devices) {
		if (devices.isEmpty()) {
			System.out.println("no devices found!");
			return devices;
		}
		System.out.println("devices {");
		for (RemoteDevice device : devices) {
			try {
				String address = device.getBluetoothAddress();
				String name = device.getFriendlyName(false);
				System.out.println("    " + address + " {");
				System.out.println("        name: \"" + name + "\"");
				System.out.println("    }");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("}");
		return devices;
	}

	private final Object LOCK = new Object();

	private final List<RemoteDevice> devices = new ArrayList<>();

	private Devices() {}

	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		devices.add(btDevice);
	}

	public void inquiryCompleted(int discType) {
		synchronized (LOCK) {
			LOCK.notifyAll();
		}
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		// ignorar, este listener só descobre dispositivos
	}

	public void serviceSearchCompleted(int transID, int respCode) {
		// ignorar, este listener só descobre dispositivos
	}

	private List<RemoteDevice> listDevices() throws IOException {
		try {
			synchronized (LOCK) {
				devices.clear();
				DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
				boolean started = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
				if (started) {
					LOCK.wait();
				}
				return devices;
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}

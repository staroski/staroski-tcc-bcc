package br.com.staroski.obdjrp.io.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

final class DeviceScanner extends DiscoveryAdapter {

	private static final DeviceScanner SCANNER = new DeviceScanner();

	public static RemoteDevice find(String deviceAddress) throws IOException {
		List<RemoteDevice> devices = list();
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

	public static List<RemoteDevice> list() throws IOException {
		return SCANNER.listDevices();
	}

	private final Object LOCK = new Object();

	private final List<RemoteDevice> devices = new ArrayList<>();

	private DeviceScanner() {}

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

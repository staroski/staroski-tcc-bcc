package br.com.staroski.obd2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

final class Bluetooth implements IO {

	/**
	 * The Base UUID is used for calculating 128-bit UUIDs from "short UUIDs" (uuid16 and uuid32) as described in the SDP Specification. See Service Discovery Protocol (SDP) in the Bluetooth Core
	 * Specification<BR>
	 * See <a href= "https://www.bluetooth.com/specifications/assigned-numbers/service-discovery"> Service Discovery</a>
	 */
	public static final String BASE_UUID = "0000000000001000800000805F9B34FB";
	public static final short UUID_SERIAL_PORT_PROFILE = 0x1101;

	public static final short ATTRIBUTE_SERVICE_NAME = 0x0100;

	private static String hexa(int value, int digits) {
		StringBuilder hexa = new StringBuilder(Integer.toHexString(value));
		while (hexa.length() < digits) {
			hexa.insert(0, '0');
		}
		return hexa.toString();
	}

	/**
	 * A 16-bit Attribute UUID replaces the x’s in the following:
	 * 
	 * <pre>
	 * 00000000-0000-1000-8000-00805F9B34FB // BASE UUID
	 * 0000xxxx-0000-1000-8000-00805F9B34FB
	 * </pre>
	 * 
	 * @param uuid32
	 * @return
	 */
	public static String uuid16to128(short uuid16) {
		return String.format("0000%s00001000800000805F9B34FB", hexa(uuid16, 4));
	}

	/**
	 * A 32-bit Attribute UUID replaces the x's in the following:
	 * 
	 * <pre>
	 * 00000000-0000-1000-8000-00805F9B34FB // BASE UUID
	 * xxxxxxxx-0000-1000-8000-00805F9B34FB
	 * </pre>
	 * 
	 * @param uuid32
	 * @return
	 */
	public static String uuid32to128(int uuid32) {
		return String.format("%s00001000800000805F9B34FB", hexa(uuid32, 8));
	}

	private final InputStream input;

	private final OutputStream output;

	Bluetooth(String deviceAddress, String serviceName) throws IOException {
		Object connection = connect(deviceAddress, serviceName);
		input = ((InputConnection) connection).openInputStream();
		output = ((OutputConnection) connection).openOutputStream();
	}

	@Override
	public void close() throws IOException {
		input.close();
		output.close();
	}

	private Object connect(String deviceAddress, String serviceName) throws IOException {
		List<RemoteDevice> devices = listDevices();
		if (devices.isEmpty()) {
			throw new IllegalStateException(String.format("No paired devices found!"));
		}
		RemoteDevice foundDevice = null;
		for (RemoteDevice device : devices) {
			if (deviceAddress.equals(device.getBluetoothAddress())) {
				foundDevice = device;
				break;
			}
		}
		if (foundDevice == null) {
			throw new IllegalStateException(String.format("Device %s not paired!", deviceAddress));
		}

		List<ServiceRecord> services = listServices(foundDevice);
		if (services.isEmpty()) {
			throw new IllegalStateException(String.format("There is no OBD2 service running on device %s", deviceAddress));
		}
		String url = null;
		for (ServiceRecord service : services) {
			if (serviceName.equals(service.getAttributeValue(ATTRIBUTE_SERVICE_NAME).getValue())) {
				url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				break;
			}
		}
		if (url == null) {
			throw new IllegalStateException(String.format("Service \"%s\" not found on device %s", serviceName, deviceAddress));
		}
		return Connector.open(url);
	}

	private DiscoveryAgent getDiscoveryAgent() throws IOException {
		LocalDevice device = LocalDevice.getLocalDevice();
		return device.getDiscoveryAgent();
	}

	private List<RemoteDevice> listDevices() throws IOException {
		try {
			final List<RemoteDevice> devicesDiscovered = new ArrayList<>();
			final Object LOCK = new Object();
			synchronized (LOCK) {
				DiscoveryAgent discoveryAgent = getDiscoveryAgent();
				boolean started = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {

					public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
						devicesDiscovered.add(btDevice);
					}

					public void inquiryCompleted(int discType) {
						synchronized (LOCK) {
							LOCK.notifyAll();
						}
					}

					public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {}

					public void serviceSearchCompleted(int transID, int respCode) {}
				});
				if (started) {
					LOCK.wait();
				}
			}
			showDevices(devicesDiscovered);
			return devicesDiscovered;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private List<ServiceRecord> listServices(final RemoteDevice device) throws IOException {
		try {
			final List<ServiceRecord> services = new ArrayList<>();
			final Object LOCK = new Object();
			synchronized (LOCK) {
				int[] attrSet = new int[] { ATTRIBUTE_SERVICE_NAME };
				UUID[] uuidSet = new UUID[] { new UUID(uuid16to128(UUID_SERIAL_PORT_PROFILE), false) };
				int transactionID = getDiscoveryAgent().searchServices(attrSet, uuidSet, device, new DiscoveryListener() {

					@Override
					public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {}

					@Override
					public void inquiryCompleted(int discType) {}

					public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
						for (ServiceRecord service : servRecord) {
							if (service.getHostDevice().equals(device)) {
								services.add(service);
							}
						}
					}

					public void serviceSearchCompleted(int transID, int respCode) {
						synchronized (LOCK) {
							LOCK.notifyAll();
						}
					}
				});
				if (transactionID > 0) {
					LOCK.wait();
				}
				showServices(services);
				return services;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] read() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		int value = -1;
		while ((value = input.read()) != -1) {
			bytes.write(value);
			if (value == '>') {
				break;
			}
		}
		return bytes.toByteArray();
	}

	private void showDevices(List<RemoteDevice> devices) {
		if (devices.isEmpty()) {
			System.out.println("no devices found!");
			return;
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
	}

	private void showServices(List<ServiceRecord> services) {
		if (services.isEmpty()) {
			System.out.println("no services found!");
			return;
		}
		System.out.println("services {");
		for (ServiceRecord service : services) {
			String address = service.getHostDevice().getBluetoothAddress();
			System.out.println("    " + address + " {");
			String name = (String) service.getAttributeValue(ATTRIBUTE_SERVICE_NAME).getValue();
			System.out.println("        name: \"" + name + "\"");
			System.out.println("    }");
		}
		System.out.println("}");
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		output.write(bytes);
	}
}

package br.com.staroski.obd2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

	Bluetooth(String address) throws IOException {
		Object conn = openConnection(address);
		input = ((InputConnection) conn).openInputStream();
		output = ((OutputConnection) conn).openOutputStream();
	}

	private Object openConnection(String address) throws IOException {
		RemoteDevice[] devices = getDiscoveryAgent().retrieveDevices(DiscoveryAgent.CACHED);
		if (devices == null) {
			devices = getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
		}
		if (devices == null) {
			throw new IllegalStateException(String.format("No paired devices found!"));
		}
		RemoteDevice foundDevice = null;
		for (RemoteDevice device : devices) {
			if (address.equalsIgnoreCase(device.getBluetoothAddress())) {
				foundDevice = device;
				break;
			}
		}
		if (foundDevice == null) {
			throw new IllegalStateException(String.format("Device %s not paired!", address));
		}

		List<ServiceRecord> services = listServices(foundDevice);
		if (services.isEmpty()) {
			throw new IllegalStateException(String.format("There is no OBD2 service running on device %s", address));
		}
		ServiceRecord service = services.get(0);
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		return Connector.open(url);
	}

	@Override
	public void close() throws IOException {
		input.close();
		output.close();
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

	@Override
	public void write(byte[] bytes) throws IOException {
		output.write(bytes);
		output.flush();
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
			return devicesDiscovered;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private List<ServiceRecord> listServices(RemoteDevice device) throws IOException {
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
						services.addAll(Arrays.asList(servRecord));
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
				return services;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}

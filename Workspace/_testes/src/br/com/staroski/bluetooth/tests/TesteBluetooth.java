package br.com.staroski.bluetooth.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
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

public class TesteBluetooth {

	static final String MOTO_G_STAROSKI = "F8E079DAE781";

	static final int SERVICE_NAME = 0x0100;

	// 0001101-0000-1000-8000-00805F9B34FB
	static final UUID ELM_327_UUID = new UUID("000110100001000800000805F9B34FB", false);

	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			TesteBluetooth testeBluetooth = new TesteBluetooth();
			testeBluetooth.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private final DiscoveryAgent discoveryAgent;

	public TesteBluetooth() throws BluetoothStateException {
		LocalDevice device = LocalDevice.getLocalDevice();
		discoveryAgent = device.getDiscoveryAgent();
	}

	public void execute() throws IOException {
		System.out.println("buscando dispositivos...");
		List<RemoteDevice> devices = listDevices();
		System.out.println(devices.size() + " dispositivo(s) encontrado(s)");
		for (RemoteDevice device : devices) {
			showDevice(device);
			if (MOTO_G_STAROSKI.equals(device.getBluetoothAddress())) {
				connectTo(device);
			}
		}
	}

	private void connectTo(RemoteDevice device) throws IOException {
		System.out.println("buscando servicos...");
		List<ServiceRecord> services = listServices(device);
		System.out.println(services.size() + " servico(s) encontrado(s)");
		for (ServiceRecord service : services) {
			String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			if (url == null) {
				continue;
			}
			System.out.println("URL:  " + url);
			System.out.println("Name: " + service.getAttributeValue(SERVICE_NAME));

			readObdData(url);
		}

	}

	private List<RemoteDevice> listDevices() throws BluetoothStateException {
		try {
			final List<RemoteDevice> devicesDiscovered = new ArrayList<>();
			final Object LOCK = new Object();
			synchronized (LOCK) {
				boolean started = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {

					public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
						devicesDiscovered.add(btDevice);
					}

					public void inquiryCompleted(int discType) {
						synchronized (LOCK) {
							LOCK.notifyAll();
						}
					}

					public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
					}

					public void serviceSearchCompleted(int transID, int respCode) {
					}
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

	private List<ServiceRecord> listServices(RemoteDevice device) throws BluetoothStateException {
		try {
			final List<ServiceRecord> services = new ArrayList<>();
			final Object LOCK = new Object();
			synchronized (LOCK) {
				int transactionID = discoveryAgent.searchServices(new int[] { SERVICE_NAME }, new UUID[] { ELM_327_UUID }, device, new DiscoveryListener() {

					@Override
					public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
					}

					@Override
					public void inquiryCompleted(int discType) {
					}

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

	/**
	 * 
	 * Set the Protocol Let’s set the protocol to “AUTO”, which means that you want the interface to automatically detect the protocol when you send the first OBD request. To do this, enter the “AT SP
	 * 0” command:
	 * 
	 * <pre>
	>AT SP 0
	OK
	 * </pre>
	 * 
	 * To verify the protocol, enter the AT DP command (“Display Protocol”):
	 * 
	 * <pre>
	>AT DP
	AUTO
	 * </pre>
	 * 
	 * Get RPM Now it is time to send our first OBD request. Real-time parameters are accessed through Mode 1 (also called “Service $01”), and each parameter has a Parameter ID, or PID for short.
	 * RPM’s PID is 0C, so we must tell the interface to send “010C”:
	 * 
	 * <pre>
	>010C
	SEARCHING: OK
	41 0C 0F A0
	 * </pre>
	 * 
	 * The reply contains two bytes that identify it as a response to Mode 1, PID 0C request (41 0C), and two more bytes with the encoded RPM value (1/4 RPM per bit). To get the actual RPM value,
	 * convert the hex number to decimal, and divide it by four:
	 * 
	 * <pre>
	0x0FA0 = 4000
	4000 / 4 = 1000 rpm
	 * </pre>
	 * 
	 * @throws IOException
	 *
	 */
	private void readObdData(String url) throws IOException {
		try {
			Object conn = Connector.open(url);
			OutputStream output = ((OutputConnection) conn).openOutputStream();
			InputStream input = ((InputConnection) conn).openInputStream();
			byte[] buffer = "010D\r".getBytes();
			output.write(buffer);
			output.flush();
			for (int n = -1; (n = input.read()) != -1;) {
				System.out.print(new String(new byte[] { (byte) n }));
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showDevice(RemoteDevice device) {
		try {
			String address = device.getBluetoothAddress();
			String name = device.getFriendlyName(false);
			System.out.println("nome:     " + name);
			System.out.println("endereço: " + address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
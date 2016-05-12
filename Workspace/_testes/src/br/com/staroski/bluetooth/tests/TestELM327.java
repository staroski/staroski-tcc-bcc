package br.com.staroski.bluetooth.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import br.com.staroski.bluetooth.Bluetooth;
import br.com.staroski.bluetooth.DeviceFilter;
import br.com.staroski.bluetooth.ServiceCriteria;

public class TestELM327 {

	static final DeviceFilter MOTO_G_STAROSKI = new DeviceFilter() {

		@Override
		public boolean accept(RemoteDevice device) {
			return "F8E079DAE781".equals(device.getBluetoothAddress());
		}
	};

	static final int ATTRIBUTE = 0x0100;

	// 0001101-0000-1000-8000-00805F9B34FB
	static final UUID ELM_327_UUID = new UUID("000110100001000800000805F9B34FB", false);

	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			TestELM327 testeBluetooth = new TestELM327();
			testeBluetooth.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public TestELM327() throws BluetoothStateException {}

	public void execute() throws IOException {
		System.out.println("buscando dispositivo...");
		RemoteDevice device = Bluetooth.get.device(MOTO_G_STAROSKI);
		showDevice(device);
		connectTo(device);
	}

	private void connectTo(RemoteDevice device) throws IOException {
		System.out.println("buscando servico...");
		ServiceCriteria criteria = ServiceCriteria.create(ATTRIBUTE, ELM_327_UUID, device);
		ServiceRecord service = Bluetooth.get.service(criteria);
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		if (url == null) {
			return;
		}
		System.out.println("URL:  " + url);
		System.out.println("Name: " + service.getAttributeValue(ATTRIBUTE));
		readObdData(url);
	}

	/**
	 * 
	 * Set the Protocol Let’s set the protocol to “AUTO”, which means that you want the interface to automatically detect the protocol when you send the first OBD request. To do this, enter the “AT SP 0” command:
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
	 * Get RPM Now it is time to send our first OBD request. Real-time parameters are accessed through Mode 1 (also called “Service $01”), and each parameter has a Parameter ID, or PID for short. RPM’s PID is 0C, so we must tell the
	 * interface to send “010C”:
	 * 
	 * <pre>
	>010C
	SEARCHING: OK
	41 0C 0F A0
	 * </pre>
	 * 
	 * The reply contains two bytes that identify it as a response to Mode 1, PID 0C request (41 0C), and two more bytes with the encoded RPM value (1/4 RPM per bit). To get the actual RPM value, convert the hex number to decimal, and
	 * divide it by four:
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
			System.out.println("enviando para ELM327:");
			System.out.println("01 0D");

			output.write(buffer);
			output.flush();

			System.out.println("recebeu do ELM327:");
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
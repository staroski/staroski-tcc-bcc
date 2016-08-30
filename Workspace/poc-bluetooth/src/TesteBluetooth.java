
import java.io.IOException;
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

public class TesteBluetooth {
/*
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
			if (MOTO_G_STAROSKI.equals(device.getBluetoothAddress())) {
				System.out.println("conectando");
				showDevice(device);
				connectTo(device);
				System.out.println("conexao concluida com sucesso!");
				return;
			}
		}
		System.out.println("nao foi encontrado nenhum adapdator obd2");
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
				int transactionID = discoveryAgent.searchServices(new int[] { SERVICE_NAME },
						new UUID[] { ELM_327_UUID }, device, new DiscoveryListener() {

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

	private void readObdData(String url) throws IOException {
		try {
			Elm327 elm327 = getElm327(url);

			System.out.println("definindo protocolo ISO 15765-4 CAN");
			String result = elm327.send("ATSP6");
			System.out.println(result);

			System.out.println("requisitando PIDs suportados [01-20]");
			result = elm327.send("0100");
			System.out.println(result);

			System.out.println("requisitando velocidade do veiculo");
			result = elm327.send("010D");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Elm327 elm327;

	public Elm327 getElm327(String url) throws IOException {
		if (elm327 == null) {
			elm327 = Elm327.connect(url);
		}
		return elm327;
	}

	// private void readObdData(String url) throws IOException {
	// try {
	// Object conn = Connector.open(url);
	// OutputStream output = ((OutputConnection) conn).openOutputStream();
	// InputStream input = ((InputConnection) conn).openInputStream();
	// byte[] buffer = "0100\r".getBytes();
	// output.write(buffer);
	// output.flush();
	// int bytes = 0;
	// for (int n = -1; (n = input.read()) != '>';) {
	// bytes++;
	// System.out.print(new String(new byte[] { (byte) n }));
	// if (bytes == 11) {
	// break;
	// }
	// }
	// System.out.println();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private void showDevice(RemoteDevice device) {
		try {
			String address = device.getBluetoothAddress();
			String name = device.getFriendlyName(false);
			System.out.println("nome:     " + name);
			System.out.println("endereco: " + address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
}
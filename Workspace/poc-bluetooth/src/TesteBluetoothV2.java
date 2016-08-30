
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import br.com.staroski.obd2.Elm327;
import br.com.staroski.obd2.IO;

public class TesteBluetoothV2 {

	static final String MOTO_G_STAROSKI = "F8E079DAE781";

	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			TesteBluetoothV2 testeBluetooth = new TesteBluetoothV2();
			testeBluetooth.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private Elm327 elm327;

	// public void execute() throws IOException {
	// System.out.println("buscando dispositivos...");
	// List<RemoteDevice> devices = listDevices();
	// System.out.println(devices.size() + " dispositivo(s) encontrado(s)");
	// for (RemoteDevice device : devices) {
	// if (MOTO_G_STAROSKI.equals(device.getBluetoothAddress())) {
	// System.out.println("conectando");
	// showDevice(device);
	// connectTo(device);
	// System.out.println("conexao concluida com sucesso!");
	// return;
	// }
	// }
	// System.out.println("nao foi encontrado nenhum adapdator obd2");
	// }

	public void execute() throws IOException {
		readObdData();
	}

	public Elm327 getElm327() throws IOException {
		if (elm327 == null) {
			IO io = IO.factory.bluetooth("F8E079DAE781", "BLT\0");
			elm327 = new Elm327(io);
		}
		return elm327;
	}

	// private void connectTo(RemoteDevice device) throws IOException {
	// System.out.println("buscando servicos...");
	// List<ServiceRecord> services = listServices(device);
	// System.out.println(services.size() + " servico(s) encontrado(s)");
	// for (ServiceRecord service : services) {
	// String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
	// if (url == null) {
	// continue;
	// }
	// System.out.println("URL: " + url);
	// System.out.println("Name: " + service.getAttributeValue(SERVICE_NAME));
	// }
	// }

	private void readObdData() throws IOException {
		try {
			Elm327 elm327 = getElm327();

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
}
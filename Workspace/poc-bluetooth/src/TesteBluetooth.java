
import java.io.IOException;

import br.com.staroski.obdjrp.elm327.Elm327;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

public class TesteBluetooth {

	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			TesteBluetooth program = new TesteBluetooth();
			program.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void execute() throws IOException {
		try {
			String deviceAddress = "F8E079DAE781";
			// String serviceName = "OBD_Listener";
			String serviceName = "BLT";
			IO connection = Bluetooth.connect(deviceAddress, serviceName);

			Elm327 elm327 = new Elm327(connection);

			System.out.println("configurando protocolo automatico"); // ISO 15765-4 CAN == 6
			String result = elm327.exec("AT SP 0");
			System.out.println(result);

			System.out.println("desligando retorno dos cabeçalhos");
			result = elm327.exec("AT H0");
			System.out.println(result);

			System.out.println("requisitando PIDs suportados [01-20]");
			result = elm327.exec("01 00");
			System.out.println(result);
			while (true) {

				System.out.println("requisitando rpm do veiculo");
				result = elm327.exec("01 0C");
				System.out.println(result);

				System.out.println("requisitando velocidade do veiculo");
				result = elm327.exec("01 0D");
				System.out.println(result);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
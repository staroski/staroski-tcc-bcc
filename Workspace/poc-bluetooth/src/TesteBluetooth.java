
import java.io.IOException;

import br.com.staroski.obd2.Connection;
import br.com.staroski.obd2.Elm327;

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
			// String serviceName = "OBD_Listener\0";
			String serviceName = "BLT\0";
			Connection connection = Connection.open.bluetooth(deviceAddress, serviceName);

			Elm327 elm327 = new Elm327(connection);

			System.out.println("configurando protocolo ISO 15765-4 CAN");
			String result = elm327.send("ATSP6\r");
			System.out.println(result);

			System.out.println("configurando retorno dos cabeçalhos");
			result = elm327.send("ATH1\r");
			System.out.println(result);

			System.out.println("requisitando PIDs suportados [01-20]");
			result = elm327.send("0100\r");
			System.out.println(result);
			while (true) {

				System.out.println("requisitando rpm do veiculo");
				result = elm327.send("010C\r");
				System.out.println(result);

				System.out.println("requisitando velocidade do veiculo");
				result = elm327.send("010D\r");
				System.out.println(result);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
import java.io.RandomAccessFile;
import java.util.Properties;

public class TesteArquivo {

	public static void main(String[] args) {
		try {
			new TesteArquivo().execute();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private String getPortName() {
		if (isWindows()) {
			return "COM7";
		} else {
			// /dev/ttyAMA0 -> Bluetooth
			// /dev/ttyS0 -> GPIO serial port.
			return "/dev/ttyAMA0";
		}
	}

	private boolean isWindows() {
		Properties props = System.getProperties();
		return props.get("os.name").toString().toLowerCase().contains("windows");
	}

	private void execute() {
		try {
			String portName = getPortName();
			System.out.println("Tentando enviar dados para \"" + portName + "\"");
			RandomAccessFile file = new RandomAccessFile(portName, "rw");
			byte[] buffer = "010D\r".getBytes();
			file.write(buffer);
			int bytes = 0;
			for (int n = -1; (n = file.read()) != '>';) {
				bytes++;
				System.out.print(new String(new byte[] { (byte) n }));
				if (bytes == 11) {
					break;
				}
			}
			System.out.println();
			file.close();
			System.out.println("Dados enviados e recebidos com sucesso!");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

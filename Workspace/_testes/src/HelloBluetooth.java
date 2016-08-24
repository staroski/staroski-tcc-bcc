import jssc.SerialPortList;

public class HelloBluetooth {

	public static void main(String[] args) {
		try {
			HelloBluetooth program = new HelloBluetooth();
			program.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private void execute() {
		String[] portNames = SerialPortList.getPortNames();
		for (String port : portNames) {
			System.out.println(port);
		}
	}
}

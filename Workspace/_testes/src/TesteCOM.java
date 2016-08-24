import java.io.IOException;
import java.io.RandomAccessFile;

public class TesteCOM {

	public static void main(String[] args) {
		try {
			new TesteCOM().execute();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private void execute() throws IOException {
		RandomAccessFile file = new RandomAccessFile("COM7", "rw");
		byte[] buffer = "010D\r".getBytes();
		file.write(buffer);
		int bytes = 0;
		for (int n = -1; (n = file.read()) != -1;) {
			bytes++;
			System.out.print(new String(new byte[] { (byte) n }));
			if (bytes == 11) {
				break;
			}
		}
		System.out.println();
		file.close();
	}
}

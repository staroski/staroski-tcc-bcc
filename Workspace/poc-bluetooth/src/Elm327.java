import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

public class Elm327 {

	public static Elm327 connect(String url) throws IOException {
		Object conn = Connector.open(url);
		InputStream in = ((InputConnection) conn).openInputStream();
		OutputStream out = ((OutputConnection) conn).openOutputStream();
		return new Elm327(in, out);
	}

	private final InputStream in;
	private final OutputStream out;

	private Elm327(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public String send(String command) throws IOException {
		byte[] bytes = (command + "\r").getBytes();
		out.write(bytes);
		StringBuilder text = new StringBuilder();
		for (int n = -1; (n = in.read()) != '>';) {
			text.append(new String(new byte[] { (byte) n }));
		}
		return text.toString().trim();
	}
}

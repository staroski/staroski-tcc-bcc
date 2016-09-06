package br.com.staroski.obdjrp.elm327;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import br.com.staroski.obdjrp.io.IO;

public final class Elm327 {

	private static final char PROMPT = '>';
	private static final char RETURN = '\r';

	private static String bytesToText(byte[] bytes) {
		String text = new String(bytes);
		text = text.trim();
		return text;
	}

	private static byte[] textToBytes(String command) {
		command = command.trim();
		if (command.charAt(command.length() - 1) != RETURN) {
			command += RETURN;
		}
		return command.getBytes();
	}

	private final IO io;

	public Elm327(IO connection) {
		this.io = connection;
	}

	public String exec(String command) throws IOException {
		byte[] buffer = textToBytes(command);
		io.write(buffer, 0, buffer.length);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		buffer = createReadBuffer();
		int read = -1;
		while ((read = io.read(buffer, 0, buffer.length)) != -1) {
			if (read > 0) {
				bytes.write(buffer, 0, read);
				if (buffer[read - 1] == PROMPT) {
					break;
				}
			}
			buffer = createReadBuffer();
		}
		buffer = bytes.toByteArray();
		return bytesToText(buffer);
	}

	private byte[] createReadBuffer() throws IOException {
		int max = io.available();
		if (max < 1) {
			max = 1;
		}
		return new byte[max];
	}
}

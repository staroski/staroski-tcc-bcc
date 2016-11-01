package br.com.staroski.obdjrp;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.staroski.obdjrp.io.IO;

final class ELM327 {

	public static final char PROMPT = '>';
	public static final char RETURN = '\r';

	private static String bytesToText(byte[] bytes) {
		String text = new String(bytes);
		text = text.trim();
		return text;
	}

	private static String checkReturn(String command) {
		command = command.trim();
		if (command.charAt(command.length() - 1) != RETURN) {
			command += RETURN;
		}
		return command;
	}

	private final IO io;

	private final PrintStream logFile;

	public ELM327(IO connection, boolean createLofFile) throws IOException {
		this.io = connection;
		logFile = createLogFile(createLofFile);
	}

	public String exec(String command) throws IOException {
		command = checkReturn(command);
		log("command:%n\"%s\"%n", command);
		byte[] buffer = command.getBytes();
		io.write(buffer, 0, buffer.length);
		io.flush();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		buffer = updateBuffer(buffer);
		int read = -1;
		while ((read = io.read(buffer, 0, buffer.length)) != -1) {
			if (read > 0) {
				bytes.write(buffer, 0, read);
				if (buffer[read - 1] == PROMPT) {
					break;
				}
			}
			buffer = updateBuffer(buffer);
		}
		buffer = bytes.toByteArray();
		String respose = bytesToText(buffer);
		log("response:%n\"%s\"%n", respose);
		return respose;
	}

	private PrintStream createLogFile(boolean create) throws IOException {
		if (create) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String instant = formatter.format(new Date());
			String name = getClass().getSimpleName();
			FileOutputStream output = new FileOutputStream(name + "_" + instant + ".log");
			return new PrintStream(output);
		}
		return System.out;
	}

	private void log(String format, String value) {
		logFile.printf(format, value.replaceAll("\r", "\\r"));
	}

	private byte[] updateBuffer(byte[] buffer) throws IOException {
		int max = io.available();
		if (max < 1) {
			max = 1;
		}
		if (buffer.length < max) {
			buffer = new byte[max];
		}
		return buffer;
	}
}

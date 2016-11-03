package br.com.staroski.obdjrp.elm;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.staroski.obdjrp.io.IO;

public final class ELM327 {

	public static final char PROMPT = '>';
	public static final char RETURN = '\r';

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private static String checkError(String response) throws ELM327Error {
		ELM327Error error = ELM327Error.findError(response);
		if (error != null) {
			throw error;
		}
		return response;
	}

	private static String prepareCommand(String command) {
		command = command.trim();
		if (command.charAt(command.length() - 1) != RETURN) {
			return command + RETURN;
		}
		return command;
	}

	private static String prepareResponse(byte[] bytes) {
		String text = new String(bytes);
		text = text.trim();
		return text;
	}

	private final IO io;

	private final PrintStream log;

	public ELM327(IO io, boolean createLofFile) throws IOException {
		this.io = io;
		log = createLogStream(createLofFile);
		Disconnector.add(this);
	}

	public void disconnect() {
		io.close();
		Disconnector.remove(this);
	}

	public String execute(String command) throws ELM327Error, IOException {
		command = prepareCommand(command);
		printLog("command:%n\"%s\"%n", command);
		byte[] bytes = send(command.getBytes());
		String response = prepareResponse(bytes);
		printLog("response:%n\"%s\"%n%n", response);
		return checkError(response);
	}

	private PrintStream createLogStream(boolean create) throws IOException {
		if (create) {
			String instant = DATE_FORMAT.format(new Date());
			String name = getClass().getSimpleName();
			FileOutputStream output = new FileOutputStream(name + "_" + instant + ".log");
			return new PrintStream(output);
		}
		return System.out;
	}

	private void printLog(String format, String value) {
		log.printf(format, value.replaceAll("\r", "\\r"));
	}

	private byte[] send(byte[] buffer) throws IOException {
		synchronized (io.out) {
			io.out.write(buffer);
			io.out.flush();
		}
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		synchronized (io.in) {
			int read = -1;
			while ((read = io.in.read()) != -1) {
				bytes.write(read);
				if (read == PROMPT) {
					break;
				}
			}
		}
		return bytes.toByteArray();
	}
}

package br.com.staroski.obdjrp.elm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import br.com.staroski.obdjrp.io.IO;

public final class ELM327 {

	public static final char PROMPT = '>';
	public static final char RETURN = '\r';

	private static String checkError(String response) throws ELM327Error {
		ELM327Error error = ELM327Error.findError(response);
		if (error != null) {
			throw error;
		}
		return response;
	}

	private static <T> T checkParam(Class<T> type, T instance) {
		if (instance == null) {
			throw new IllegalArgumentException(type.getSimpleName() + " is null");
		}
		return instance;
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

	public ELM327(IO io) throws IOException {
		this(io, System.out);
	}

	public ELM327(IO io, PrintStream log) throws IOException {
		this.io = checkParam(IO.class, io);
		this.log = checkParam(PrintStream.class, log);
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

	private void printLog(String format, String value) {
		log.printf(format, value.replaceAll("\r", "\\\\r"));
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

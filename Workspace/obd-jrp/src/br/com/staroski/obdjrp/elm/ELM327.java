package br.com.staroski.obdjrp.elm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import br.com.staroski.obdjrp.ObdJrpConnection;

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

	private final ObdJrpConnection connection;
	private final PrintStream logger;

	public ELM327(ObdJrpConnection connection) throws IOException {
		this(connection, System.out);
	}

	public ELM327(ObdJrpConnection connection, PrintStream logger) throws IOException {
		this.connection = checkParam(ObdJrpConnection.class, connection);
		this.logger = checkParam(PrintStream.class, logger);
		Disconnector.add(this);
	}

	public void disconnect() {
		connection.close();
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
		logger.printf(format, value.replaceAll("\r", "\\\\r"));
	}

	private byte[] send(byte[] command) throws IOException {
		final ByteArrayOutputStream response = new ByteArrayOutputStream();
		final OutputStream out = connection.getOutput();
		final InputStream in = connection.getInput();
		synchronized (out) {
			synchronized (in) {
				out.write(command);
				out.flush();
				int read = -1;
				while ((read = in.read()) != -1) {
					response.write(read);
					if (read == PROMPT) {
						break;
					}
				}
			}
		}
		return response.toByteArray();
	}
}

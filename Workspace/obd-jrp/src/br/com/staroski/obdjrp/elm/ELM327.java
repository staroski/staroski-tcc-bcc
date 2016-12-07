package br.com.staroski.obdjrp.elm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import br.com.staroski.obdjrp.core.IO;

public final class ELM327 {

	private static final char PROMPT = '>';
	private static final char RETURN = '\r';

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

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(8192);

	private final PrintStream logger;
	private final IO connection;

	public ELM327(IO connection) throws IOException {
		this(connection, System.out);
	}

	public ELM327(IO connection, PrintStream logger) throws IOException {
		this.connection = checkParam(IO.class, connection);
		this.logger = checkParam(PrintStream.class, logger);
		Disconnector.add(this);
	}

	public void disconnect() {
		connection.close();
		Disconnector.remove(this);
	}

	public String execute(String command) throws ELM327Error {
		command = prepareCommand(command);
		byte[] bytes = command.getBytes();
		writeMessage(bytes);
		bytes = readMessage();
		String response = prepareResponse(bytes);
		return checkError(response);
	}

	private void log(String format, String value) {
		logger.printf(format, value.replaceAll("\r", "\\\\r"));
	}

	private String prepareCommand(String command) {
		command = command.trim();
		if (command.charAt(command.length() - 1) != RETURN) {
			command = command + RETURN;
		}
		log("command:%n\"%s\"%n", command);
		return command;
	}

	private String prepareResponse(byte[] bytes) {
		String text = new String(bytes);
		text = text.trim();
		log("response:%n\"%s\"%n%n", text);
		return text;
	}

	private byte[] readMessage() throws ELM327Error {
		final InputStream in = connection.getInput();
		try {
			buffer.reset();
			int read = -1;
			while (connection.isOpen()) {
				if ((read = in.read()) != -1) {
					buffer.write(read);
					if (read == PROMPT) {
						break;
					}
				}
			}
		} catch (ELM327Error elm327Error) {
			throw elm327Error;
		} catch (Throwable error) {
			throw ELM327Error.wrap(error);
		}
		return buffer.toByteArray();
	}

	private void writeMessage(byte[] command) throws ELM327Error {
		try {
			synchronized (connection) {
				buffer.reset();
				final OutputStream out = connection.getOutput();
				out.write(command);
				out.flush();
			}
		} catch (Throwable error) {
			throw ELM327Error.wrap(error);
		}
	}
}

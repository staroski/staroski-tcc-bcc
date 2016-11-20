package br.com.staroski.obdjrp.elm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import br.com.staroski.obdjrp.ObdJrpConnection;
import br.com.staroski.obdjrp.utils.Lock;

public final class ELM327 {

	private final class InputReader implements Runnable {

		@Override
		public void run() {
			final InputStream in = connection.getInput();
			try {
				int read = -1;
				while (connection.isOpen()) {
					if ((read = in.read()) != -1) {
						buffer.write(read);
						if (read == PROMPT) {
							LOCK.unlock();
						}
					}
				}
			} catch (Throwable error) {
				readError = error;
				LOCK.unlock();
			}
		}
	}

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

	private Throwable readError;

	private final Lock LOCK = new Lock();
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);

	private final PrintStream logger;
	private final ObdJrpConnection connection;

	public ELM327(ObdJrpConnection connection) throws IOException {
		this(connection, System.out);
	}

	public ELM327(ObdJrpConnection connection, PrintStream logger) throws IOException {
		this.connection = checkParam(ObdJrpConnection.class, connection);
		this.logger = checkParam(PrintStream.class, logger);
		Disconnector.add(this);

		new Thread(new InputReader(), "ELM327_InputReader").start();
	}

	public void disconnect() {
		connection.close();
		Disconnector.remove(this);
	}

	public String execute(String command) throws ELM327Error {
		command = prepareCommand(command);
		log("command:%n\"%s\"%n", command);

		byte[] bytes = command.getBytes();
		messageWrite(bytes);
		bytes = messageRead();

		String response = prepareResponse(bytes);
		log("response:%n\"%s\"%n%n", response);
		return checkError(response);
	}

	private void log(String format, String value) {
		logger.printf(format, value.replaceAll("\r", "\\\\r"));
	}

	private byte[] messageRead() throws ELM327Error {
		LOCK.lock(1000);
		if (readError != null) {
			try {
				throw ELM327Error.wrap(readError);
			} finally {
				readError = null;
			}
		}
		return buffer.toByteArray();
	}

	private void messageWrite(byte[] command) throws ELM327Error {
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

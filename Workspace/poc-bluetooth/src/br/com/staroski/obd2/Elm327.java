package br.com.staroski.obd2;

import java.io.IOException;

public final class Elm327 {

	private final Connection connection;

	public Elm327(Connection connection) {
		this.connection = connection;
	}

	public String send(String command) throws IOException {
		byte[] bytes = command.getBytes();
		connection.write(bytes);
		bytes = connection.read();
		return new String(bytes);
	}
}

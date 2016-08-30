package br.com.staroski.obd2;

import java.io.IOException;

public final class Elm327 {

	private final IO io;

	public Elm327(IO io) {
		this.io = io;
	}

	public String send(String command) throws IOException {
		byte[] bytes = (command + "\r").getBytes();
		io.write(bytes);
		return new String(io.read());
	}
}

package br.com.staroski.obdjrp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

public final class IO {

	private static <T> T checkParam(String name, T param) {
		if (param == null) {
			throw new IllegalArgumentException(name + " = null");
		}
		return param;
	}

	private Connection connection;
	public final InputStream in;
	public final OutputStream out;

	public IO(Connection connection) throws IOException {
		checkParam("connection", connection);
		this.connection = connection;
		this.in = ((InputConnection) connection).openInputStream();
		this.out = ((OutputConnection) connection).openOutputStream();
	}

	public void close() {
		try {
			in.close();
			out.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

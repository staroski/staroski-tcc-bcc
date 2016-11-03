package br.com.staroski.obdjrp.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IO implements Closeable {

	private static <T> T checkParam(String name, T param) {
		if (param == null) {
			throw new IllegalArgumentException(name + " = null");
		}
		return param;
	}

	public final InputStream in;
	public final OutputStream out;

	public IO(InputStream in, OutputStream out) {
		this.in = checkParam("in", in);
		this.out = checkParam("out", out);
	}

	@Override
	public void close() throws IOException {
		in.close();
		out.close();
	}

	public void closeIO() {
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

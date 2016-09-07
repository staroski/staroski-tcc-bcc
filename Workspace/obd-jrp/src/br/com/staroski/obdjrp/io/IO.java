package br.com.staroski.obdjrp.io;

import java.io.Closeable;
import java.io.IOException;

public interface IO extends Closeable {

	public int available() throws IOException;

	public void flush() throws IOException;

	public int read(byte[] buffer, int offset, int length) throws IOException;

	public void write(byte[] buffer, int offset, int length) throws IOException;
}

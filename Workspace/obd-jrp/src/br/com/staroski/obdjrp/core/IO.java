package br.com.staroski.obdjrp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IO {

	public IO close();

	public InputStream getInput();

	public OutputStream getOutput();

	public boolean isOpen();

	public IO open() throws IOException;
}

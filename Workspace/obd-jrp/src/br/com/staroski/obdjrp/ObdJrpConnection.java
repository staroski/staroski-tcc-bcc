package br.com.staroski.obdjrp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ObdJrpConnection {

	public ObdJrpConnection close();

	public InputStream getInput();

	public OutputStream getOutput();

	public boolean isOpen();

	public ObdJrpConnection open() throws IOException;
}

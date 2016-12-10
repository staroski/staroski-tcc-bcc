package br.com.staroski.obdjrp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

final class SocketIO implements IO {

	private final String address;
	private final int port;

	private boolean open;
	private Socket socket;
	private InputStream input;
	private OutputStream output;

	SocketIO(Config props) throws IOException {
		address = props.checkProperty(Config.SOCKET_ADDRESS);
		port = Integer.parseInt(props.checkProperty(Config.SOCKET_PORT));
	}

	@Override
	public IO close() {
		if (open) {
			try {
				input.close();
				output.close();
				socket.close();
				open = false;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return this;
	}

	@Override
	public InputStream getInput() {
		return input;
	}

	@Override
	public OutputStream getOutput() {
		return output;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public IO open() throws IOException {
		if (!open) {
			socket = new Socket(address, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
			open = true;
		}
		return this;
	}

	@Override
	public String toString() {
		return String.format("%s address: \"%s\" port: \"%d\"", //
				Config.SOCKET, //
				address, //
				port);
	}
}

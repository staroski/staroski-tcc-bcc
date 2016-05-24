package br.com.staroski.obdjrp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import br.com.staroski.bluetooth.Bluetooth;
import br.com.staroski.bluetooth.ServiceCriteria;

public class Elm327 {

	public static Elm327 connect(RemoteDevice device) throws IOException {
		final int attribute = 0x0100; // serviceName
		final UUID uuid = new UUID("0000110100001000800000805F9B34FB", false); // 00001101-0000-1000-8000-00805F9B34FB
		ServiceCriteria criteria = ServiceCriteria.create(attribute, uuid, device);
		ServiceRecord service = Bluetooth.get.service(criteria);
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		if (url == null) {
			return null;
		}

		return new Elm327(url);
	}

	private final OutputStream output;
	private final InputStream input;

	private Elm327(String url) throws IOException {
		Object conn = Connector.open(url);
		output = ((OutputConnection) conn).openOutputStream();
		input = ((InputConnection) conn).openInputStream();
	}

	public String[] send(String mode, String pid, String... morePids) throws IOException {
		StringBuilder command = new StringBuilder();
		command.append(mode);
		command.append(pid);
		for (String otherPid : morePids) {
			command.append(otherPid);
		}
		command.append("\r");
		byte[] buffer = command.toString().getBytes();
		output.write(buffer);
		output.flush();
		byte[] array = new byte[1024];
		int read = input.read(array);
		if (read < 0) {
			return new String[0];
		}
		String result = new String(array, 0, read).trim();
		if (result.toUpperCase().startsWith("NO DATA")) {
			return new String[0];
		}
		String[] bytes = result.split("\\s|\\n|\\r|\\>");
		return bytes;
	}
}

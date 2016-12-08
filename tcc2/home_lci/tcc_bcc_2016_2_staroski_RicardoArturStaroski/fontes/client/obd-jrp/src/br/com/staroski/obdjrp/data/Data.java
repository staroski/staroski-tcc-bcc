package br.com.staroski.obdjrp.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import br.com.staroski.obdjrp.utils.Conversions;

public final class Data {

	public static Data readFrom(InputStream input) throws IOException {
		DataInputStream in = new DataInputStream(input);
		byte pid = in.readByte();
		byte size = in.readByte();
		byte[] value = new byte[size];
		for (int i = 0; i < size; i++) {
			value[i] = in.readByte();
		}
		Data data = new Data(Conversions.byteToHexa(pid), Conversions.bytesToHexas(value));
		return data;
	}

	private final String pid;
	private final String value;

	public Data(String pid, String value) {
		this.pid = pid;
		this.value = value;
	}

	public String getPID() {
		return pid;
	}

	public String getValue() {
		return value;
	}

	public boolean isEmpty() {
		return Conversions.isEmpty(getValue());
	}

	public <T extends OutputStream> T writeTo(T output) throws IOException {
		DataOutputStream out = new DataOutputStream(output);
		Data data = this;
		byte pid = Conversions.hexaToByte(data.getPID());
		out.writeByte(pid);
		byte[] value = Conversions.hexasToBytes(data.getValue());
		int size = value.length;
		out.writeByte(size);
		for (int i = 0; i < size; i++) {
			out.writeByte(value[i]);
		}
		return output;
	}
}

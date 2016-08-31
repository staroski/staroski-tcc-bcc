package br.com.staroski.obd2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

final class Serial implements Connection {

	private final RandomAccessFile randomAccessFile;

	Serial(String port) throws IOException {
		randomAccessFile = new RandomAccessFile(port, "rw");
	}

	@Override
	public void close() throws IOException {
		randomAccessFile.close();
	}

	@Override
	public byte[] read() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		int value = -1;
		while ((value = randomAccessFile.read()) != -1) {
			bytes.write(value);
			if (value == '>') {
				break;
			}
		}
		return bytes.toByteArray();
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		randomAccessFile.write(bytes);
	}
}

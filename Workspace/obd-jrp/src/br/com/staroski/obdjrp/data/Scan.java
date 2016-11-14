package br.com.staroski.obdjrp.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public final class Scan {

	public static Scan readFrom(InputStream input) throws IOException {
		DataInputStream in = new DataInputStream(input);
		Scan dataScan = new Scan(in.readLong());
		List<Data> dataList = dataScan.getData();
		final int dataCount = in.readInt();
		for (int dataIndex = 0; dataIndex < dataCount; dataIndex++) {
			Data data = Data.readFrom(input);
			dataList.add(data);
		}
		return dataScan;
	}

	private final long time;

	private final List<Data> data = new LinkedList<>();

	public Scan(long time) {
		this.time = time;
	}

	public List<Data> getData() {
		return data;
	}

	public long getTime() {
		return time;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public <T extends OutputStream> T writeTo(T output) throws IOException {
		Scan dataScan = this;
		DataOutputStream out = new DataOutputStream(output);
		out.writeLong(dataScan.getTime());
		List<Data> dataList = dataScan.getData();
		out.writeInt(dataList.size());
		for (Data data : dataList) {
			data.writeTo(output);
		}
		out.flush();
		return output;
	}
}

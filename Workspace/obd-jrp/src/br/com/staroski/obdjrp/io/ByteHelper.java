package br.com.staroski.obdjrp.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import br.com.staroski.obdjrp.ObdJrpUtils;
import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;

public final class ByteHelper {

	public static Package bytesToPackage(byte[] bytes) throws IOException {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		Package dataPackage = new Package(in.readUTF(), in.readLong());
		List<Scan> scannedData = dataPackage.getScans();
		final int scanCount = in.readInt();
		for (int scanIndex = 0; scanIndex < scanCount; scanIndex++) {
			Scan dataScan = new Scan(in.readLong());
			List<Data> dataList = dataScan.getData();
			final int dataCount = in.readInt();
			for (int dataIndex = 0; dataIndex < dataCount; dataIndex++) {
				byte pid = in.readByte();
				byte size = in.readByte();
				byte[] value = new byte[size];
				for (int i = 0; i < size; i++) {
					value[i] = in.readByte();
				}
				Data data = new Data(ObdJrpUtils.byteToHexa(pid), ObdJrpUtils.bytesToHexas(value));
				dataList.add(data);
			}
			scannedData.add(dataScan);
		}
		return dataPackage;
	}

	public static byte[] packageToBytes(Package dataPackage) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		out.writeUTF(dataPackage.getVIN());
		out.writeLong(dataPackage.getTime());
		List<Scan> scannedData = dataPackage.getScans();
		out.writeInt(scannedData.size());
		for (Scan dataScan : scannedData) {
			out.writeLong(dataScan.getTime());
			List<Data> dataList = dataScan.getData();
			out.writeInt(dataList.size());
			for (Data data : dataList) {
				byte pid = ObdJrpUtils.hexaToByte(data.getPID());
				out.writeByte(pid);
				byte[] value = ObdJrpUtils.hexasToBytes(data.getValue());
				int size = value.length;
				out.writeByte(size);
				for (int i = 0; i < size; i++) {
					out.writeByte(value[i]);
				}
			}
		}
		return bytes.toByteArray();
	}

	public static Package readFrom(InputStream input) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		for (int read = -1; ((read = input.read(buffer)) != -1); bytes.write(buffer, 0, read)) {}
		return bytesToPackage(bytes.toByteArray());
	}

	public static void writeTo(OutputStream output, Package dataPackage) throws IOException {
		byte[] bytes = packageToBytes(dataPackage);
		output.write(bytes);
		output.flush();
	}

	private ByteHelper() {}
}

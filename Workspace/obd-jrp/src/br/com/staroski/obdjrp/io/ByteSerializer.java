package br.com.staroski.obdjrp.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2DataPackage;
import br.com.staroski.obdjrp.obd2.OBD2DataScan;
import br.com.staroski.obdjrp.utils.Convert;

public class ByteSerializer {

	public static OBD2DataPackage bytesToPackage(byte[] bytes) throws IOException {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		OBD2DataPackage dataPackage = new OBD2DataPackage(in.readUTF(), in.readLong());
		List<OBD2DataScan> scannedData = dataPackage.getScannedData();
		final int scanCount = in.readInt();
		for (int scanIndex = 0; scanIndex < scanCount; scanIndex++) {
			OBD2DataScan dataScan = new OBD2DataScan();
			List<OBD2Data> dataList = dataScan.getDataList();
			final int dataCount = in.readInt();
			for (int dataIndex = 0; dataIndex < dataCount; dataIndex++) {
				byte pid = in.readByte();
				byte size = in.readByte();
				byte[] value = new byte[size];
				for (int i = 0; i < size; i++) {
					value[i] = in.readByte();
				}
				OBD2Data data = new OBD2Data(Convert.byteToHexa(pid), Convert.bytesToHexas(value));
				dataList.add(data);
			}
			scannedData.add(dataScan);
		}
		return dataPackage;
	}

	public static byte[] packageToBytes(OBD2DataPackage dataPackage) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		out.writeUTF(dataPackage.getVIN());
		out.writeLong(dataPackage.getTime());
		List<OBD2DataScan> scannedData = dataPackage.getScannedData();
		out.writeInt(scannedData.size());
		for (OBD2DataScan dataScan : scannedData) {
			List<OBD2Data> dataList = dataScan.getDataList();
			out.writeInt(dataList.size());
			for (OBD2Data data : dataList) {
				byte pid = Convert.hexaToByte(data.getPID());
				out.writeByte(pid);
				byte[] value = Convert.hexasToBytes(data.getValue());
				int size = value.length;
				out.writeByte(size);
				for (int i = 0; i < size; i++) {
					out.writeByte(value[i]);
				}
			}
		}
		return bytes.toByteArray();
	}

	public static OBD2DataPackage readFrom(InputStream input) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		for (int read = -1; ((read = input.read(buffer)) != -1); bytes.write(buffer, 0, read)) {}
		return bytesToPackage(bytes.toByteArray());
	}

	public static void writeTo(OutputStream output, OBD2DataPackage dataPackage) throws IOException {
		byte[] bytes = packageToBytes(dataPackage);
		output.write(bytes);
		output.flush();
	}

	private ByteSerializer() {}
}

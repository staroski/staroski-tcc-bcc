package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class OBD2DataPackage {

	public static OBD2DataPackage readFrom(InputStream input) throws IOException {
		OBD2DataPackage dataPackage = new OBD2DataPackage();
		// TODO
		return dataPackage;
	}

	private final List<OBD2DataScan> scannedData = new LinkedList<>();

	private String vin;
	private long time;

	private OBD2DataPackage() {
		this(null, -1);
	}

	OBD2DataPackage(String vin, long time) {
		this.vin = vin == null ? "UNKNOWN" : vin;
		this.time = time;
	}

	public List<OBD2DataScan> getScannedData() {
		return scannedData;
	}

	public long getTime() {
		return time;
	}

	public String getVIN() {
		return vin;
	}

	void addScannedData(OBD2DataScan scannedData) {
		this.scannedData.add(scannedData);
	}

}

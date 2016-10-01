package br.com.staroski.obdjrp.obd2;

import java.util.LinkedList;
import java.util.List;

public class OBD2DataPackage {

	private final List<OBD2DataScan> scannedData = new LinkedList<>();

	private String vin;
	private long time;

	public OBD2DataPackage(String vin, long time) {
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
}

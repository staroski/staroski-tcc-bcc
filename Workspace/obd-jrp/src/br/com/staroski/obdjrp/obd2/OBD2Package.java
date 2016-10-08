package br.com.staroski.obdjrp.obd2;

import java.util.LinkedList;
import java.util.List;

public final class OBD2Package {

	private final List<OBD2Scan> scans;
	private final String vin;
	private final long time;

	public OBD2Package(String vin, long time) {
		this.vin = vin == null ? "UNKNOWN" : vin;
		this.time = time;
		this.scans = new LinkedList<>();
	}

	public List<OBD2Scan> getScans() {
		return scans;
	}

	public long getTime() {
		return time;
	}

	public String getVIN() {
		return vin;
	}
}

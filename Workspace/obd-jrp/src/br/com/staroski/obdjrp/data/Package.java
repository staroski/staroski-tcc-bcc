package br.com.staroski.obdjrp.data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public final class Package {

	public static final File DEFAULT_DIR = new File(System.getProperty("user.dir"), "obd-jrp-data");
	public static final int DEFAULT_MAX_SIZE = 100;

	private final List<Scan> scans;
	private final String vin;
	private final long time;

	public Package(String vin, long time) {
		this.vin = vin == null ? "UNKNOWN" : vin;
		this.time = time;
		this.scans = new LinkedList<>();
	}

	public List<Scan> getScans() {
		return scans;
	}

	public long getTime() {
		return time;
	}

	public String getVIN() {
		return vin;
	}
}

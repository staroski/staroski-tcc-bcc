package br.com.staroski.obdjrp.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class Package {

	public static final File DEFAULT_DIR = new File(System.getProperty("user.dir"), "obd-jrp-data");
	public static final int DEFAULT_MAX_SIZE = 100;
	public static final String UNKNOWN_VEHICLE = "unknown_vehicle";

	private static List<String> getPIDs(Package dataPackage, boolean onlyWithTranslation) {
		List<String> pids = new ArrayList<>();
		List<Scan> scannedData = dataPackage.getScans();
		if (scannedData.isEmpty()) {
			return pids;
		}
		for (Scan scan : scannedData) {
			for (Data data : scan.getData()) {
				if (onlyWithTranslation) {
					Parsed translation = Parsing.parse(data);
					if (!translation.isUnknown()) {
						pids.add(data.getPID());
					}
				} else {
					pids.add(data.getPID());
				}
			}
		}
		return pids;
	}

	private final List<Scan> scans;
	private final String vehicleId;

	private final long time;

	public Package(String vehicleId, long time) {
		this.vehicleId = vehicleId == null ? UNKNOWN_VEHICLE : vehicleId;
		this.time = time;
		this.scans = new LinkedList<>();
	}

	public List<String> getPIDs() {
		return getPIDs(this, false);
	}

	public List<String> getPIDsWithTranslation() {
		return getPIDs(this, true);
	}

	public List<Scan> getScans() {
		return scans;
	}

	public long getTime() {
		return time;
	}

	public String getVehicle() {
		return vehicleId;
	}

	public boolean isEmpty() {
		return scans.isEmpty();
	}

	public static Package readFrom(InputStream input) throws IOException {
		return ByteSerializer.readPackage(input);
	}

	public void writeTo(OutputStream output) throws IOException {
		ByteSerializer.writePackage(output, this);
	}
}

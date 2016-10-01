package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public final class OBD2DataScan {

	public static OBD2DataScan readFrom(InputStream input) throws IOException {
		OBD2DataScan dataPackage = new OBD2DataScan();
		// TODO
		return dataPackage;
	}

	private final List<OBD2Data> dataPackage = new LinkedList<>();

	OBD2DataScan() {}

	public List<OBD2Data> getDataList() {
		return dataPackage;
	}

	void add(OBD2Data data) {
		this.dataPackage.add(data);
	}
}

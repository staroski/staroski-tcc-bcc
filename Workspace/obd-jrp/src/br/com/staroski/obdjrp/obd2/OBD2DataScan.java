package br.com.staroski.obdjrp.obd2;

import java.util.LinkedList;
import java.util.List;

public final class OBD2DataScan {

	private final List<OBD2Data> dataPackage = new LinkedList<>();

	public OBD2DataScan() {}

	public List<OBD2Data> getDataList() {
		return dataPackage;
	}
}

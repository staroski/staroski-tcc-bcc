package br.com.staroski.obdjrp.obd2;

import java.util.LinkedList;
import java.util.List;

public final class OBD2Scan {

	private final List<OBD2Data> data = new LinkedList<>();

	public OBD2Scan() {}

	public List<OBD2Data> getData() {
		return data;
	}
}

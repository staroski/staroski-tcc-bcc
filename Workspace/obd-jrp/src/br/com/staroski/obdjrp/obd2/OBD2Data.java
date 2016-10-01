package br.com.staroski.obdjrp.obd2;

public final class OBD2Data {

	private final String pid;
	private final String result;

	public OBD2Data(String pid, String result) {
		this.pid = pid;
		this.result = result;
	}

	public String getPID() {
		return pid;
	}

	public String getValue() {
		return result;
	}
}

package br.com.staroski.obdjrp.obd2;

public final class OBD2Data {

	private final String pid;
	private final String result;
	private final String description;
	private final long value;

	OBD2Data(String pid, String result, String description, long value) {
		this.pid = pid;
		this.result = result;
		this.description = description;
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public String getPID() {
		return pid;
	}

	public String getResult() {
		return result;
	}

	public long getValue() {
		return value;
	}
}

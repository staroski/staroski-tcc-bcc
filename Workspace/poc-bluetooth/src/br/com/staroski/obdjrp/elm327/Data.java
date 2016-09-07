package br.com.staroski.obdjrp.elm327;

final class Data implements VehicleData {

	private final String pid;
	private final String result;
	private final String description;
	private final long value;

	Data(String pid, String result, String description, long value) {
		this.pid = pid;
		this.result = result;
		this.description = description;
		this.value = value;
	}

	@Override
	public String getPID() {
		return pid;
	}

	@Override
	public String getResult() {
		return result;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public long getValue() {
		return value;
	}
}

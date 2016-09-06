package br.com.staroski.obdjrp.elm327;

final class Data implements VehicleData {

	private final String description;
	private final int value;

	Data(String description, int value) {
		this.description = description;
		this.value = value;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getValue() {
		return value;
	}
}

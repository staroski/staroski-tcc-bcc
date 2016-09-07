package br.com.staroski.obdjrp.obd2;

public final class OBD2Translation {

	private final String description;
	private final String value;

	OBD2Translation(String description, String value) {
		this.description = description;
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public String getValue() {
		return value;
	}
}

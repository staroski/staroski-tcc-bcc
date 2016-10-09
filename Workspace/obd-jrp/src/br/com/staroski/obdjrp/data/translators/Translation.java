package br.com.staroski.obdjrp.data.translators;

public final class Translation {

	private final String description;
	private final String value;

	Translation(String description, String value) {
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

package br.com.staroski.obdjrp.data;

public interface Translator {

	public Translation translate(Data data);

	public default Translation translation(String description, String value) {
		return new Translation(description, value);
	}

	public default Translation translation(String[][] descriptionValues) {
		return new Translation(descriptionValues);

	}
}

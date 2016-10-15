package br.com.staroski.obdjrp.data;

public interface Translator {

	public Translation translate(Data data);

	public default Translation translation(String description, String value, String... moreValues) {
		return new Translation(description, value, moreValues);
	}
}

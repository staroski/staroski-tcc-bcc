package br.com.staroski.obdjrp.data;

public interface Parser {

	public Parsed parse(Data data);

	public default Parsed parse(String description, String value) {
		return new Parsed(description, value);
	}

	public default Parsed parse(String[][] descriptionValues) {
		return new Parsed(descriptionValues);

	}
}

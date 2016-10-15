package br.com.staroski.obdjrp.data;

public final class Translation {

	public static final Translation UNKNOWN = new Translation("", "");

	private final String description;
	private final String[] values;

	Translation(String description, String value, String... moreValues) {
		this.description = description;
		this.values = new String[1 + moreValues.length];
		this.values[0] = value;
		System.arraycopy(moreValues, 0, this.values, 1, moreValues.length);

	}

	public String getDescription() {
		return description;
	}

	public String getValue(int index) {
		return values[index];
	}

	public int getValues() {
		return values.length;
	}
}

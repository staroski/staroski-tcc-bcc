package br.com.staroski.obdjrp.data;

public final class Translation {

	private static final int DESCRIPTION_INDEX = 0;
	private static final int VALUE_INDEX = 1;

	public static final Translation UNKNOWN = new Translation("", "");

	private final String[][] array;

	Translation(String description, String value) {
		this(new String[][] { new String[] { description, value } });
	}

	Translation(String[][] descriptionValues) {
		this.array = descriptionValues;
	}

	public String getDescription(int index) {
		return array[index][DESCRIPTION_INDEX];
	}

	public String getDescriptions(String separator) {
		StringBuilder description = new StringBuilder();
		for (int i = 0, n = getSize(); i < n; i++) {
			if (i > 0) {
				description.append(separator);
			}
			description.append(getDescription(i));
		}
		return description.toString();
	}

	public int getSize() {
		return array.length;
	}

	public String getValue(int index) {
		return array[index][VALUE_INDEX];
	}

	public String getValues(String separator) {
		StringBuilder value = new StringBuilder();
		for (int i = 0, n = getSize(); i < n; i++) {
			if (i > 0) {
				value.append(separator);
			}
			value.append(getValue(i));
		}
		return value.toString();
	}
}

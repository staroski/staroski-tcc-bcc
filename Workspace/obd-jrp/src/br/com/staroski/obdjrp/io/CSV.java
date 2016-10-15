package br.com.staroski.obdjrp.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CSV {

	public static final String SEPARATOR = ";";

	private final int lines;
	private final List<String> headers = new ArrayList<>();
	private final Map<String, List<String>> headerValues = new HashMap<>();

	CSV(List<String> csvLines) {
		lines = load(csvLines);
	}

	public String getHeader(int index) {
		return headers.get(index);
	}

	public int getHeaders() {
		return headers.size();
	}

	public int getLines() {
		return lines;
	}

	public String getValueAt(int row, int column) {
		return getValueAt(row, getHeader(column));
	}

	public String getValueAt(int row, String header) {
		List<String> values = headerValues.get(header);
		return values.get(row);
	}

	public boolean isEmpty() {
		return getLines() < 1;
	}

	private void addValue(int column, String value) {
		String key = headers.get(column);
		List<String> values = headerValues.get(key);
		if (values == null) {
			values = new ArrayList<>();
			headerValues.put(key, values);
		}
		values.add(value);
	}

	private int load(List<String> csvLines) {
		if (csvLines.isEmpty()) {
			return 0;
		}
		String text = csvLines.get(0);
		for (String header : text.split(SEPARATOR)) {
			headers.add(header);
		}
		for (int line = 1, lines = csvLines.size(); line < lines; line++) {
			text = csvLines.get(line);
			String[] values = text.split(SEPARATOR);
			for (int column = 0, columns = values.length; column < columns; column++) {
				addValue(column, values[column]);
			}
		}
		return csvLines.size() - 1;
	}
}

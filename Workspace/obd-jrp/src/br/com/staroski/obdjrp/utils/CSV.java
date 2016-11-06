package br.com.staroski.obdjrp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parsing;
import br.com.staroski.obdjrp.data.Scan;

public final class CSV {

	public static final String SEPARATOR = ";";

	public static final FileFilter FILE_FILTER = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return false;
			}
			return file.getName().toLowerCase().endsWith(".csv");
		}
	};

	private static final Comparator<File> LAST_MODIFIED = new Comparator<File>() {

		@Override
		public int compare(File o1, File o2) {
			return (int) (o1.lastModified() - o2.lastModified());
		}
	};

	public static CSV createSingleCSV(File dir) {
		List<String> csvLines = new ArrayList<>();
		try {
			File[] csvFiles = dir.listFiles(FILE_FILTER);
			boolean addHeader = true;
			Arrays.sort(csvFiles, LAST_MODIFIED);
			for (File file : csvFiles) {
				try (BufferedReader in = new BufferedReader(new FileReader(file))) {
					String line = in.readLine();
					if (addHeader) {
						addHeader = false; // só adiciona o header do primeiro arquivo
						csvLines.add(line);
					}
					while ((line = in.readLine()) != null) {
						csvLines.add(line);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new CSV(csvLines);
	}

	public static List<String> packageToCSV(Package dataPackage) throws IOException {
		List<String> csv = new ArrayList<>();
		List<Scan> scannedData = dataPackage.getScans();
		if (scannedData.isEmpty()) {
			return csv;
		}
		boolean header = true;
		for (Scan scan : scannedData) {
			StringBuilder line = new StringBuilder();
			if (header) {
				line.append("Date time");
				for (Data data : scan.getData()) {
					Parsed translation = Parsing.parse(data);
					if (!translation.isUnknown()) {
						line.append(CSV.SEPARATOR).append(translation.getDescriptions(CSV.SEPARATOR));
					}
				}
				csv.add(line.toString());
				line = new StringBuilder();
				header = false;
			}
			line.append(ObdJrpProperties.DATE_FORMAT.format(new Date(scan.getTime())));
			for (Data data : scan.getData()) {
				Parsed translation = Parsing.parse(data);
				if (!translation.isUnknown()) {
					line.append(CSV.SEPARATOR).append(translation.getValues(CSV.SEPARATOR));
				}
			}
			csv.add(line.toString());
		}
		return csv;
	}

	public static void writeTo(OutputStream output, Package dataPackage) throws IOException {
		PrintWriter writer = new PrintWriter(output);
		List<String> csv = packageToCSV(dataPackage);
		for (String line : csv) {
			writer.println(line);
		}
		writer.flush();
	}

	private final int lines;

	private final List<String> headers = new ArrayList<>();

	private final Map<String, List<String>> headerValues = new HashMap<>();

	private CSV(List<String> csvLines) {
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

package br.com.staroski.obdjrp.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parsing;

public final class CsvHelper {

	public static final FileFilter CSV_FILE_FILTER = new FileFilter() {

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

	public static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	public static CSV createSingleCsv(File dir) {
		List<String> csvLines = new ArrayList<>();
		try {
			File[] csvFiles = dir.listFiles(CSV_FILE_FILTER);
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

	public static List<String> packageToCsv(Package dataPackage) throws IOException {
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
			line.append(CSV_DATE_FORMAT.format(new Date(scan.getTime())));
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
		List<String> csv = packageToCsv(dataPackage);
		for (String line : csv) {
			writer.println(line);
		}
		writer.flush();
	}

	private CsvHelper() {}
}

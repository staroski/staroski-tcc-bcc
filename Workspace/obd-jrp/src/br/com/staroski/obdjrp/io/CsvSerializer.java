package br.com.staroski.obdjrp.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.data.Translator;

public final class CsvSerializer {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
					line.append(",").append(Translator.getTranslation(data).getDescription());
				}
				csv.add(line.toString());
				line = new StringBuilder();
				header = false;
			}
			line.append(DATE_FORMAT.format(new Date(scan.getTime())));
			for (Data data : scan.getData()) {
				line.append(",").append(Translator.getTranslation(data).getValue());
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

	private CsvSerializer() {}
}

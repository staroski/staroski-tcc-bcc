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
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translators;

public final class CsvSerializer {

	private static final String SEPARATOR = ",";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

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
					Translation translation = Translators.translate(data);
					if (!translation.isUnknown()) {
						line.append(SEPARATOR).append(translation.getDescriptions(SEPARATOR));
					}
				}
				csv.add(line.toString());
				line = new StringBuilder();
				header = false;
			}
			line.append(DATE_FORMAT.format(new Date(scan.getTime())));
			for (Data data : scan.getData()) {
				Translation translation = Translators.translate(data);
				if (!translation.isUnknown()) {
					line.append(SEPARATOR).append(translation.getValues(SEPARATOR));
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

	private CsvSerializer() {}
}

package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.staroski.obdjrp.data.Scan;

final class ObdJrpWeb {

	public static final int CHART_SCAN_SIZE = 60;

	public static final int HISTORY_SCAN_SIZE = 1000;

	public static final Comparator<File> NEW_FILE_FIRST = new Comparator<File>() {

		@Override
		public int compare(File a, File b) {
			return (int) (b.lastModified() - a.lastModified());
		}
	};

	public static final FileFilter SCAN_FILES = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.isFile() && file.getName().toLowerCase().endsWith(".scan");
		}
	};

	public static final Comparator<File> OLD_FILE_FIRST = new Comparator<File>() {

		@Override
		public int compare(File a, File b) {
			return (int) (a.lastModified() - b.lastModified());
		}
	};

	public static List<Scan> getAllScans(String vehicleId) throws IOException {
		List<Scan> scans = new ArrayList<>();
		List<File> files = getScanFiles(vehicleId);
		if (files.isEmpty()) {
			return scans;

		}
		final Scan emptyScan = new Scan(System.currentTimeMillis());
		for (File file : files) {
			try {
				FileInputStream input = new FileInputStream(file);
				Scan scan = Scan.readFrom(input);
				scans.add(scan);
				input.close();
			} catch (FileNotFoundException e) {
				scans.add(emptyScan);
			}
		}
		return scans;
	}

	public static File getDataDir() {
		String tomcat = System.getenv("CATALINA_HOME");
		File file = new File(tomcat + "\\webapps\\obd-jrp-web\\obd-jrp-data");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static Scan getLastScan(String vehicleId) throws IOException {
		List<File> files = getScanFiles(vehicleId);
		final Scan emptyScan = new Scan(System.currentTimeMillis());
		if (files.isEmpty()) {
			return emptyScan;
		}
		Scan scan;
		try {
			FileInputStream input = new FileInputStream(files.get(0));
			scan = Scan.readFrom(input);
			input.close();
		} catch (FileNotFoundException e) {
			return emptyScan;
		}
		return scan;
	}

	public static File getPackagesDir(String vehicleId) {
		File dir = new File(getVehicleDir(vehicleId), "package");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static File getScanDir(String vehicleId) {
		File dir = new File(getVehicleDir(vehicleId), "scans");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static File getVehicleDir(String vehicleId) {
		File dir = new File(getDataDir(), vehicleId);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static void organizeScanDir(String vehicleId) {
		File dir = ObdJrpWeb.getScanDir(vehicleId);
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(dir.listFiles(ObdJrpWeb.SCAN_FILES)));
		Collections.sort(files, ObdJrpWeb.OLD_FILE_FIRST);
		while (files.size() > ObdJrpWeb.HISTORY_SCAN_SIZE) {
			File scanFile = files.remove(0);
			System.out.printf("Erasing \"%s\"...%n", scanFile.getAbsolutePath());
			if (scanFile.delete()) {
				System.out.println("OK");
			} else {
				System.out.println("ERROR");
			}
		}
	}

	private static List<File> getScanFiles(String vehicleId) {
		File dir = getScanDir(vehicleId);
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(dir.listFiles(ObdJrpWeb.SCAN_FILES)));
		Collections.sort(files, ObdJrpWeb.NEW_FILE_FIRST);
		return files;
	}

	private ObdJrpWeb() {}

}

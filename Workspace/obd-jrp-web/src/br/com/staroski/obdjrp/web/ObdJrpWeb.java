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

	public static final int SCANS_SIZE = 60;

	public static final Comparator<File> NEW_FILE_FIRST = new Comparator<File>() {

		@Override
		public int compare(File a, File b) {
			return (int) (b.lastModified() - a.lastModified());
		}
	};

	public static final Comparator<File> OLD_FILE_FIRST = new Comparator<File>() {

		@Override
		public int compare(File a, File b) {
			return (int) (a.lastModified() - b.lastModified());
		}
	};

	public static final FileFilter SCAN_FILES = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.isFile() && file.getName().toLowerCase().endsWith(".scan");
		}
	};

	public static File getDataDir() {
		File file = new File("T:\\obd-jrp-web\\obd-jrp-data");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static Scan getLastScan(String vehicleId) throws IOException {
		File dir = getScanDir(vehicleId);
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(dir.listFiles(ObdJrpWeb.SCAN_FILES)));
		final Scan emptScan = new Scan(System.currentTimeMillis());
		if (files.isEmpty()) {
			return emptScan;
		}
		Collections.sort(files, ObdJrpWeb.NEW_FILE_FIRST);
		Scan scan;
		try {
			FileInputStream input = new FileInputStream(files.get(0));
			scan = Scan.readFrom(input);
			input.close();
		} catch (FileNotFoundException e) {
			return emptScan;
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

	private ObdJrpWeb() {}

}

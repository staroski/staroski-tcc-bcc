package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

import javax.servlet.http.HttpServlet;

abstract class ObdJrpServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	public static final int SCANS_SIZE = 20;

	public static final Comparator<File> OLDEST_FILE = new Comparator<File>() {

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

	public static File getVehicleDir(String vehicleId) {
		File dir = new File(getDataDir(), vehicleId);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public File getPackagesDir(String vehicleId) {
		File dir = new File(getVehicleDir(vehicleId), "package");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public File getScanDir(String vehicleId) {
		File dir = new File(getVehicleDir(vehicleId), "scans");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}

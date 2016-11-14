package br.com.staroski.obdjrp.web;

import java.io.File;

import javax.servlet.http.HttpServlet;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.utils.Conversions;

abstract class ObdJrpServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	public static File getDataDir() {
		File file = new File("T:\\obd-jrp-web\\obd-jrp-data");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static File getDataDir(Package dataPackage) {
		String vehicle = dataPackage.getVehicle();
		String hexa = Conversions.bytesToHexas(vehicle.getBytes());
		File file = new File(getDataDir(), hexa);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
}

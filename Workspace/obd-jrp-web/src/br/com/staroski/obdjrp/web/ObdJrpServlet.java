package br.com.staroski.obdjrp.web;

import java.io.File;

import javax.servlet.http.HttpServlet;

import br.com.staroski.obdjrp.data.Package;

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
		File file = new File(getDataDir(), dataPackage.getVIN());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
}

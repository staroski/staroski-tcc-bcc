package br.com.staroski.obdjrp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.com.staroski.obdjrp.data.Package;

public final class ObdJrpProperties extends Properties {

	private static final long serialVersionUID = 1;

	private static final String FILE_NAME = "obd-jrp.properties";

	public ObdJrpProperties() {
		try {
			InputStream input = ObdJrpProperties.class.getResourceAsStream("/" + FILE_NAME);
			load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDeviceAddress() {
		return getProperty("device_address");
	}

	public String getObdJrpWebServer() {
		return getProperty("obd_jrp_web_server");
	}

	public File getPackageDir() {
		String value = getProperty("package_dir");
		File dir = ObdJrpUtils.isEmpty(value) ? Package.DEFAULT_DIR : new File(value);
		dir.mkdirs();
		return dir;
	}

	public int getPackageMaxSize() {
		String value = getProperty("package_max_size");
		return ObdJrpUtils.isEmpty(value) ? Package.DEFAULT_MAX_SIZE : Integer.parseInt(value);
	}

	public String getServiceName() {
		return getProperty("service_name");
	}

	public String getTranslatorClassForPID(String pid) {
		return getProperty("translator_" + pid);
	}

	public boolean isSavePackageAsXml() {
		String value = getProperty("save_package_as_xml");
		return ObdJrpUtils.isEmpty(value) ? false : "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
	}
}

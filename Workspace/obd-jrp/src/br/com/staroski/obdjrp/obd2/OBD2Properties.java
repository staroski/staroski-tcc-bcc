package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class OBD2Properties extends Properties {

	private static final long serialVersionUID = 1;

	private static final String FILE_NAME = "obd2.properties";

	public OBD2Properties() {
		try {
			InputStream input = OBD2Properties.class.getResourceAsStream("/" + FILE_NAME);
			load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDeviceAddress() {
		return getProperty("device_address");
	}

	public String getServiceName() {
		return getProperty("service_name");
	}

	public String getTranslatorClassForPID(String pid) {
		return getProperty("translator_" + pid);
	}
}

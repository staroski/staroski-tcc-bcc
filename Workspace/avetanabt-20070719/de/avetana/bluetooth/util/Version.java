package de.avetana.bluetooth.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.avetana.bluetooth.stack.BlueZ;

public class Version {

	public static void readVersion() {

		String version = "0";
		String revision = "0";
		String build = "0";

		String line = "";
		try {
			InputStream is = LibLoader.getResourceAsStream("/version.xml");
			if (is == null) is = LibLoader.getResourceAsStream("version.xml");
			BufferedReader br = new BufferedReader (new InputStreamReader (is));
			
		while ((line = br.readLine()) != null) {
			line = line.toLowerCase();
			if (line.indexOf("<version") != -1) {
				revision = "0";
				build = "0";
				int idx = line.indexOf("value");
				idx = line.indexOf("\"", idx + 1);
				int idx2 = line.indexOf("\"", idx + 1);
				version = line.substring(idx + 1, idx2);
			} else if (line.indexOf("<revision") != -1) {
				build = "0";
				int idx = line.indexOf("value");
				idx = line.indexOf("\"", idx + 1);
				int idx2 = line.indexOf("\"", idx + 1);
				revision = line.substring(idx + 1, idx2);				
			} else if (line.indexOf("<build") != -1) {
				int idx = line.indexOf("value");
				idx = line.indexOf("\"", idx + 1);
				int idx2 = line.indexOf("\"", idx + 1);
				build = line.substring(idx + 1, idx2);				
			}
		}
		} catch (Throwable e) {
			version = "not available";
			revision = "";
			build ="";
		}
		System.out.println ("avetanaBluetooth version " + version + "." + revision + "." + build);
	}

}

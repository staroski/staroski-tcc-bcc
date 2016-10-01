package br.com.staroski.obdjrp.obd2.serializers;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import br.com.staroski.obdjrp.obd2.OBD2DataPackage;

public class Teste {

	public static void main(String[] args) {
		try {
			String inFile = "T:\\obd-jrp\\obd-jrp-data\\teste\\2016-10-01-17-05-39.obd";
			FileInputStream input = new FileInputStream(inFile);

			OBD2DataPackage dataPackage = ByteSerializer.readFrom(input);

			String outFile = "T:\\obd-jrp\\obd-jrp-data\\teste\\teste.xml";
			FileOutputStream output = new FileOutputStream(outFile);
			XmlSerializer.writeTo(output, dataPackage);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

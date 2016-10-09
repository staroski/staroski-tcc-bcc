package br.com.staroski.obdjrp.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import br.com.staroski.obdjrp.data.Package;

public class Teste {

	public static void main(String[] args) {
		try {
			String inFile = "T:\\obd-jrp\\obd-jrp-data\\teste\\2016-10-01-18-24-49.obd";
			FileInputStream input = new FileInputStream(inFile);

			Package dataPackage = ByteSerializer.readFrom(input);

			String outFile = "T:\\obd-jrp\\obd-jrp-data\\teste\\teste.xml";
			FileOutputStream output = new FileOutputStream(outFile);
			XmlSerializer.writeTo(output, dataPackage);

			upload(inFile);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static void upload(String path) throws IOException {
		String charset = "UTF-8";
		File uploadFile = new File(path);
		String requestURL = "http://localhost:8081/obd-jrp-web/send-data";

		try {
			MultipartUtility multipart = new MultipartUtility(requestURL, charset);

			// multipart.addHeaderField("User-Agent", "CodeJava");
			// multipart.addHeaderField("Test-Header", "Header-Value");
			//
			// multipart.addFormField("description", "Cool Pictures");
			// multipart.addFormField("keywords", "Java,upload,Spring");

			multipart.addFilePart("fileUpload", uploadFile);

			List<String> response = multipart.finish();

			System.out.println("SERVER REPLIED:");

			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}

	}
}

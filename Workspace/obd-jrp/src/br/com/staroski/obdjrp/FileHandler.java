package br.com.staroski.obdjrp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.io.ByteSerializer;
import br.com.staroski.obdjrp.io.XmlSerializer;

final class FileHandler implements ObdJrpListener {
	@Override
	public void onError(Throwable error) {
		// ignora
	}

	@Override
	public void onFinishPackage(Package dataPackage) {
		saveDataPackage(dataPackage);
	}

	@Override
	public void onScanned(Scan scannedData) {
		// ignora
	}

	@Override
	public void onStartPackage(Package dataPackage) {
		// ignora
	}

	private File getDataDir(Package dataPackage) {
		File file = new File(System.getProperty("user.dir"));
		file = new File(file, "obd-jrp-data");
		file = new File(file, dataPackage.getVIN());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	private File getFile(Package dataPackage, String extension) throws IOException {
		File file = getDataDir(dataPackage);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String name = formatter.format(new Date(dataPackage.getTime()));
		file = new File(file, name + extension);
		file.createNewFile();
		return file;
	}

	private void saveDataPackage(Package dataPackage) {
		try {
			File xmlFile = getFile(dataPackage, ".xml");
			System.out.printf("Gravando \"%s\"...", xmlFile.getAbsolutePath());
			FileOutputStream xmlOutput = new FileOutputStream(xmlFile);
			XmlSerializer.writeTo(xmlOutput, dataPackage);
			xmlOutput.close();
			System.out.println("  OK!");

			File obdFile = getFile(dataPackage, ".obd");
			System.out.printf("Gravando \"%s\"...", obdFile.getAbsolutePath());
			FileOutputStream obdOutput = new FileOutputStream(obdFile);
			ByteSerializer.writeTo(obdOutput, dataPackage);
			obdOutput.close();
			System.out.println("  OK!");
		} catch (IOException e) {
			System.out.println("  ERRO!");
			e.printStackTrace();
		}
	}
}

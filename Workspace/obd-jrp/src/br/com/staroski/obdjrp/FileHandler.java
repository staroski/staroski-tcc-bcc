package br.com.staroski.obdjrp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.io.ByteHelper;
import br.com.staroski.obdjrp.io.XmlHelper;

final class FileHandler extends ObdJrpAdapter {

	private ObdJrpProperties props = new ObdJrpProperties();

	@Override
	public void onFinishPackage(Package dataPackage) {
		saveDataPackage(dataPackage);
	}

	private File getDataDir(Package dataPackage) {
		File folder = props.getPackageDir();
		File file = new File(folder, "vin-" + dataPackage.getVIN());
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
		this.props = new ObdJrpProperties();
		try {
			if (props.isSavePackageAsXml()) {
				File xmlFile = getFile(dataPackage, ".xml");
				System.out.printf("Gravando \"%s\"...", xmlFile.getAbsolutePath());
				FileOutputStream xmlOutput = new FileOutputStream(xmlFile);
				XmlHelper.writeTo(xmlOutput, dataPackage);
				xmlOutput.close();
				xmlFile.setLastModified(dataPackage.getTime());
				System.out.println("  OK!");
			}
			File obdFile = getFile(dataPackage, ".obd");
			System.out.printf("Gravando \"%s\"...", obdFile.getAbsolutePath());
			FileOutputStream obdOutput = new FileOutputStream(obdFile);
			ByteHelper.writeTo(obdOutput, dataPackage);
			obdOutput.close();
			obdFile.setLastModified(dataPackage.getTime());
			System.out.println("  OK!");
		} catch (IOException e) {
			System.out.println("  ERRO!");
			e.printStackTrace();
		}
	}
}

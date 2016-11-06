package br.com.staroski.obdjrp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.utils.ByteHelper;
import br.com.staroski.obdjrp.utils.XmlHelper;

final class FileHandler extends ObdJrpAdapter {

	@Override
	public void onFinishPackage(Package dataPackage) {
		ObdJrpProperties props = ObdJrpProperties.get();
		try {
			if (props.isSavePackageAsXml()) {
				saveAsXml(dataPackage);
			}
			saveAsObd(dataPackage);
		} catch (IOException e) {
			System.out.printf("%s: %s%n", //
					e.getClass().getSimpleName(), //
					e.getMessage());
		}
	}

	private File getDataDir(Package dataPackage) {
		ObdJrpProperties props = ObdJrpProperties.get();
		File folder = props.getPackageDir();
		File file = new File(folder, dataPackage.getVehicle());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	private File getFile(Package dataPackage, String extension) throws IOException {
		File file = getDataDir(dataPackage);
		String name = ObdJrpProperties.DATE_FORMAT.format(new Date(dataPackage.getTime()));
		file = new File(file, name + extension);
		file.createNewFile();
		return file;
	}

	private void saveAsObd(Package dataPackage) throws IOException, FileNotFoundException {
		File obdFile = getFile(dataPackage, ".obd");
		System.out.printf("Gravando \"%s\"...", obdFile.getAbsolutePath());
		FileOutputStream obdOutput = new FileOutputStream(obdFile);
		ByteHelper.writeTo(obdOutput, dataPackage);
		obdOutput.close();
		obdFile.setLastModified(dataPackage.getTime());
		System.out.println("  OK!");
	}

	private void saveAsXml(Package dataPackage) throws IOException, FileNotFoundException {
		File xmlFile = getFile(dataPackage, ".xml");
		System.out.printf("Gravando \"%s\"...", xmlFile.getAbsolutePath());
		FileOutputStream xmlOutput = new FileOutputStream(xmlFile);
		XmlHelper.writeTo(xmlOutput, dataPackage);
		xmlOutput.close();
		xmlFile.setLastModified(dataPackage.getTime());
		System.out.println("  OK!");
	}
}

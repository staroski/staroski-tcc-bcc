package br.com.staroski.obdjrp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.Print;
import br.com.staroski.obdjrp.utils.XmlHelper;

final class PackagePersister {

	private Package dataPackage;

	PackagePersister() {
		startNewPackage();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				persist();
			}
		}, "PackagePersister_ShutdownHook"));
	}

	public void add(Scan scan) {
		final int limit = Config.get().dataMaxScans();
		List<Scan> scannedData = dataPackage.getScans();
		System.out.printf("adding data %d of %d to data package...%n", scannedData.size() + 1, limit);
		scannedData.add(scan);
		if (scannedData.size() == limit) {
			System.out.printf("data package filled!%n");
			persist();
		}
	}

	public void persist() {
		if (dataPackage.isEmpty()) {
			return;
		}
		System.out.println("trying to persist data package...");
		Config props = Config.get();
		try {
			if (props.savingPackageAsXml()) {
				persistXml(dataPackage);
			}
			persistObd(dataPackage);
			System.out.println("data package persisted!");
		} catch (IOException error) {
			Print.message(error);
		} finally {
			startNewPackage();
		}
	}

	private File getDataDir(Package dataPackage) {
		Config props = Config.get();
		File folder = props.dataDir();
		File file = new File(folder, dataPackage.getVehicle());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	private File getFile(Package dataPackage, String extension) throws IOException {
		File file = getDataDir(dataPackage);
		String name = Config.get().formatted(new Date(dataPackage.getTime()));
		file = new File(file, name + extension);
		file.createNewFile();
		return file;
	}

	private void persistObd(Package dataPackage) throws IOException, FileNotFoundException {
		File obdFile = getFile(dataPackage, ".obd");
		System.out.printf("writing \"%s\"...%n", obdFile.getAbsolutePath());
		FileOutputStream obdOutput = new FileOutputStream(obdFile);
		dataPackage.writeTo(obdOutput);
		obdOutput.close();
		obdFile.setLastModified(dataPackage.getTime());
		System.out.println("OK!");
	}

	private void persistXml(Package dataPackage) throws IOException, FileNotFoundException {
		File xmlFile = getFile(dataPackage, ".xml");
		System.out.printf("writing \"%s\"...%n", xmlFile.getAbsolutePath());
		FileOutputStream xmlOutput = new FileOutputStream(xmlFile);
		XmlHelper.writeTo(xmlOutput, dataPackage);
		xmlOutput.close();
		xmlFile.setLastModified(dataPackage.getTime());
		System.out.println("OK!");
	}

	private void startNewPackage() {
		final String vehicle = Config.get().vehicle();
		dataPackage = new Package(vehicle, System.currentTimeMillis());
	}
}

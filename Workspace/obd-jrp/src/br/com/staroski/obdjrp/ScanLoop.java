package br.com.staroski.obdjrp;

import java.io.IOException;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.utils.Timer;

final class ScanLoop {

	private final ObdJrpScanner scanner;

	private boolean scanning;
	private Thread loop;

	public ScanLoop(ObdJrpScanner scanner) {
		this.scanner = scanner;
		scanner.addListener(new ScanUploader());
	}

	public void start() {
		if (scanning) {
			return;
		}
		scanning = true;
		loop = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					execute();
				} catch (Throwable error) {
					scanning = false;
					System.out.printf("%s: %s%n", //
							error.getClass().getSimpleName(), //
							error.getMessage());
					scanner.notifyError(ELM327Error.wrap(error));
				}
			}
		}, getClass().getSimpleName());
		loop.start();
	}

	public void stop() {
		if (!scanning) {
			return;
		}
		scanning = false;
		if (loop != null && loop.isAlive()) {
			try {
				loop.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Package createDataPackage() {
		ObdJrpProperties properties = ObdJrpProperties.get();
		final String vehicle = properties.vehicle();
		Package dataPackage = new Package(vehicle, System.currentTimeMillis());
		System.out.printf("creating new data package...%n");
		scanner.notifyStartPackage(dataPackage);
		return dataPackage;
	}

	private void execute() throws IOException {
		final int count = ObdJrpProperties.get().dataMaxScans();
		while (scanning) {
			Package dataPackage = null;
			try {
				dataPackage = createDataPackage();
				for (int index = 1; scanning && index <= count; index++) {
					scanData(dataPackage, index, count);
				}
				save(dataPackage);
			} catch (ELM327Error error) {
				scanning = false;
				System.out.printf("%s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				save(dataPackage);
				scanner.notifyError(error);
			}
		}
	}

	private void save(Package obd2Package) {
		if (obd2Package != null) {
			if (obd2Package.isEmpty()) {
				System.out.println("empty package ignored!");
			} else {
				System.out.println("trying to save data package...");
				scanner.notifyFinishPackage(obd2Package);
				System.out.println("data package saved!");
			}
		}
	}

	private Scan scanData(Package dataPackage, int index, int limit) throws IOException, ELM327Error {
		System.out.printf("scanning data %d of %d...%n", index, limit);
		Timer timer = new Timer();
		Scan scan = scanner.scan();
		System.out.printf("%d PIDs read%ndata scanned in %dms%n", scan.getData().size(), timer.elapsed());
		dataPackage.getScans().add(scan);
		scanner.notifyScanned(scan);
		return scan;
	}
}
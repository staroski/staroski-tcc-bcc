package br.com.staroski.obdjrp;

import java.io.IOException;
import java.util.List;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.utils.Timer;

final class ScanLoop {

	private final ObdJrpScanner scanner;
	private boolean stopped;
	private boolean scanning;

	private Thread loop;

	public ScanLoop(ObdJrpScanner obd2Monitor) {
		this.scanner = obd2Monitor;
	}

	public void start() {
		if (scanning) {
			return;
		}
		stopped = false;
		scanning = true;
		loop = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					execute();
				} catch (Throwable t) {
					scanning = false;
					stopped = true;
					t.printStackTrace();
				}
			}
		}, getClass().getSimpleName());
		loop.start();
	}

	public void stop() {
		if (stopped) {
			return;
		}
		stopped = true;
		scanning = false;
		if (loop != null && loop.isAlive()) {
			try {
				loop.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void execute() throws IOException {
		final Timer timer = new Timer();
		while (scanning) {
			Package obd2Package = null;
			try {
				ObdJrpProperties properties = ObdJrpProperties.get();
				final int packageMaxSize = properties.getPackageMaxSize();
				final String vin = properties.getVehicle();
				obd2Package = new Package(vin, System.currentTimeMillis());
				final List<Scan> scans = obd2Package.getScans();
				System.out.printf("creating new data package...%n");
				scanner.notifyStartPackage(obd2Package);
				for (int i = 0; scanning && i < packageMaxSize; i++) {
					System.out.printf("scanning data %d of %d...%n", (i + 1), packageMaxSize);
					timer.reset();
					Scan scan = scanner.scan();
					System.out.printf("%d PIDs read%ndata scanned in %dms%n", scan.getData().size(), timer.elapsed());
					scans.add(scan);
					scanner.notifyScanned(scan);
					if (!scanning) {
						break;
					}
				}
				save(obd2Package);
			} catch (ELM327Error error) {
				scanning = false;
				stopped = true;
				System.out.printf("%s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				save(obd2Package);
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
}
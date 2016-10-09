package br.com.staroski.obdjrp;

import java.util.List;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;

final class ScanLoop {

	private static final int ONE_SECOND = 1000;

	private final ELM327Monitor obd2Monitor;

	private boolean scanning;

	private Thread thread;

	public ScanLoop(ELM327Monitor obd2Monitor) {
		this.obd2Monitor = obd2Monitor;
	}

	public void start() {
		if (!scanning) {
			scanning = true;
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					execute();
				}
			});
			thread.start();
		}
	}

	public void stop() {
		scanning = false;
		if (thread != null && thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void execute() {
		while (scanning) {
			Package obd2Package = null;
			try {
				final int packageMaxSize = new ObdJrpProperties().getPackageMaxSize();
				final String vin = obd2Monitor.getVIN();
				obd2Package = new Package(vin, System.currentTimeMillis());
				final List<Scan> scans = obd2Package.getScans();
				System.out.printf("creating new data package...%n");
				obd2Monitor.notifyStartPackage(obd2Package);
				for (int i = 0; scanning && i < packageMaxSize; i++) {
					System.out.printf("scanning data %d of %d...", (i + 1), packageMaxSize);
					long begin = System.currentTimeMillis();
					Scan scan = obd2Monitor.scan();
					scans.add(scan);
					obd2Monitor.notifyScanned(scan);
					System.out.printf("    DONE! %d PIDs scanned!%n", scan.getData().size());
					long end = System.currentTimeMillis();
					long elapsed = end - begin;
					if (elapsed < ONE_SECOND) {
						Thread.sleep(ONE_SECOND - elapsed);
					} else {
						Thread.yield();
					}
					if (!scanning) {
						break;
					}
				}
				obd2Monitor.notifyFinishPackage(obd2Package);
				System.out.println("data package saved!");
			} catch (Throwable e) {
				scanning = false;
				if (obd2Package != null) {
					System.out.println("a error happened! trying to save data package...");
					obd2Monitor.notifyFinishPackage(obd2Package);
					System.out.println("data package saved!");
				}
				e.printStackTrace();
				obd2Monitor.notifyError(e);
			}
		}
	}
}
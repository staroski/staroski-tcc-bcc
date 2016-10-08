package br.com.staroski.obdjrp.obd2;

import java.util.List;

final class ScanLoop {

	private static final int ONE_SECOND = 1000;

	private final ELM327Monitor obd2Monitor;

	private boolean scanning;

	public ScanLoop(ELM327Monitor obd2Monitor) {
		this.obd2Monitor = obd2Monitor;
	}

	public void start() {
		if (!scanning) {
			scanning = true;
			new Thread(new Runnable() {

				@Override
				public void run() {
					execute();
				}
			}).start();
		}
	}

	public void stop() {
		scanning = false;
	}

	private void execute() {
		while (scanning) {
			try {
				final int packageMaxSize = new OBD2Properties().getPackageMaxSize();
				final String vin = obd2Monitor.getVIN();
				final OBD2Package obd2Package = new OBD2Package(vin, System.currentTimeMillis());
				final List<OBD2Scan> scans = obd2Package.getScans();

				System.out.printf("preparing new data package...%n");

				obd2Monitor.notifyStartPackage(obd2Package);
				for (int i = 0; i < packageMaxSize && scanning; i++) {

					System.out.printf("scanning data %d of %d to package...", (i + 1), packageMaxSize);

					long begin = System.currentTimeMillis();
					OBD2Scan scan = obd2Monitor.scan();
					scans.add(scan);
					obd2Monitor.notifyScanned(scan);

					System.out.printf("    OK! %d PIDs scanned!%n", scan.getData().size());

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

				System.out.printf("data package prepared!%n");

			} catch (Throwable e) {
				e.printStackTrace();
				scanning = false;
				obd2Monitor.notifyError(e);
			}
		}
	}
}
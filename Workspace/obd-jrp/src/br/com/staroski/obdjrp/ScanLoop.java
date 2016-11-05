package br.com.staroski.obdjrp;

import java.io.IOException;
import java.util.List;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;

final class ScanLoop {

	private static final int ONE_SECOND = 1000;

	private final ObdJrpScanner obdSscanner;

	private boolean stopped;
	private boolean scanning;

	private Thread loop_thread;

	private long begin_scan;

	public ScanLoop(ObdJrpScanner obd2Monitor) {
		this.obdSscanner = obd2Monitor;
	}

	public void start() {
		if (scanning) {
			return;
		}
		stopped = false;
		scanning = true;
		loop_thread = new Thread(new Runnable() {

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
		});
		loop_thread.start();
	}

	public void stop() {
		if (stopped) {
			return;
		}
		stopped = true;
		scanning = false;
		if (loop_thread != null && loop_thread.isAlive()) {
			try {
				loop_thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void begin_scan() {
		begin_scan = System.currentTimeMillis();
	}

	private void end_scan() {
		long end_scan = System.currentTimeMillis();
		long elapsed_time = end_scan - begin_scan;
		if (elapsed_time < ONE_SECOND) {
			try {
				Thread.sleep(ONE_SECOND - elapsed_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void execute() throws IOException {
		while (scanning) {
			Package obd2Package = null;
			try {
				ObdJrpProperties properties = ObdJrpProperties.get();
				final int packageMaxSize = properties.getPackageMaxSize();
				final String vin = properties.getVehicle();
				obd2Package = new Package(vin, System.currentTimeMillis());
				final List<Scan> scans = obd2Package.getScans();
				System.out.printf("creating new data package...%n");
				obdSscanner.notifyStartPackage(obd2Package);
				for (int i = 0; scanning && i < packageMaxSize; i++) {
					begin_scan();
					System.out.printf("scanning data %d of %d...", (i + 1), packageMaxSize);
					Scan scan = obdSscanner.scan();
					scans.add(scan);
					obdSscanner.notifyScanned(scan);
					System.out.printf("    DONE! %d PIDs scanned!%n", scan.getData().size());
					end_scan();
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
				obdSscanner.notifyError(error);
			}
		}
	}

	private void save(Package obd2Package) {
		if (obd2Package != null) {
			if (obd2Package.isEmpty()) {
				System.out.println("empty package ignored!");
			} else {
				System.out.println("trying to save data package...");
				obdSscanner.notifyFinishPackage(obd2Package);
				System.out.println("data package saved!");
			}
		}
	}
}
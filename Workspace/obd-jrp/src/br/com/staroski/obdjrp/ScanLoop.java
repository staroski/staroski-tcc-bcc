package br.com.staroski.obdjrp;

import java.util.List;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;

final class ScanLoop {

	private static final int ONE_SECOND = 1000;

	private final ObdJrpScanner obd2Monitor;

	private boolean scanning;

	private Thread loop_thread;

	private long begin_scan;

	public ScanLoop(ObdJrpScanner obd2Monitor) {
		this.obd2Monitor = obd2Monitor;
	}

	public void start() {
		if (!scanning) {
			scanning = true;
			loop_thread = new Thread(new Runnable() {

				@Override
				public void run() {
					execute();
				}
			});
			loop_thread.start();
		}
	}

	public void stop() {
		if (!scanning) {
			return;
		}
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
		} else {
			Thread.yield();
		}
	}

	private void execute() {
		while (scanning) {
			Package obd2Package = null;
			try {
				ObdJrpProperties properties = new ObdJrpProperties();
				final int packageMaxSize = properties.getPackageMaxSize();
				final String vin = properties.getVehicleId();
				obd2Package = new Package(vin, System.currentTimeMillis());
				final List<Scan> scans = obd2Package.getScans();
				System.out.printf("creating new data package...%n");
				obd2Monitor.notifyStartPackage(obd2Package);
				for (int i = 0; scanning && i < packageMaxSize; i++) {
					begin_scan();
					{
						System.out.printf("scanning data %d of %d...", (i + 1), packageMaxSize);
						Scan scan = obd2Monitor.scan();
						scans.add(scan);
						obd2Monitor.notifyScanned(scan);
						System.out.printf("    DONE! %d PIDs scanned!%n", scan.getData().size());
					}
					end_scan();
					if (!scanning) {
						break;
					}
				}
				save(obd2Package);
			} catch (Throwable e) {
				scanning = false;
				System.out.println("a error happened!");
				e.printStackTrace();
				obd2Monitor.notifyError(e);
				save(obd2Package);
				return;
			}
		}
	}

	private void save(Package obd2Package) {
		if (obd2Package != null) {
			if (obd2Package.isEmpty()) {
				System.out.println("empty package ignored!");
			} else {
				System.out.println("trying to save data package...");
				obd2Monitor.notifyFinishPackage(obd2Package);
				System.out.println("data package saved!");
			}
		}
	}
}
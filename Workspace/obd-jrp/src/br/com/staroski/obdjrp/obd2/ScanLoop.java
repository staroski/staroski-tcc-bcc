package br.com.staroski.obdjrp.obd2;

import java.util.List;

final class ScanLoop {

	private static final int ONE_SECOND = 1000;

	private final ELM327Decorator obd2Decorator;

	private boolean scanning;

	public ScanLoop(ELM327Decorator obd2Decorator) {
		this.obd2Decorator = obd2Decorator;
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
				String vin = obd2Decorator.getVIN();
				OBD2Package dataPackage = new OBD2Package(vin, System.currentTimeMillis());
				List<OBD2Scan> scannedList = dataPackage.getScans();
				obd2Decorator.notifyStartPackage(dataPackage);
				for (int i = 0; i < 10 && scanning; i++) {
					long begin = System.currentTimeMillis();
					OBD2Scan scannedData = obd2Decorator.scan();
					scannedList.add(scannedData);
					obd2Decorator.notifyScanned(scannedData);
					long end = System.currentTimeMillis();
					long elapsed = end - begin;
					if (elapsed < ONE_SECOND) {
						Thread.sleep(ONE_SECOND - elapsed);
					} else {
						Thread.yield();
					}
				}
				obd2Decorator.notifyFinishPackage(dataPackage);
			} catch (Throwable e) {
				e.printStackTrace();
				scanning = false;
				obd2Decorator.notifyError(e);
			}
		}
	}

}
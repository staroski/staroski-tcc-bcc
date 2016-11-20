package br.com.staroski.obdjrp;

import java.io.IOException;

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
					scanData();
				} catch (Throwable error) {
					scanning = false;
					System.out.printf("%s: %s%n", //
							error.getClass().getSimpleName(), //
							error.getMessage());
					scanner.notifyError(ELM327Error.wrap(error));
				}
			}
		}, "ObdJrp_ScanLoop");
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

	private void scanData() throws IOException {
		while (scanning) {
			try {
				System.out.printf("scanning data...%n");
				Timer timer = new Timer();
				Scan scan = scanner.scan();
				int pids = scan.getData().size();
				long time = timer.elapsed();
				System.out.printf("%d PIDs read%ndata scanned in %dms%n", pids, time);
				scanner.notifyScanned(scan);
			} catch (ELM327Error error) {
				scanning = false;
				System.out.printf("%s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				scanner.notifyError(error);
			}
		}
	}
}
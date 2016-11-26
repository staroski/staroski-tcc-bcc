package br.com.staroski.obdjrp.core;

import java.io.IOException;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.utils.Print;
import br.com.staroski.obdjrp.utils.Timer;

final class ScanLoop {

	private final Scanner scanner;

	private boolean scanning;
	private Thread loop;

	public ScanLoop(Scanner scanner) {
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
					Print.message(error);
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

	private void execute() throws IOException {
		while (scanning) {
			try {
				System.out.printf("scanning data...%n");
				Timer timer = new Timer();
				Scan scan = scanner.scan();
				int pids = scan.getData().size();
				long time = timer.elapsed();
				System.out.printf("%d PIDs read%n", pids);
				System.out.printf("data scanned in %dms%n", time);
				scanner.notifyScanned(scan);
			} catch (ELM327Error error) {
				scanning = false;
				Print.message(error);
				scanner.notifyError(error);
			}
		}
	}
}
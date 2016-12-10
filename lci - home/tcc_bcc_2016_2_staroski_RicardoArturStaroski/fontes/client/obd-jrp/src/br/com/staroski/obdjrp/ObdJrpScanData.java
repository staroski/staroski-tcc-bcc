package br.com.staroski.obdjrp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.UIManager;

import br.com.staroski.obdjrp.core.Config;
import br.com.staroski.obdjrp.core.IO;
import br.com.staroski.obdjrp.core.Scanner;
import br.com.staroski.obdjrp.core.ScannerListener;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.ui.ScannerWindow;
import br.com.staroski.obdjrp.utils.Print;

public final class ObdJrpScanData extends ObdJrpApp {

	public static void main(String[] args) {
		try {
			ObdJrpScanData program = new ObdJrpScanData();
			program.execute();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private Scanner scanner;
	private ScannerWindow scannerWindow;

	private final ScannerListener errorListener = new ScannerListener() {

		@Override
		public void onError(ELM327Error error) {
			restartAfterError(error);
		}

		@Override
		public void onScanned(Scan scannedData) { /* ignora */ }
	};

	private ObdJrpScanData() throws IOException {
		super("scan-data");
	}

	private void execute() {
		final IO connection = Config.get().connection();
		final ScannerWindow window = getScannerWindow();
		final ScannerListener windowListener = window.getObdJrpListener();
		window.setVisible(true);
		while (!connection.isOpen()) {
			System.out.println("trying to connect with " + connection);
			try {
				scanner = new Scanner(connection.open());
				scanner.addListener(errorListener);
				scanner.addListener(windowListener);
				scanner.start();
				System.out.println("successfull connected!");
			} catch (IOException | ELM327Error error) {
				Print.message(error);
				if (scanner != null) {
					scanner.removeListener(errorListener);
					scanner.removeListener(windowListener);
				}
				connection.close();
			}
		}
	}

	private ScannerWindow getScannerWindow() {
		if (scannerWindow != null) {
			return scannerWindow;
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		scannerWindow = new ScannerWindow();
		scannerWindow.setResizable(false);
		scannerWindow.setLocationRelativeTo(null);

		scannerWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				stop();
				System.out.println("shutting down...");
				System.exit(0);
			}
		});
		return scannerWindow;
	}

	private void restartAfterError(final ELM327Error error) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				stop();
				execute();
			}
		}, "Scanner_Restarter").start();
	}

	private void stop() {
		if (scanner != null) {
			scanner.stop();
			System.out.println("stopped scanning!");
		}
	}
}

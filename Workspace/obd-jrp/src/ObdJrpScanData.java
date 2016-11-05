import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.UIManager;

import br.com.staroski.obdjrp.ObdJrpAdapter;
import br.com.staroski.obdjrp.ObdJrpConnection;
import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.ObdJrpScanner;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.ui.ScannerWindow;

public final class ObdJrpScanData extends ObdJrpAdapter {

	public static void main(String[] args) {
		try {
			ObdJrpScanData scanner = new ObdJrpScanData();
			scanner.startScanning();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private ObdJrpScanner scanner;
	private ScannerWindow scannerWindow;

	private ObdJrpScanData() {}

	@Override
	public void onError(ELM327Error error) {
		restartAfterError(error);
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
				stopScanning();
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
				scannerWindow.onError(error);
				stopScanning();
				startScanning();
			}
		}, getClass().getSimpleName() + "_Restarting").start();
	}

	private void startScanning() {
		final ObdJrpProperties props = ObdJrpProperties.get();
		final ObdJrpConnection connection = props.getConnection();
		final ScannerWindow window = getScannerWindow();
		window.setVisible(true);
		boolean connected = false;
		while (!connected) {
			System.out.printf("trying to connect with %s%n", connection.toString());
			try {
				scanner = new ObdJrpScanner(connection.open());
				scanner.addListener(window);
				scanner.addListener(this);
				scanner.startScanning();
				connected = true;
				System.out.println("successfull connected!");
			} catch (IOException | ELM327Error error) {
				System.out.printf("%s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				connection.close();
			}
		}
	}

	private void stopScanning() {
		scanner.stopScanning();
		System.out.println("stopped scanning!");
	}
}

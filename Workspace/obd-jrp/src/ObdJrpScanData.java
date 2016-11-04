import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;

import br.com.staroski.obdjrp.ObdJrpAdapter;
import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.ObdJrpScanner;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.ui.ScannerWindow;

public final class ObdJrpScanData extends ObdJrpAdapter {

	public static void main(String[] args) {
		try {
			ObdJrpScanData scanner = new ObdJrpScanData();
			scanner.startScanning();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private ObdJrpScanner obdScanner;

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
		ObdJrpProperties props = new ObdJrpProperties();
		String deviceAddress = props.getDeviceAddress();
		if (isEmpty(deviceAddress)) {
			System.out.printf("property \"device_address\" not found!%n");
			return;
		}
		String serviceName = props.getServiceName();
		if (isEmpty(serviceName)) {
			System.out.printf("property \"service_name\" not found!%n");
			return;
		}
		final ScannerWindow window = getScannerWindow();
		window.setVisible(true);
		boolean connected = false;
		while (!connected) {
			System.out.printf("trying to connect with\n\tdevice:  %s\n\tservice: %s%n", deviceAddress, serviceName);
			IO io = null;
			try {
				io = Bluetooth.connect(deviceAddress, serviceName);
				obdScanner = new ObdJrpScanner(io);
				obdScanner.addListener(window);
				obdScanner.addListener(this);
				obdScanner.startScanning();
				connected = true;
				System.out.println("started scanning!");
			} catch (Throwable error) {
				System.out.printf("%s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				if (io != null) {
					io.close();
				}
			}
		}
	}

	private void stopScanning() {
		obdScanner.stopScanning();
		System.out.println("stopped scanning!");
	}
}

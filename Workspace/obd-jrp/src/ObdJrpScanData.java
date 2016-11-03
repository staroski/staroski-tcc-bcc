import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;

import br.com.staroski.obdjrp.ObdJrpListener;
import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.ObdJrpScanner;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.ui.ListenerFrame;

public final class ObdJrpScanData {

	public static void main(String[] args) {
		try {
			ObdJrpScanData scanner = new ObdJrpScanData();
			scanner.startScanning();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private ObdJrpScanner obdScanner;

	private ListenerFrame scannerWindow;

	private final ObdJrpListener listener = new ObdJrpListener() {

		@Override
		public void onError(ELM327Error error) {
			restartAfterError(error);
		}

		@Override
		public void onFinishPackage(Package dataPackage) {
			if (scannerWindow != null) {
				scannerWindow.onFinishPackage(dataPackage);
			}
		}

		@Override
		public void onScanned(Scan scannedData) {
			if (scannerWindow != null) {
				scannerWindow.onScanned(scannedData);
			}
		}

		@Override
		public void onStartPackage(Package dataPackage) {
			if (scannerWindow != null) {
				scannerWindow.onStartPackage(dataPackage);
			}
		}
	};

	private ObdJrpScanData() {}

	private ListenerFrame createScannerWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		final ListenerFrame listenerFrame = new ListenerFrame();
		listenerFrame.setResizable(false);
		listenerFrame.setLocationRelativeTo(null);

		listenerFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				stopScanning();
				System.out.println("shutting down...");
				System.exit(0);
			}
		});
		return listenerFrame;
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

	private void showScannerWindow() {
		if (scannerWindow == null) {
			scannerWindow = createScannerWindow();
			scannerWindow.setVisible(true);
		}
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
		showScannerWindow();
		boolean connected = false;
		while (!connected) {
			System.out.printf("trying to connect with\n\tdevice:  %s\n\tservice: %s%n", deviceAddress, serviceName);
			IO io = null;
			try {
				io = Bluetooth.connect(deviceAddress, serviceName);
				obdScanner = new ObdJrpScanner(io);
				obdScanner.addListener(listener);
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

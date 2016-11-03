import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.UIManager;

import br.com.staroski.obdjrp.ObdJrpListener;
import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.ObdJrpScanner;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.ui.ListenerFrame;

public final class ObdJrpScanData {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
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
		public void onError(Throwable error) {
			scannerWindow.onError(error);
			stopScanning();
			startScanning();
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

	private void showScannerWindow() {
		if (scannerWindow == null) {
			scannerWindow = createScannerWindow();
			scannerWindow.setVisible(true);
		}
	}

	private void startScanning() {
		ObdJrpProperties props = new ObdJrpProperties();
		String deviceAddress = props.getDeviceAddress();
		String serviceName = props.getServiceName();
		if (isEmpty(deviceAddress) || isEmpty(serviceName)) {
			System.out.printf("nao ha um dispositivo e/ou servico configurado!");
			return;
		}
		boolean connected = false;
		while (!connected) {
			System.out.printf("tentando conectar com\n\tdispositivo: %s\n\tservico:     %s%n", deviceAddress, serviceName);
			IO io = null;
			try {
				io = Bluetooth.connect(deviceAddress, serviceName);
				obdScanner = new ObdJrpScanner(io);
				obdScanner.addListener(listener);
				obdScanner.startScanning();
				showScannerWindow();
				connected = true;
			} catch (Throwable error) {
				System.out.printf("nao foi possivel conectar! %s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				if (io != null) {
					io.closeIO();
				}
			}
		}
	}

	private void stopScanning() {
		try {
			obdScanner.stopScanning();
			System.out.println("stopped scanning!");
		} catch (IOException error) {
			System.out.printf("stopped scanning! error: %s: %s%n", //
					error.getClass().getSimpleName(), //
					error.getMessage());
		}
	}
}

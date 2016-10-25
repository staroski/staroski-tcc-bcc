import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.UIManager;

import br.com.staroski.obdjrp.ELM327;
import br.com.staroski.obdjrp.ELM327Monitor;
import br.com.staroski.obdjrp.FolderMonitor;
import br.com.staroski.obdjrp.ObdJrpListener;
import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.ui.v2.ListenerFrame;

public final class ObdJrpV2 implements ObdJrpListener {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			ObdJrpV2 obdJrp = new ObdJrpV2();
			obdJrp.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private ELM327Monitor elm327monitor;

	private ListenerFrame listenerFrame;

	private ObdJrpV2() {}

	@Override
	public void onError(Throwable error) {
		listenerFrame.onError(error);
		elm327monitor.stop();
		System.out.printf("disconnected!");
		tryToConnect();
	}

	@Override
	public void onFinishPackage(Package dataPackage) {
		if (listenerFrame != null) {
			listenerFrame.onFinishPackage(dataPackage);
		}
	}

	@Override
	public void onScanned(Scan scannedData) {
		if (listenerFrame != null) {
			listenerFrame.onScanned(scannedData);
		}
	}

	@Override
	public void onStartPackage(Package dataPackage) {
		if (listenerFrame != null) {
			listenerFrame.onStartPackage(dataPackage);
		}
	}

	private void checkListenerFrame() {
		if (listenerFrame == null) {
			listenerFrame = createListenerFrame();
			listenerFrame.setVisible(true);
		}
	}

	private void connect(String deviceAddress, String serviceName) throws IOException {
		IO connection = Bluetooth.connect(deviceAddress, serviceName);
		ELM327 elm327 = new ELM327(connection);
		elm327.exec("AT SP 0"); // protocolo automatico
		elm327.exec("AT H0"); // desligando envio dos cabeçalhos

		elm327monitor = new ELM327Monitor(elm327);
		checkListenerFrame();
		elm327monitor.addListener(this);
		elm327monitor.start();
	}

	private ListenerFrame createListenerFrame() {
		final ListenerFrame listenerFrame = new ListenerFrame();
		listenerFrame.setResizable(false);
		listenerFrame.setLocationRelativeTo(null);

		listenerFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				try {
					elm327monitor.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("shutting down...");
				System.exit(0);
			}
		});
		return listenerFrame;
	}

	private void execute() {
		startFolderMonitor();
		tryToConnect();
	}

	private void startFolderMonitor() {
		try {
			new FolderMonitor().start();
		} catch (Exception e) {
			System.out.println("folder monitor aborted!");
			e.printStackTrace();
		}
	}

	private void tryToConnect() {
		Bluetooth.PRINT_DEBUG_INFO = false;
		ObdJrpProperties props = new ObdJrpProperties();
		String deviceAddress = props.getDeviceAddress();
		String serviceName = props.getServiceName();
		if (isEmpty(deviceAddress) || isEmpty(serviceName)) {
			System.out.printf("nao ha um dispositivo e/ou servico configurado!");
			return;
		}
		boolean connected = false;
		while (!connected) {
			try {
				System.out.printf("tentando conectar com\n\tdispositivo: %s\n\tservico:     %s%n", deviceAddress, serviceName);
				connect(deviceAddress, serviceName);
				connected = true;
			} catch (Throwable t) {
				System.out.printf("nao foi possivel conectar!%n");
				t.printStackTrace();
			}
		}
	}
}

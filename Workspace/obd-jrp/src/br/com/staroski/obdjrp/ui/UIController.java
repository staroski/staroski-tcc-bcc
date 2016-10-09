package br.com.staroski.obdjrp.ui;

import static br.com.staroski.obdjrp.ObdJrpUtils.isEmpty;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.JFrame;

import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

public final class UIController {

	private static final class Holder {

		private static final UIController INSTANCE = new UIController();
	}

	public static UIController get() {
		return Holder.INSTANCE;
	}

	private final MainFrame mainFrame;
	private final CardLayout cardLayout;
	private final Container parentPanel;
	private final ConnectionPanel connectionPanel;

	private final DataPanel dataPanel;

	private UIController() {
		mainFrame = new MainFrame();
		cardLayout = new CardLayout();
		connectionPanel = new ConnectionPanel();
		dataPanel = new DataPanel();

		parentPanel = mainFrame.getContentPane();
		parentPanel.setLayout(cardLayout);
		parentPanel.add(connectionPanel, connectionPanel.getName());
		parentPanel.add(dataPanel, dataPanel.getName());

		mainFrame.pack();
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);

		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dataPanel.stop();
				System.exit(0);
			}
		});

		checkAutoConnec();
	}

	public void connect(RemoteDevice device, ServiceRecord service) {
		dataPanel.connect(device, service);
		cardLayout.show(parentPanel, dataPanel.getName());
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public Cursor setCursor(Cursor cursor) {
		JFrame frame = getMainFrame();
		Cursor oldCursor = frame.getCursor();
		frame.setCursor(cursor);
		return oldCursor;
	}

	public Cursor setCursor(int cursor) {
		return setCursor(Cursor.getPredefinedCursor(cursor));
	}

	private void checkAutoConnec() {
		Bluetooth.PRINT_DEBUG_INFO = false;
		ObdJrpProperties props = new ObdJrpProperties();
		String deviceAddress = props.getDeviceAddress();
		String serviceName = props.getServiceName();
		if (isEmpty(deviceAddress) || isEmpty(serviceName)) {
			return;
		}
		try {
			System.out.printf("tentando conectar automaticamente com\n\tdispositivo: %s\n\tservico:     %s%n", deviceAddress, serviceName);
			connect(deviceAddress, serviceName);
		} catch (Throwable t) {
			System.out.printf("nao foi possivel conectar automaticamente!%n");
			t.printStackTrace();
		}
	}

	private void connect(String deviceAddress, String serviceName) {
		dataPanel.connect(deviceAddress, serviceName);
		cardLayout.show(parentPanel, dataPanel.getName());
	}
}

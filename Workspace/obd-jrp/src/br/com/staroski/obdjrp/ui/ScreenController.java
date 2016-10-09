package br.com.staroski.obdjrp.ui;

import java.awt.CardLayout;
import java.awt.Container;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.JFrame;

import br.com.staroski.obdjrp.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.obd2.OBD2Properties;

public final class ScreenController {

	private static final class Holder {

		private static final ScreenController INSTANCE = new ScreenController();
	}

	public static ScreenController get() {
		return Holder.INSTANCE;
	}

	private static boolean isEmpty(String value) {
		return value == null || (value = value.trim()).isEmpty();
	}

	private final OBD2Frame mainFrame;
	private final CardLayout cardLayout;
	private final Container parentPanel;
	private final ConnectionPanel connectionPanel;

	private final DataPanel dataPanel;

	private ScreenController() {
		mainFrame = new OBD2Frame();
		cardLayout = new CardLayout();
		connectionPanel = new ConnectionPanel();
		dataPanel = new DataPanel();

		parentPanel = mainFrame.getContentPane();
		parentPanel.setLayout(cardLayout);
		parentPanel.add(connectionPanel, connectionPanel.getName());
		parentPanel.add(dataPanel, dataPanel.getName());
		mainFrame.pack();

		checkAutoConnec();
	}

	public void connect(RemoteDevice device, ServiceRecord service) {
		dataPanel.connect(device, service);
		cardLayout.show(parentPanel, dataPanel.getName());
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	private void checkAutoConnec() {
		Bluetooth.PRINT_DEBUG_INFO = false;
		OBD2Properties props = new OBD2Properties();
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

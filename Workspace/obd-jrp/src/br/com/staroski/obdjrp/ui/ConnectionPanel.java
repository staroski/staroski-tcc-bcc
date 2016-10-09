package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

final class ConnectionPanel extends JPanel {

	private class DeviceModel extends DefaultComboBoxModel<String> {

		private static final long serialVersionUID = 1;

		@Override
		public String getElementAt(int index) {
			return getDevices().get(index).getBluetoothAddress();
		}

		@Override
		public int getSize() {
			return getDevices().size();
		}

		void update() {
			fireContentsChanged(comboBoxDevice, 0, getDevices().size());
		}
	}

	private class ServiceModel extends DefaultComboBoxModel<String> {

		private static final long serialVersionUID = 1;

		@Override
		public String getElementAt(int index) {
			return Bluetooth.getName(getServices().get(index));
		}

		@Override
		public int getSize() {
			return getServices().size();
		}

		void update() {
			fireContentsChanged(comboBoxService, 0, getServices().size());
		}
	}

	private static final long serialVersionUID = 1;

	private JComboBox<String> comboBoxDevice;
	private JComboBox<String> comboBoxService;

	private List<RemoteDevice> devices = new ArrayList<>();
	private Map<RemoteDevice, List<ServiceRecord>> servicesMap = new HashMap<>();

	private DeviceModel deviceModel;
	private ServiceModel serviceModel;

	public ConnectionPanel() {
		setName(getClass().getSimpleName());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(480, 320));
		setMinimumSize(new Dimension(480, 320));
		setPreferredSize(new Dimension(480, 320));
		setSize(new Dimension(480, 320));
		setLayout(new GridLayout(5, 1, 0, 20));
		setOpaque(false);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(panel);

		JButton buttonSearch = new JButton("Procurar");
		buttonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				search();
			}
		});
		buttonSearch.setPreferredSize(new Dimension(100, 40));
		panel.add(buttonSearch);

		JPanel panelDevice = new JPanel();
		add(panelDevice);
		panelDevice.setLayout(new BorderLayout(0, 0));

		JLabel labelDevice = new JLabel("Dispositivo:");
		panelDevice.add(labelDevice, BorderLayout.NORTH);

		comboBoxDevice = new JComboBox<>();
		comboBoxDevice.setEditable(false);
		comboBoxDevice.addItem("F8E079DAE781");
		comboBoxDevice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				deviceChanged();
			}
		});
		panelDevice.add(comboBoxDevice, BorderLayout.CENTER);

		JPanel panelService = new JPanel();
		add(panelService);
		panelService.setLayout(new BorderLayout(0, 0));

		JLabel labelService = new JLabel("Serviço:");
		panelService.add(labelService, BorderLayout.NORTH);

		comboBoxService = new JComboBox<>();
		comboBoxService.setEditable(false);
		comboBoxService.addItem("BLT");
		panelService.add(comboBoxService, BorderLayout.CENTER);

		JPanel panelConnect = new JPanel();
		add(panelConnect);
		GridBagLayout gbl_panelConnect = new GridBagLayout();
		gbl_panelConnect.columnWidths = new int[] { 0 };
		gbl_panelConnect.rowHeights = new int[] { 0 };
		gbl_panelConnect.columnWeights = new double[] { 0.0, 0.0 };
		gbl_panelConnect.rowWeights = new double[] { 0.0 };
		panelConnect.setLayout(gbl_panelConnect);

		JButton buttonConnect = new JButton("Conectar");
		buttonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connect();
			}
		});
		buttonConnect.setPreferredSize(new Dimension(150, 40));
		GridBagConstraints gbc_buttonConnect = new GridBagConstraints();
		gbc_buttonConnect.gridx = 1;
		gbc_buttonConnect.gridy = 0;
		panelConnect.add(buttonConnect, gbc_buttonConnect);

		JLabel labelFiller2 = new JLabel("");
		add(labelFiller2);

		deviceModel = new DeviceModel();
		comboBoxDevice.setModel(deviceModel);

		serviceModel = new ServiceModel();
		comboBoxService.setModel(serviceModel);
	}

	private void connect() {

		int deviceIndex = comboBoxDevice.getSelectedIndex();
		int serviceIndex = comboBoxService.getSelectedIndex();

		RemoteDevice device = getDevices().get(deviceIndex);
		ServiceRecord service = getServices().get(serviceIndex);
		UIController.get().connect(device, service);
	}

	private void deviceChanged() {
		serviceModel.update();
	}

	private List<RemoteDevice> getDevices() {
		return devices;
	}

	private List<ServiceRecord> getServices() {
		int index = comboBoxDevice.getSelectedIndex();
		RemoteDevice device = index > -1 ? devices.get(index) : null;
		List<ServiceRecord> services = servicesMap.get(device);
		return services != null ? services : new ArrayList<>();
	}

	private void search() {
		final UIController controller = UIController.get();
		final Cursor cursor = controller.setCursor(Cursor.WAIT_CURSOR);
		try {
			servicesMap = new HashMap<>();
			devices = new ArrayList<>(Bluetooth.getDevices());
			List<RemoteDevice> withoutServices = new ArrayList<>();
			for (RemoteDevice device : devices) {
				List<ServiceRecord> services = Bluetooth.getServices(device);
				if (services.isEmpty()) {
					withoutServices.add(device);
				} else {
					servicesMap.put(device, services);
				}
			}
			devices.removeAll(withoutServices);
			deviceModel.update();
			serviceModel.update();

			if (getDevices().size() > 0) {
				comboBoxDevice.setSelectedIndex(0);
				if (getServices().size() > 0) {
					comboBoxService.setSelectedIndex(0);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			controller.setCursor(cursor);
		}
	}

}

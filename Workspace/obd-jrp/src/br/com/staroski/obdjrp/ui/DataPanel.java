package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import br.com.staroski.obdjrp.ELM327;
import br.com.staroski.obdjrp.ELM327Monitor;
import br.com.staroski.obdjrp.ObdJrpListener;
import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

final class DataPanel extends JPanel {

	private class OBD2DataListener implements ObdJrpListener {

		@Override
		public void onError(Throwable error) {
			Container parent = getParent();
			JOptionPane.showMessageDialog(parent, error.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
			CardLayout layout = (CardLayout) parent.getLayout();
			layout.previous(parent);
		}

		@Override
		public void onFinishPackage(Package dataPackage) {
			// ignora
		}

		@Override
		public void onScanned(Scan scannedData) {
			DataPanel.this.dataList = scannedData.getData();
			OBD2DataModel model = (OBD2DataModel) table.getModel();
			model.update();
		}

		@Override
		public void onStartPackage(Package dataPackage) {
			DataPanel.this.labelVIN.setText("VIN: " + dataPackage.getVIN());
		}
	}

	private class OBD2DataModel extends AbstractTableModel {

		private static final long serialVersionUID = 1;

		@Override
		public Class<?> getColumnClass(int col) {
			switch (col) {
				case 3:
					return Long.class;
				default:
					return String.class;
			}
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
				case 0:
					return "PID";
				case 1:
					return "Bytes";
				case 2:
					return "Translation";
				case 3:
				default:
					return "Value";
			}
		}

		@Override
		public int getRowCount() {
			return dataList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Data rawData = dataList.get(row);
			Translation translated = Translator.getTranslation(rawData);
			switch (col) {
				case 0:
					return rawData.getPID();
				case 1:
					return rawData.getValue();
				case 2:
					return translated.getDescription();
				case 3:
				default:
					return translated.getValue();
			}
		}

		void update() {
			fireTableDataChanged();
		}
	}

	private static final long serialVersionUID = 1;

	private JLabel labelVIN;
	private JTable table;

	private List<Data> dataList = new LinkedList<>();

	private ELM327Monitor elm327monitor;

	/**
	 * Create the panel.
	 */
	public DataPanel() {
		setName(getClass().getSimpleName());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(480, 320));
		setMinimumSize(new Dimension(480, 320));
		setPreferredSize(new Dimension(480, 320));
		setSize(new Dimension(480, 320));
		setLayout(new BorderLayout(5, 5));
		setOpaque(false);

		labelVIN = new JLabel("VIN:");
		add(labelVIN, BorderLayout.NORTH);

		table = new JTable(new OBD2DataModel());
		table.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);

		add(scrollPane, BorderLayout.CENTER);
	}

	private void connect(IO connection) throws IOException {
		ELM327 elm327 = new ELM327(connection);
		elm327.exec("AT SP 0"); // protocolo automatico
		elm327.exec("AT H0"); // desligando envio dos cabeçalhos

		elm327monitor = new ELM327Monitor(elm327);
		elm327monitor.addListener(new OBD2DataListener());
		elm327monitor.start();
	}

	void connect(RemoteDevice device, ServiceRecord service) {
		try {
			IO connection = Bluetooth.connect(device, service);
			connect(connection);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void connect(String device, String service) {
		try {
			IO connection = Bluetooth.connect(device, service);
			connect(connection);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void stop() {
		final UIController controller = UIController.get();
		final Cursor cursor = controller.setCursor(Cursor.WAIT_CURSOR);
		try {
			if (elm327monitor != null) {
				elm327monitor.stop();
			}
		} finally {
			controller.setCursor(cursor);
		}
	}
}

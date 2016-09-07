package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import br.com.staroski.obdjrp.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.obd2.ELM327;
import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2Listener;
import br.com.staroski.obdjrp.obd2.OBD2Monitor;

class DataPanel extends JPanel {

	private class DataListener implements OBD2Listener {

		@Override
		public void onDataReceived(List<OBD2Data> data) {
			DataPanel.this.dataList = data;
			DataModel model = (DataModel) table.getModel();
			model.update();
		}
	}

	private class DataModel extends AbstractTableModel {

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
					return "Result";
				case 2:
					return "Info";
				case 3:
				default:
					return "Valor";
			}
		}

		@Override
		public int getRowCount() {
			return dataList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			OBD2Data data = dataList.get(row);
			switch (col) {
				case 0:
					return data.getPID();
				case 1:
					return data.getResult();
				case 2:
					return data.getDescription();
				case 3:
				default:
					return data.getValue();
			}
		}

		void update() {
			fireTableDataChanged();
		}
	}

	private static final long serialVersionUID = 1;

	private JTable table;

	private OBD2Monitor monitor;

	private List<OBD2Data> dataList = new LinkedList<>();

	/**
	 * Create the panel.
	 */
	public DataPanel(String device, String service) {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(480, 320));
		setMinimumSize(new Dimension(480, 320));
		setPreferredSize(new Dimension(480, 320));
		setSize(new Dimension(480, 320));
		setLayout(new BorderLayout(5, 5));
		setOpaque(false);

		table = new JTable(new DataModel());
		table.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);

		add(scrollPane, BorderLayout.CENTER);

		connect(device, service);
	}

	private void connect(String device, String service) {
		try {
			IO connection = Bluetooth.connect(device, service);
			ELM327 elm327 = new ELM327(connection);
			elm327.exec("AT SP 0"); // protocolo automatico
			elm327.exec("AT H0"); // desligando envio dos cabeçalhos

			DataListener listener = new DataListener();
			monitor = new OBD2Monitor(elm327, listener);
			monitor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

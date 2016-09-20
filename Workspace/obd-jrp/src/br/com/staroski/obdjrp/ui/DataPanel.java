package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
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
import br.com.staroski.obdjrp.obd2.OBD2Translation;

class DataPanel extends JPanel {

	private class DataListener implements OBD2Listener {

		@Override
		public void onError(Exception error) {
			Container parent = getParent();
			JOptionPane.showMessageDialog(parent, error.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
			CardLayout layout = (CardLayout) parent.getLayout();
			layout.previous(parent);
		}

		@Override
		public void onUpdate(List<OBD2Data> data) {
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
					return "Resposta";
				case 2:
					return "Descrição";
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
			OBD2Data rawData = dataList.get(row);
			OBD2Translation translated = rawData.translate();
			switch (col) {
				case 0:
					return rawData.getPID();
				case 1:
					return rawData.getResult();
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

	private JTable table;

	private List<OBD2Data> dataList = new LinkedList<>();

	/**
	 * Create the panel.
	 */
	public DataPanel() {
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
	}

	void connect(String device, String service) {
		try {
			IO connection = Bluetooth.connect(device, service);
			ELM327 elm327 = new ELM327(connection);
			elm327.exec("AT SP 0"); // protocolo automatico
			elm327.exec("AT H0"); // desligando envio dos cabeçalhos

			DataListener listener = new DataListener();
			OBD2Monitor monitor = new OBD2Monitor(elm327, listener);
			monitor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
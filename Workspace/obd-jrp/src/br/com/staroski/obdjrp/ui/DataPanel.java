package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import br.com.staroski.obdjrp.ObdJrpListener;
import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parsing;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;

final class DataPanel extends JPanel implements ObdJrpListener {

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
					return "Description";
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
			Parsed translated = Parsing.parse(rawData);
			switch (col) {
				case 0:
					return rawData.getPID();
				case 1:
					return rawData.getValue();
				case 2:
					return translated.getDescriptions(" | ");
				case 3:
				default:
					return translated.getValues(" | ");
			}
		}

		void update() {
			fireTableDataChanged();
		}
	}

	private static final long serialVersionUID = 1;

	private JLabel labelVehicle;
	private JTable table;
	private List<Data> dataList = new LinkedList<>();

	public DataPanel() {
		setName(getClass().getSimpleName());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(480, 320));
		setMinimumSize(new Dimension(480, 320));
		setPreferredSize(new Dimension(480, 320));
		setSize(new Dimension(480, 320));
		setLayout(new BorderLayout(5, 5));
		setOpaque(false);

		labelVehicle = new JLabel("Vehicle:" + ObdJrpProperties.get().vehicle());

		add(labelVehicle, BorderLayout.NORTH);

		table = new JTable(new OBD2DataModel());
		table.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);

		add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void onError(ELM327Error error) {
		String mesage = String.format("%s: %s", //
				error.getClass().getSimpleName(), //
				error.getMessage());
		labelVehicle.setText(mesage);
	}

	@Override
	public void onScanned(Scan scannedData) {
		dataList = scannedData.getData();
		OBD2DataModel model = (OBD2DataModel) table.getModel();
		model.update();
	}
}

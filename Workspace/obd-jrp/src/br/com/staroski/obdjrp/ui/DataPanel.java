package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import br.com.staroski.obdjrp.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.obd2.ELM327;
import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2DataPackage;
import br.com.staroski.obdjrp.obd2.OBD2DataScan;
import br.com.staroski.obdjrp.obd2.OBD2Listener;
import br.com.staroski.obdjrp.obd2.OBD2Monitor;
import br.com.staroski.obdjrp.obd2.OBD2Translation;
import br.com.staroski.obdjrp.obd2.writers.XmlSerializer;

class DataPanel extends JPanel {

	private class OBD2DataListener implements OBD2Listener {

		@Override
		public void onError(Exception error) {
			Container parent = getParent();
			JOptionPane.showMessageDialog(parent, error.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
			CardLayout layout = (CardLayout) parent.getLayout();
			layout.previous(parent);
		}

		@Override
		public void onFinishPackage(OBD2DataPackage dataPackage) {
			saveDataPackage(dataPackage);
		}

		@Override
		public void onScanned(OBD2DataScan scannedData) {
			DataPanel.this.dataList = scannedData.getDataList();
			OBD2DataModel model = (OBD2DataModel) table.getModel();
			model.update();
		}

		@Override
		public void onStartPackage(OBD2DataPackage dataPackage) {
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

	private JLabel labelVIN;
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

	private File getXMLFile(OBD2DataPackage dataPackage) throws IOException {
		File file = new File(System.getProperty("user.dir"));
		file = new File(file, "obd-jrp-data");
		file = new File(file, dataPackage.getVIN());
		if (!file.exists()) {
			file.mkdirs();
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		String name = formatter.format(new Date(dataPackage.getTime()));
		file = new File(file, name + ".xml");
		file.createNewFile();
		return file;
	}

	private void saveDataPackage(OBD2DataPackage dataPackage) {
		try {
			File xmlFile = getXMLFile(dataPackage);
			System.out.printf("Gravando \"%s\"...", xmlFile.getAbsolutePath());
			FileOutputStream output = new FileOutputStream(xmlFile);
			XmlSerializer.writeTo(output, dataPackage);
			output.close();
			System.out.println("  OK!");
		} catch (IOException e) {
			System.out.println("  ERRO!");
			e.printStackTrace();
		}

	}

	void connect(RemoteDevice device, ServiceRecord service) {
		try {
			IO connection = Bluetooth.connect(device, service);
			ELM327 elm327 = new ELM327(connection);
			elm327.exec("AT SP 0"); // protocolo automatico
			elm327.exec("AT H0"); // desligando envio dos cabeçalhos

			OBD2Monitor monitor = new OBD2Monitor(elm327);
			monitor.addListener(new OBD2DataListener());
			monitor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

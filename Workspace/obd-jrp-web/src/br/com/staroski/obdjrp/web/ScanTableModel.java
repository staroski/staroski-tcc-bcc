package br.com.staroski.obdjrp.web;

import java.util.List;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parsing;
import br.com.staroski.obdjrp.data.Scan;

public final class ScanTableModel {

	private final List<Data> dataList;

	private final long time;

	ScanTableModel(Scan scan) {
		this.dataList = scan.getData();
		time = scan.getTime();
	}

	public int getColumnCount() {
		return 4;
	}

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

	public int getRowCount() {
		return dataList.size();
	}

	public long getTime() {
		return time;
	}

	public Object getValueAt(int row, int col) {
		Data rawData = dataList.get(row);
		Parsed parsed = Parsing.parse(rawData);
		switch (col) {
			case 0:
				return rawData.getPID();
			case 1:
				return rawData.getValue();
			case 2:
				return parsed.getDescriptions(" | ");
			case 3:
			default:
				return parsed.getValues(" | ");
		}
	}

	public boolean hasParser(int row) {
		Data rawData = dataList.get(row);
		Parsed parsed = Parsing.parse(rawData);
		return !Parsed.EMPTY.equals(parsed);
	}
}

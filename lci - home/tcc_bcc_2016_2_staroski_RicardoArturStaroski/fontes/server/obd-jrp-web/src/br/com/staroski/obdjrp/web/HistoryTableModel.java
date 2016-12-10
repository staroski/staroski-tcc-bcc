package br.com.staroski.obdjrp.web;

import java.util.Date;
import java.util.List;

import br.com.staroski.obdjrp.core.Config;
import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parsing;
import br.com.staroski.obdjrp.data.Scan;

public final class HistoryTableModel {

	private final String pid;
	private final List<Scan> history;

	HistoryTableModel(String pid, List<Scan> scans) {
		this.pid = pid;
		this.history = scans;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return "Time";
			case 1:
				return "Bytes";
			case 2:
			default:
				return "Value";
		}
	}

	public int getRowCount() {
		return history.size();
	}

	public Object getValueAt(int row, int col) {
		Data rawData = getRawData(row);
		Date time = new Date(history.get(row).getTime());
		Parsed parsed = Parsing.parse(rawData);
		switch (col) {
			case 0:
				return Config.DATE_FORMAT.format(time);
			case 1:
				return rawData.getValue();
			case 2:
			default:
				return parsed.getValues(" | ");
		}
	}

	public boolean hasParser(int row) {
		Data rawData = getRawData(row);
		Parsed parsed = Parsing.parse(rawData);
		return !Parsed.EMPTY.equals(parsed);
	}

	private Data getRawData(int row) {
		Scan scan = history.get(row);
		List<Data> dataList = scan.getData();
		for (Data data : dataList) {
			if (pid.equals(data.getPID())) {
				return data;
			}
		}
		return null;
	}
}

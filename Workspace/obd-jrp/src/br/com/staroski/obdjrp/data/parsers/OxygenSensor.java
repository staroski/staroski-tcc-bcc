package br.com.staroski.obdjrp.data.parsers;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parser;

abstract class OxygenSensor implements Parser {

	@Override
	public final Parsed parse(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result.substring(0, 2), 16);
		int b = Integer.parseInt(result.substring(2), 16);
		double voltage = a / 200.0;
		double fuelTrim = (b / 1.28) - 100;
		int number = getNumber();
		return parse(new String[][] { //
				new String[] { String.format("Oxygen sensor %d voltage", number), String.format("%.2f", voltage) }, //
				new String[] { String.format("Oxygen sensor %d fuel trim", number), String.format("%.2f", fuelTrim) }, //
		});
	}

	abstract int getNumber();
}

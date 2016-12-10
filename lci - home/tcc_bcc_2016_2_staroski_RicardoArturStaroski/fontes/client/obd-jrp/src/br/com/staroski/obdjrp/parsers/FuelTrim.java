package br.com.staroski.obdjrp.parsers;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parser;

abstract class FuelTrim implements Parser {

	@Override
	public final Parsed parse(Data data) {
		String result = data.getValue().substring(0, 2);
		int a = Integer.parseInt(result, 16);
		double value = (a / 1.28) - 100;
		return parse(getDescription(), String.format("%.2f", value));
	}

	abstract String getDescription();
}

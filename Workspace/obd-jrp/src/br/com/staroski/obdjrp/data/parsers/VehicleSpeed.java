package br.com.staroski.obdjrp.data.parsers;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parser;

public class VehicleSpeed implements Parser {

	@Override
	public Parsed parse(Data data) {
		String result = data.getValue();
		int value = Integer.parseInt(result, 16);
		return parse("Vehicle Speed", String.valueOf(value));
	}
}

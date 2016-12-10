package br.com.staroski.obdjrp.parsers;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parser;

public class IntakeAirTemperature implements Parser {

	@Override
	public Parsed parse(Data data) {
		String result = data.getValue();
		int value = Integer.parseInt(result, 16) - 40;
		return parse("Intake Air Temperature", String.valueOf(value));
	}
}

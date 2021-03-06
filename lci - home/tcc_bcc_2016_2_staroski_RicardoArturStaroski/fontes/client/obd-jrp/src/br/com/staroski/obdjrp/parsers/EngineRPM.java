package br.com.staroski.obdjrp.parsers;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parser;

public class EngineRPM implements Parser {

	@Override
	public Parsed parse(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		int value = a / 4;
		return parse("Engine RPM", String.valueOf(value));
	}
}

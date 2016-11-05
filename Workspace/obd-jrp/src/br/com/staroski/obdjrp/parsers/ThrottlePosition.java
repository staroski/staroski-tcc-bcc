package br.com.staroski.obdjrp.parsers;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parser;

public class ThrottlePosition implements Parser {

	@Override
	public Parsed parse(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		double value = a / 2.55;
		return parse("Throttle position", String.format("%.2f", value));
	}
}

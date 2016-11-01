package br.com.staroski.obdjrp.data.parsers;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Parsed;
import br.com.staroski.obdjrp.data.Parser;

public class MAFAirFlowRate implements Parser {

	@Override
	public Parsed parse(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		int value = a / 100;
		return parse("MAF Air Flow Rate", String.valueOf(value));
	}
}

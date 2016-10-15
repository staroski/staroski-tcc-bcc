package br.com.staroski.obdjrp.data.translators;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;

abstract class FuelTrim implements Translator {

	@Override
	public final Translation translate(Data data) {
		String result = data.getValue().substring(0, 2);
		int a = Integer.parseInt(result, 16);
		double value = (a / 1.28) - 100;
		return translation(getDescription(), String.format("%.2f", value));
	}

	abstract String getDescription();
}

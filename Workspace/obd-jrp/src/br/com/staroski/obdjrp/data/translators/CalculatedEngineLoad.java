package br.com.staroski.obdjrp.data.translators;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;

public class CalculatedEngineLoad implements Translator {

	@Override
	public Translation translate(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		double value = a / 2.55;
		return translation("Calculated engine load", String.format("%.2f", value));
	}
}

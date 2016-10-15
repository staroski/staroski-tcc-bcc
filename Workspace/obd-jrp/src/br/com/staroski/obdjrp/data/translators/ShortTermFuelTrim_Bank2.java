package br.com.staroski.obdjrp.data.translators;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;

public class ShortTermFuelTrim_Bank2 implements Translator {

	@Override
	public Translation translate(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		double value = (a / 1.28) - 100;
		return translation("Short term fuel trim—Bank 2", String.format("%.2f", value));
	}
}

package br.com.staroski.obdjrp.obd2.translators;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2Translation;
import br.com.staroski.obdjrp.obd2.OBD2Translator;

public class LongTermFuelTrim_Bank1 extends OBD2Translator {

	@Override
	public OBD2Translation translate(OBD2Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		double value = (a / 1.28) - 100;
		return translation("Long term fuel trim—Bank 1", String.format("%.2f", value));
	}
}

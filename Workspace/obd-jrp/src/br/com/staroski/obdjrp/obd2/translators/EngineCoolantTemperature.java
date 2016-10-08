package br.com.staroski.obdjrp.obd2.translators;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2Translation;
import br.com.staroski.obdjrp.obd2.OBD2Translator;

public class EngineCoolantTemperature extends OBD2Translator {

	@Override
	public OBD2Translation translate(OBD2Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		int value = a - 40;
		return translation("Engine coolant temperature", String.valueOf(value));
	}
}

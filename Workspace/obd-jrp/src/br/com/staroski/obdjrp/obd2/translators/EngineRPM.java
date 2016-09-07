package br.com.staroski.obdjrp.obd2.translators;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2DataTranslator;
import br.com.staroski.obdjrp.obd2.OBD2Translation;

public class EngineRPM extends OBD2DataTranslator {

	@Override
	public OBD2Translation translate(OBD2Data data) {
		String result = data.getResult();
		int a = Integer.parseInt(result, 16);
		int value = a / 4;
		return translation("Engine RPM", String.valueOf(value));
	}
}

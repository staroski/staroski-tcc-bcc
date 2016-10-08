package br.com.staroski.obdjrp.obd2.translators;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2Translator;
import br.com.staroski.obdjrp.obd2.OBD2Translation;

public class MAFAirFlowRate extends OBD2Translator {

	@Override
	public OBD2Translation translate(OBD2Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		int value = a / 100;
		return translation("MAF Air Flow Rate", String.valueOf(value));
	}
}

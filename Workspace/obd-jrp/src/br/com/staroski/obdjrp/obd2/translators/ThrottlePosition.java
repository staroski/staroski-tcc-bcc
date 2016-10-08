package br.com.staroski.obdjrp.obd2.translators;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2Translation;
import br.com.staroski.obdjrp.obd2.OBD2Translator;

public class ThrottlePosition extends OBD2Translator {

	@Override
	public OBD2Translation translate(OBD2Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		double value = a / 2.55;
		return translation("Throttle position", String.format("%.2f", value));
	}
}

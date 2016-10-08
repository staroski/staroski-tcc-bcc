package br.com.staroski.obdjrp.obd2.translators;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2Translation;
import br.com.staroski.obdjrp.obd2.OBD2Translator;

public class OxygenSensor7 extends OBD2Translator {

	@Override
	public OBD2Translation translate(OBD2Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result.substring(0, 2), 16);
		int b = Integer.parseInt(result.substring(2), 16);
		double voltage = a / 200.0;
		double fuelTrim = (b / 1.28) - 100;
		return translation("Oxygen sensor 7", String.format("%.2fV    %.2f%%", voltage, fuelTrim));
	}
}

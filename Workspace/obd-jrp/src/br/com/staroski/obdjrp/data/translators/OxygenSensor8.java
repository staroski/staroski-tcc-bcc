package br.com.staroski.obdjrp.data.translators;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;

public class OxygenSensor8 extends Translator {

	@Override
	public Translation translate(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result.substring(0, 2), 16);
		int b = Integer.parseInt(result.substring(2), 16);
		double voltage = a / 200.0;
		double fuelTrim = (b / 1.28) - 100;
		return translation("Oxygen sensor 8", String.format("%.2fV    %.2f%%", voltage, fuelTrim));
	}
}

package br.com.staroski.obdjrp.data.translators;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;

public class IntakeAirTemperature extends Translator {

	@Override
	public Translation translate(Data data) {
		String result = data.getValue();
		int value = Integer.parseInt(result, 16) - 40;
		return translation("Intake Air Temperature", String.valueOf(value));
	}
}

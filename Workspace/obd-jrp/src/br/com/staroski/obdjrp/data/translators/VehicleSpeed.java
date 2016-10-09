package br.com.staroski.obdjrp.data.translators;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;

public class VehicleSpeed extends Translator {

	@Override
	public Translation translate(Data data) {
		String result = data.getValue();
		int value = Integer.parseInt(result, 16);
		return translation("Vehicle Speed", String.valueOf(value));
	}
}

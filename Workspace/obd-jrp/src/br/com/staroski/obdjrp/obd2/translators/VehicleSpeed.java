package br.com.staroski.obdjrp.obd2.translators;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2DataTranslator;
import br.com.staroski.obdjrp.obd2.OBD2Translation;

public class VehicleSpeed extends OBD2DataTranslator {

	@Override
	public OBD2Translation translate(OBD2Data data) {
		String result = data.getResult();
		int value = Integer.parseInt(result, 16);
		return translation("Vehicle Speed", String.valueOf(value));
	}
}
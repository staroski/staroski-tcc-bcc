package br.com.staroski.obdjrp.data.translators;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Translation;
import br.com.staroski.obdjrp.data.Translator;

public class MAFAirFlowRate extends Translator {

	@Override
	public Translation translate(Data data) {
		String result = data.getValue();
		int a = Integer.parseInt(result, 16);
		int value = a / 100;
		return translation("MAF Air Flow Rate", String.valueOf(value));
	}
}

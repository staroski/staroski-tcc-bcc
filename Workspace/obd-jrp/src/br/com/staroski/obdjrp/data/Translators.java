package br.com.staroski.obdjrp.data;

import java.util.HashMap;
import java.util.Map;

import br.com.staroski.obdjrp.ObdJrpProperties;

@SuppressWarnings("unchecked")
public final class Translators {

	private static final Map<String, Translator> TRANSLATORS = new HashMap<>();

	public static Translation translate(Data data) {
		String pid = data.getPID();
		Translator translator = TRANSLATORS.get(pid);
		if (translator == null) {
			ObdJrpProperties properties = new ObdJrpProperties();
			String className = properties.getTranslatorClassForPID(pid);
			if (className == null) {
				return Translation.UNKNOWN;
			}
			try {
				Class<Translator> clazz = (Class<Translator>) Class.forName(className);
				translator = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return Translation.UNKNOWN;
			}
			TRANSLATORS.put(pid, translator);
		}
		return translator.translate(data);
	}

	private Translators() {}
}

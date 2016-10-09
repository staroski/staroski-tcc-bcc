package br.com.staroski.obdjrp.data;

import java.util.HashMap;
import java.util.Map;

import br.com.staroski.obdjrp.ObdJrpProperties;

@SuppressWarnings("unchecked")
public abstract class Translator {

	public static final Translation UNKNOWN = translation("", "");

	private static final Map<String, Translator> TRANSLATORS = new HashMap<>();

	public static Translation getTranslation(Data data) {
		String pid = data.getPID();
		Translator translator = TRANSLATORS.get(pid);
		if (translator == null) {
			ObdJrpProperties properties = new ObdJrpProperties();
			String className = properties.getTranslatorClassForPID(pid);
			if (className == null) {
				return UNKNOWN;
			}
			try {
				Class<Translator> clazz = (Class<Translator>) Class.forName(className);
				translator = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return UNKNOWN;
			}
			TRANSLATORS.put(pid, translator);
		}
		return translator.translate(data);
	}

	protected static Translation translation(String description, String value) {
		return new Translation(description, value);
	}

	public abstract Translation translate(Data data);
}

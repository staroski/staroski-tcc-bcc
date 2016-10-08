package br.com.staroski.obdjrp.obd2;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class OBD2Translator {

	public static final OBD2Translation UNKNOWN = translation("", "");

	private static final Map<String, OBD2Translator> TRANSLATORS = new HashMap<>();

	public static OBD2Translation getTranslation(OBD2Data data) {
		String pid = data.getPID();
		OBD2Translator translator = TRANSLATORS.get(pid);
		if (translator == null) {
			OBD2Properties properties = new OBD2Properties();
			String className = properties.getTranslatorClassForPID(pid);
			if (className == null) {
				return UNKNOWN;
			}
			try {
				Class<OBD2Translator> clazz = (Class<OBD2Translator>) Class.forName(className);
				translator = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return UNKNOWN;
			}
			TRANSLATORS.put(pid, translator);
		}
		return translator.translate(data);
	}

	protected static OBD2Translation translation(String description, String value) {
		return new OBD2Translation(description, value);
	}

	public abstract OBD2Translation translate(OBD2Data data);
}

package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("unchecked")
public abstract class OBD2DataTranslator {

	public static final OBD2Translation UNKNOWN = translation("", "");

	private static final Map<String, OBD2DataTranslator> TRANSLATORS = new HashMap<>();

	private static Properties properties;

	private static Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			try {
				Class<OBD2DataTranslator> translatorClass = OBD2DataTranslator.class;
				String path = translatorClass.getPackage().getName().replace('.', '/');
				InputStream input = translatorClass.getResourceAsStream("/" + path + "/translators.properties");
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	protected static OBD2Translation translation(String description, String value) {
		return new OBD2Translation(description, value);
	}

	static OBD2Translation getTranslation(OBD2Data data) {
		String key = "pid." + data.getPID();
		OBD2DataTranslator translator = TRANSLATORS.get(key);
		if (translator == null) {
			Properties properties = getProperties();
			String className = (String) properties.get(key);
			if (className == null) {
				return UNKNOWN;
			}
			try {
				Class<OBD2DataTranslator> clazz = (Class<OBD2DataTranslator>) Class.forName(className);
				translator = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return UNKNOWN;
			}
			TRANSLATORS.put(key, translator);
		}
		return translator.translate(data);
	}

	public abstract OBD2Translation translate(OBD2Data data);
}

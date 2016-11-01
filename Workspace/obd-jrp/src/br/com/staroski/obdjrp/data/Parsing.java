package br.com.staroski.obdjrp.data;

import java.util.HashMap;
import java.util.Map;

import br.com.staroski.obdjrp.ObdJrpProperties;

@SuppressWarnings("unchecked")
public final class Parsing {

	private static final Map<String, Parser> PARSERS = new HashMap<>();

	public static Parsed parse(Data data) {
		String pid = data.getPID();
		Parser translator = PARSERS.get(pid);
		if (translator == null) {
			ObdJrpProperties properties = new ObdJrpProperties();
			String className = properties.getTranslatorClassForPID(pid);
			if (className == null) {
				return Parsed.UNKNOWN;
			}
			try {
				Class<Parser> clazz = (Class<Parser>) Class.forName(className);
				translator = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return Parsed.UNKNOWN;
			}
			PARSERS.put(pid, translator);
		}
		return translator.parse(data);
	}

	private Parsing() {}
}

package br.com.staroski.obdjrp.data;

import java.util.HashMap;
import java.util.Map;

import br.com.staroski.obdjrp.core.Config;

@SuppressWarnings("unchecked")
public final class Parsing {

	private static final Map<String, Parser> PARSERS = new HashMap<>();

	public static Parsed parse(Data data) {
		if (data == null || data.isEmpty()) {
			return Parsed.EMPTY;
		}
		String pid = data.getPID();
		Parser parser = PARSERS.get(pid);
		if (parser == null) {
			Config properties = Config.get();
			String className = properties.parser(pid);
			if (className == null) {
				return Parsed.EMPTY;
			}
			try {
				Class<Parser> clazz = (Class<Parser>) Class.forName(className);
				parser = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return Parsed.EMPTY;
			}
			PARSERS.put(pid, parser);
		}
		try {
			return parser.parse(data);
		} catch (Exception e) {
			e.printStackTrace();
			return Parsed.EMPTY;
		}
	}

	private Parsing() {}
}

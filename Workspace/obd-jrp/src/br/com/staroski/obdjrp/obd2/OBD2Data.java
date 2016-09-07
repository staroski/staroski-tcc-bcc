package br.com.staroski.obdjrp.obd2;

import java.io.Serializable;

public final class OBD2Data implements Serializable {

	private static final long serialVersionUID = 1;

	private final String pid;
	private final String result;

	private transient OBD2Translation translation;

	OBD2Data(String pid, String result) {
		this.pid = pid;
		this.result = result;
	}

	public String getPID() {
		return pid;
	}

	public String getResult() {
		return result;
	}

	public OBD2Translation translate() {
		if (translation == null) {
			translation = OBD2DataTranslator.getTranslation(this);
		}
		return translation;
	}
}

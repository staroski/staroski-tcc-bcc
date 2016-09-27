package br.com.staroski.obdjrp.obd2;

public final class OBD2Data {

	private String pid;

	private String result;

	private OBD2Translation translation;

	OBD2Data() {
		this(null, null);
	}

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

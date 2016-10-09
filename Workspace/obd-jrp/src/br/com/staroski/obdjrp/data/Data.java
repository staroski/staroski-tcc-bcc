package br.com.staroski.obdjrp.data;

public final class Data {

	private final String pid;
	private final String result;

	public Data(String pid, String result) {
		this.pid = pid;
		this.result = result;
	}

	public String getPID() {
		return pid;
	}

	public String getValue() {
		return result;
	}
}

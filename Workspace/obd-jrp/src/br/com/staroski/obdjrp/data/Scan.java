package br.com.staroski.obdjrp.data;

import java.util.LinkedList;
import java.util.List;

public final class Scan {

	private final long time;
	private final List<Data> data = new LinkedList<>();

	public Scan(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public List<Data> getData() {
		return data;
	}
}

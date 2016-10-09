package br.com.staroski.obdjrp.data;

import java.util.LinkedList;
import java.util.List;

public final class Scan {

	private final List<Data> data = new LinkedList<>();

	public Scan() {}

	public List<Data> getData() {
		return data;
	}
}

package br.com.staroski.obdjrp.utils;

public final class Timer {

	private long time;

	public Timer() {
		reset();
	}

	public long elapsed() {
		return System.currentTimeMillis() - time;
	}

	public void reset() {
		time = System.currentTimeMillis();
	}
}

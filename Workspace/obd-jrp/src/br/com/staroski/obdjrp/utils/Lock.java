package br.com.staroski.obdjrp.utils;

public final class Lock {

	private final Object LOCK = new Object();

	public void lock() {
		lock(0);
	}

	public void lock(long timeout) {
		try {
			synchronized (LOCK) {
				LOCK.wait(timeout);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(getClass().getSimpleName() + " interrupted!", e);
		}
	}

	public void unlock() {
		synchronized (LOCK) {
			LOCK.notifyAll();
		}
	}
}

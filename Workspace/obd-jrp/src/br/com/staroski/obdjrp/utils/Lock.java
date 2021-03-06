package br.com.staroski.obdjrp.utils;

public final class Lock {

	private final Object LOCK = new Object();

	public void lock() {
		lock(0);
	}

	public void lock(long timeout) {
		synchronized (LOCK) {
			try {
				LOCK.wait(timeout);
			} catch (InterruptedException e) {
				throw new RuntimeException("Lock interrupted!", e);
			}
		}
	}

	public void unlock() {
		synchronized (LOCK) {
			LOCK.notifyAll();
		}
	}
}

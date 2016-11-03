package br.com.staroski.obdjrp.elm;

import java.util.LinkedList;
import java.util.Queue;

final class Disconnector implements Runnable {

	private static final class Holder {

		private static final Disconnector INSTANCE = new Disconnector();
	}

	public static void add(ELM327 elm327) {
		Disconnector disconnector = get();
		synchronized (disconnector) {
			disconnector.elm327list.add(elm327);
		}
	}

	public static void remove(ELM327 elm327) {
		Disconnector disconnector = get();
		synchronized (disconnector) {
			disconnector.elm327list.remove(elm327);
		}
	}

	private static Disconnector get() {
		return Holder.INSTANCE;
	}

	private final Queue<ELM327> elm327list = new LinkedList<>();

	private Disconnector() {
		Runtime.getRuntime().addShutdownHook(new Thread(this, getClass().getSimpleName() + "_ShutdownHook"));
	}

	@Override
	public void run() {
		String adapter = ELM327.class.getSimpleName();
		System.out.printf("closing %s pending connections...  ", adapter);
		int count = 0;
		while (!elm327list.isEmpty()) {
			try {
				final ELM327 elm327;
				synchronized (this) {
					elm327 = elm327list.poll();
				}
				elm327.disconnect();
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (count > 0) {
			System.out.printf("disconnected %d %s!%n", count, adapter);
		} else {
			System.out.printf("no %s connected!%n", adapter);
		}
	}
}

package br.com.staroski.obdjrp.obd2;

import java.util.ArrayList;
import java.util.List;

final class OBD2EventMulticaster implements OBD2Listener {

	private final List<OBD2Listener> listeners;

	OBD2EventMulticaster() {
		listeners = new ArrayList<>();
	}

	@Override
	public void onError(Exception error) {
		for (OBD2Listener listener : listeners) {
			listener.onError(error);
		}
	}

	@Override
	public void onUpdate(OBD2DataPackage dataPackage) {
		for (OBD2Listener listener : listeners) {
			listener.onUpdate(dataPackage);
		}
	}

	void addListener(OBD2Listener listener) {
		listeners.add(listener);
	}

	void removeListener(OBD2Listener listener) {
		listeners.remove(listener);
	}
}

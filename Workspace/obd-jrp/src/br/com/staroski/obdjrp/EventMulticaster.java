package br.com.staroski.obdjrp;

import java.util.ArrayList;
import java.util.List;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;

final class EventMulticaster implements ObdJrpListener {

	private final List<ObdJrpListener> listeners;

	EventMulticaster() {
		listeners = new ArrayList<>();
	}

	@Override
	public void onError(ELM327Error error) {
		for (ObdJrpListener listener : listeners) {
			listener.onError(error);
		}
	}

	@Override
	public void onScanned(Scan scannedData) {
		for (ObdJrpListener listener : listeners) {
			listener.onScanned(scannedData);
		}
	}

	void addListener(ObdJrpListener listener) {
		listeners.add(listener);
	}

	void removeListener(ObdJrpListener listener) {
		listeners.remove(listener);
	}
}

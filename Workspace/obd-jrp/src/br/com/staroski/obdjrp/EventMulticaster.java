package br.com.staroski.obdjrp;

import java.util.ArrayList;
import java.util.List;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;

final class EventMulticaster implements ScannerListener {

	private final List<ScannerListener> listeners;

	EventMulticaster() {
		listeners = new ArrayList<>();
	}

	@Override
	public void onError(ELM327Error error) {
		for (ScannerListener listener : listeners) {
			listener.onError(error);
		}
	}

	@Override
	public void onScanned(Scan scannedData) {
		for (ScannerListener listener : listeners) {
			listener.onScanned(scannedData);
		}
	}

	void addListener(ScannerListener listener) {
		listeners.add(listener);
	}

	void removeListener(ScannerListener listener) {
		listeners.remove(listener);
	}
}

package br.com.staroski.obdjrp;

import java.util.ArrayList;
import java.util.List;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;

final class EventMulticaster implements ObdJrpListener {

	private final List<ObdJrpListener> listeners;

	EventMulticaster() {
		listeners = new ArrayList<>();
	}

	@Override
	public void onError(Throwable error) {
		for (ObdJrpListener listener : listeners) {
			listener.onError(error);
		}
	}

	@Override
	public void onFinishPackage(Package dataPackage) {
		for (ObdJrpListener listener : listeners) {
			listener.onFinishPackage(dataPackage);
		}
	}

	@Override
	public void onScanned(Scan scannedData) {
		for (ObdJrpListener listener : listeners) {
			listener.onScanned(scannedData);
		}
	}

	@Override
	public void onStartPackage(Package dataPackage) {
		for (ObdJrpListener listener : listeners) {
			listener.onStartPackage(dataPackage);
		}
	}

	void addListener(ObdJrpListener listener) {
		listeners.add(listener);
	}

	void removeListener(ObdJrpListener listener) {
		listeners.remove(listener);
	}
}

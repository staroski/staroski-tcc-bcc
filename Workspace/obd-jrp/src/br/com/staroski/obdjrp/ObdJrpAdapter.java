package br.com.staroski.obdjrp;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;

public abstract class ObdJrpAdapter implements ObdJrpListener {

	@Override
	public void onError(ELM327Error error) {}

	@Override
	public void onScanned(Scan scannedData) {}
}

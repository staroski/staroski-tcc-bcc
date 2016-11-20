package br.com.staroski.obdjrp;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;

public interface ObdJrpListener {

	public void onError(ELM327Error error);

	public void onScanned(Scan scannedData);
}

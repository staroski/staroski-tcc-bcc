package br.com.staroski.obdjrp.obd2;

public interface OBD2Listener {

	public void onError(Exception error);

	public void onUpdate(OBD2DataPackage dataPackage);
}

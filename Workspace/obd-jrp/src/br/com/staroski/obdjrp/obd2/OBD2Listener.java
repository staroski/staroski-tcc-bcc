package br.com.staroski.obdjrp.obd2;

public interface OBD2Listener {

	public void onError(Throwable error);

	public void onFinishPackage(OBD2Package dataPackage);

	public void onScanned(OBD2Scan scannedData);

	public void onStartPackage(OBD2Package dataPackage);
}

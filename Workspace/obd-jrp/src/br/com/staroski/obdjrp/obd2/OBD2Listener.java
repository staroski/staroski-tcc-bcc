package br.com.staroski.obdjrp.obd2;

public interface OBD2Listener {

	public void onError(Exception error);

	public void onFinishPackage(OBD2DataPackage dataPackage);

	public void onScanned(OBD2DataScan scannedData);

	public void onStartPackage(OBD2DataPackage dataPackage);
}

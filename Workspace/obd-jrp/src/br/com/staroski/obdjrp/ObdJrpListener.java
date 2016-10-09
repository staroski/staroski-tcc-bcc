package br.com.staroski.obdjrp;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;

public interface ObdJrpListener {

	public void onError(Throwable error);

	public void onFinishPackage(Package dataPackage);

	public void onScanned(Scan scannedData);

	public void onStartPackage(Package dataPackage);
}

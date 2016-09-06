package br.com.staroski.obdjrp.elm327;

import java.util.List;

public interface VehicleListener {

	public void onDataReceived(List<VehicleData> data);
}

package br.com.staroski.obdjrp.obd2;

import java.util.List;

public interface OBD2Listener {

	public void onError(Exception error);

	public void onUpdate(List<OBD2Data> data);
}

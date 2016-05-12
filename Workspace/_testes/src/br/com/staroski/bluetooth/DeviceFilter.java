package br.com.staroski.bluetooth;

import javax.bluetooth.RemoteDevice;

public interface DeviceFilter {

	public boolean accept(RemoteDevice device);
}

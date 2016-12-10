package br.com.staroski.obdjrp.bluetooth;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

abstract class DiscoveryAdapter implements DiscoveryListener {

	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {}

	@Override
	public void inquiryCompleted(int discType) {}

	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {}

	@Override
	public void serviceSearchCompleted(int transID, int respCode) {}
}

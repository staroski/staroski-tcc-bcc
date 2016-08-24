/*
 * Created on 01.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test;

import javax.bluetooth.*;

/**
 * @author gmelin
 *
 * 
 */
public class ServiceFindTest implements DiscoveryListener {

	private boolean inqFinished = false;
	
	public ServiceFindTest(String uuidS) throws Exception {
		
		DiscoveryAgent da = LocalDevice.getLocalDevice().getDiscoveryAgent();
		
		da.startInquiry(DiscoveryAgent.GIAC, this);
		
		while (!inqFinished) {
			synchronized (this) { wait (1000); }
		}
		
		System.out.println ("Starting service select");
		
		UUID uuid = null;
		if (uuidS == null) uuid = new UUID (0x1101);
		else uuid = new UUID (uuidS, false);
		String url = da.selectService(uuid, ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		
		System.out.println (url);
		
	}
	
	public static void main(String[] args) throws Exception {
		new ServiceFindTest(args.length > 0 ? args[0] : null);
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
	 */
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
	 */
	public void serviceSearchCompleted(int transID, int respCode) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
	 */
	public synchronized void inquiryCompleted(int discType) {
		inqFinished = true;
		notifyAll();
	}
}

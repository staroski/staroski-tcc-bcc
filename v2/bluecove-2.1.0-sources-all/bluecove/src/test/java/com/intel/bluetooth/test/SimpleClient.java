/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2004 Intel Corporation
 * 
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 *  @version $Id: SimpleClient.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */ 
package com.intel.bluetooth.test;

import javax.microedition.io.Connector;
import java.io.DataOutputStream;

import javax.bluetooth.DataElement;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.BluetoothStateException;
import java.io.IOException;
import javax.microedition.io.StreamConnection;
import java.util.Enumeration;
import java.util.Vector;

public class SimpleClient implements DiscoveryListener  {

	static final UUID uuid = new UUID(Consts.TEST_UUID, false);
	//static final UUID uuid = com.intel.bluetooth.BluetoothConsts.RFCOMM_PROTOCOL_UUID;
	//static final UUID uuid = com.intel.bluetooth.BluetoothConsts.L2CAP_PROTOCOL_UUID;

	Vector devices;

	Vector records;
	
	CancelThread cancelThread;
	
	class CancelThread extends Thread {
		
		SimpleClient client;

		boolean inquiryCompleted;
		
		CancelThread(SimpleClient client) {
			this.client = client;
			this.inquiryCompleted = false;
		}

		public void run() {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!this.inquiryCompleted) {
				System.out.println("cancelling inquiry on timeout");

				try {
					if (LocalDevice.getLocalDevice().getDiscoveryAgent().cancelInquiry(client)) {
						System.out.println("inquiry cancelled");		
					}
				} catch (BluetoothStateException bse) {
					System.out.println("Got BluetoothStateException: " + bse);
				}
			}
		}
	}

	public SimpleClient(String message) {
		
		devices = new Vector();

		cancelThread = new CancelThread(this); 
		cancelThread.start();

		synchronized (this) {
			try {
				LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
		}

		for (Enumeration enum_d = devices.elements(); enum_d.hasMoreElements();) {
			RemoteDevice d = (RemoteDevice) enum_d.nextElement();
			if (!EnvSettings.isTestAddress(d.getBluetoothAddress())) {
				continue;
			}
			try {
				System.out.println("discovered name: " + d.getFriendlyName(false));
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("discovered address: " + d.getBluetoothAddress());

			synchronized (this) {
				records = new Vector();

				try {
					LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(new int[] { 0x0100, 0x0101, 0x0A0, 0x0A1, 0x0A2, 0x0A3, 0x0A4 },
							new UUID[] { uuid }, d, this);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} catch (BluetoothStateException e) {
					e.printStackTrace();
				}
			}

			/*
			 * 
			 * BUGBUG: need to give the system time to sort itself out after
			 * doing a service attribute request
			 * 
			 */

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (Enumeration enum_r = records.elements(); enum_r.hasMoreElements();) {
				ServiceRecord r = (ServiceRecord) enum_r.nextElement();
				
				String name = null;
				DataElement nameDataElement = r.getAttributeValue(0x0100);
				if (nameDataElement != null) {
					name = nameDataElement.getValue().toString();
				}
				System.out.println("Name attribute: " + name);

				String url = r.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				System.out.println("url: " + url);
				
				if ((name != null) && name.startsWith(Consts.TEST_SERVERNAME_PREFIX)) {
					try {
						StreamConnection conn = (StreamConnection) Connector.open(url);
						DataOutputStream dos = new DataOutputStream(conn.openOutputStream());

						System.out.println("Sending message");
						dos.writeUTF(message);

						dos.close();
						conn.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Not a BlueCove ServiceRecord " + r);					
				}
			}
		}
	}

	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		devices.addElement(btDevice);
		System.out.println("deviceDiscovered " + btDevice.getBluetoothAddress() + " DeviceClass: " + ((Object)cod).toString());
	}

	public synchronized void inquiryCompleted(int discType) {
		String txt;
		switch (discType) {
			case INQUIRY_COMPLETED: txt = "INQUIRY_COMPLETED";  break;
			case INQUIRY_TERMINATED: txt = "INQUIRY_TERMINATED";  break;
			case INQUIRY_ERROR: txt = "INQUIRY_ERROR";  break;
			default:txt = "n/a";
		}
		
		System.out.println("inquiry completed: discType = " + discType + " " + txt);
		cancelThread.inquiryCompleted = true;
		notifyAll();
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for (int i = 0; i < servRecord.length; i++) {
			records.addElement(servRecord[i]);
		}
	}

	public synchronized void serviceSearchCompleted(int transID, int respCode) {
		String txt;
		switch (respCode) {
			case SERVICE_SEARCH_COMPLETED: txt = "SERVICE_SEARCH_COMPLETED"; break;
			case SERVICE_SEARCH_TERMINATED: txt = "SERVICE_SEARCH_TERMINATED";  break;
			case SERVICE_SEARCH_ERROR: txt = "SERVICE_SEARCH_ERROR";  break;
			case SERVICE_SEARCH_NO_RECORDS: txt = "SERVICE_SEARCH_NO_RECORDS";  break;
			case SERVICE_SEARCH_DEVICE_NOT_REACHABLE: txt = "SERVICE_SEARCH_DEVICE_NOT_REACHABLE";  break;
			default: txt = "n/a";
		}
		System.out.println("service search completed: respCode = " + respCode + " " + txt);
		notifyAll();
	}
	
	public static void main(String[] args) {
		
		EnvSettings.setSystemProperties();
		
		if (args.length == 1) {
			new SimpleClient(args[0]);
		} else {
			System.out.println("syntax: SimpleClient <message>");
			new SimpleClient("bluecove test message");
		}
	}

}
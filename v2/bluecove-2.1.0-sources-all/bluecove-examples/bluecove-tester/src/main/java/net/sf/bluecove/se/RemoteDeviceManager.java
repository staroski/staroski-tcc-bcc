/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2008 Vlad Skarzhevskyy
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
 *  @author vlads
 *  @version $Id: RemoteDeviceManager.java 2607 2008-12-17 23:51:33Z skarzhevskyy $
 */
package net.sf.bluecove.se;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

import org.bluecove.tester.log.Logger;


/**
 * 
 */
public class RemoteDeviceManager {

	public static void retrieveDevices(int option) {
		try {
			DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
			RemoteDevice[] devices = discoveryAgent.retrieveDevices(option);
			String optStr = "";
			switch (option) {
			case DiscoveryAgent.CACHED:
				optStr = "CACHED";
				break;
			case DiscoveryAgent.PREKNOWN:
				optStr = "PREKNOWN";
				break;
			}
			if (devices == null) {
				Logger.debug("no devices meet the " + optStr + " criteria");
				return;
			}
			Logger.debug("-- List of  " + optStr + " remote devices");
			for (int i = 0; i < devices.length; i++) {
				StringBuffer b = new StringBuffer();
				b.append(devices[i].getBluetoothAddress()).append(' ');
				boolean trusted = devices[i].isTrustedDevice();
				b.append("Trusted:").append(trusted).append(' ');
				// Padding with spaces for English
				if (trusted) {
					b.append(' ');
				}
				boolean authenticated = devices[i].isAuthenticated();
				b.append("Authenticated:").append(authenticated).append(' ');
				// Padding with spaces for English
				if (authenticated) {
					b.append(' ');
				}
				try {
					b.append(devices[i].getFriendlyName(false));
				} catch (IOException e) {
				}
				Logger.debug(b.toString());
			}

		} catch (BluetoothStateException e) {
			Logger.error("retrieve", e);
		}

	}
}

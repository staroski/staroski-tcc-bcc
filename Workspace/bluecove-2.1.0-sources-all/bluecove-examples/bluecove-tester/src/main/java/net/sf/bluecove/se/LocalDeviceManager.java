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
 *  @version $Id: LocalDeviceManager.java 2607 2008-12-17 23:51:33Z skarzhevskyy $
 */
package net.sf.bluecove.se;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;

import org.bluecove.tester.log.Logger;

import net.sf.bluecove.Configuration;
import net.sf.bluecove.TestResponderServer;

import com.intel.bluetooth.BlueCoveImpl;

/**
 * 
 */
public class LocalDeviceManager {

	private static Object threadLocalBluetoothStackWINSOCK;

	private static Object threadLocalBluetoothStackWIDCOMM;

	private static Object threadLocalBluetoothStack[] = new Object[7];

	public static void setNotDiscoverable() {
		TestResponderServer.setNotDiscoverable();
		getDiscoverable();
	}

	public static void setDiscoverableGIAC() {
		TestResponderServer.setDiscoverable(DiscoveryAgent.GIAC);
		getDiscoverable();
	}

	public static void setDiscoverableLIAC() {
		TestResponderServer.setDiscoverable(DiscoveryAgent.LIAC);
		getDiscoverable();
	}

	public static void getDiscoverable() {
		try {
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			int mode = localDevice.getDiscoverable();
			Logger.debug("LocalDevice " + localDevice.getBluetoothAddress() + " Discoverable "
					+ TestResponderServer.discoverableModeString(mode));
		} catch (Throwable e) {
			Logger.error("getDiscoverable error", e);
		}
	}

	public static void setThreadLocalBluetoothStack(Object id) {
		if (id != null) {
			try {
				BlueCoveImpl.setThreadBluetoothStackID(id);
			} catch (Throwable e) {
				Logger.error("error", e);
			}
		}
	}

	public static void shutdown() {
		BlueCoveImpl.shutdown();
		Logger.info("shutdown finished");
	}

	public static void shutdownThreadLocal() {
		if (Configuration.threadLocalBluetoothStack != null) {
			BlueCoveImpl.shutdownThreadBluetoothStack();
			Configuration.threadLocalBluetoothStack = null;
			Logger.info("ThreadLocal shutdown finished");
		} else {
			Logger.info("no ThreadLocal stack");
		}
	}

	public static void setUseWINSOCK() {
		if (threadLocalBluetoothStackWINSOCK == null) {
			try {
				BlueCoveImpl.useThreadLocalBluetoothStack();
				BlueCoveImpl.setThreadBluetoothStackID(null);
				BlueCoveImpl.setConfigProperty("bluecove.stack", "winsock");
				threadLocalBluetoothStackWINSOCK = BlueCoveImpl.getThreadBluetoothStackID();
			} catch (Throwable e) {
				Logger.error("error", e);
				return;
			}
		}
		Logger.info("will use stack " + threadLocalBluetoothStackWINSOCK);
		Configuration.threadLocalBluetoothStack = threadLocalBluetoothStackWINSOCK;
		setThreadLocalBluetoothStack(Configuration.threadLocalBluetoothStack);
	}

	public static void setUseWIDCOMM() {
		if (threadLocalBluetoothStackWIDCOMM == null) {
			try {
				BlueCoveImpl.useThreadLocalBluetoothStack();
				BlueCoveImpl.setThreadBluetoothStackID(null);
				BlueCoveImpl.setConfigProperty("bluecove.stack", "widcomm");
				threadLocalBluetoothStackWIDCOMM = BlueCoveImpl.getThreadBluetoothStackID();
			} catch (Throwable e) {
				Logger.error("error", e);
				return;
			}
		}
		Logger.info("will use stack " + threadLocalBluetoothStackWIDCOMM);
		Configuration.threadLocalBluetoothStack = threadLocalBluetoothStackWIDCOMM;
		setThreadLocalBluetoothStack(Configuration.threadLocalBluetoothStack);
	}

	public static void setUseDevice(int id) {
		if (threadLocalBluetoothStack[id] == null) {
			try {
				BlueCoveImpl.useThreadLocalBluetoothStack();
				BlueCoveImpl.setThreadBluetoothStackID(null);
				BlueCoveImpl.setConfigProperty("bluecove.deviceID", String.valueOf(id));
				threadLocalBluetoothStack[id] = BlueCoveImpl.getThreadBluetoothStackID();
			} catch (Throwable e) {
				Logger.error("error", e);
				return;
			}
		}
		Logger.info("new Threads will use stack " + threadLocalBluetoothStack[id]);
		Configuration.threadLocalBluetoothStack = threadLocalBluetoothStack[id];
		setThreadLocalBluetoothStack(Configuration.threadLocalBluetoothStack);
	}

}

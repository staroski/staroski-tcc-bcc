/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2006-2008 Vlad Skarzhevskyy
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
 *  @version $Id: NativeTestCase.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package com.intel.bluetooth;

import junit.framework.TestCase;

/**
 * Base class for test cases that are calling native function.
 *
 * Native Debug automatically enabled when running tests in Eclipse
 *
 */
public abstract class NativeTestCase extends TestCase {

	// Use this to debug tests
	protected boolean debug = false;

	protected boolean debugOnInEclipse = true;

	protected void setUp() throws Exception {
		super.setUp();

		boolean eclipse = isEclipse();

		// Use this to avoid project refresh in Eclipse after dll build in VC
		if (eclipse) {
			System.getProperties().put("bluecove.native.path", "./src/main/resources");
		}

		if (eclipse && debugOnInEclipse) {
			debug = true;
		}

		// Use this to debug tests
		if (debug) {
			System.getProperties().put("bluecove.debug", "true");
			BlueCoveImpl.instance().enableNativeDebug(true);
		}

		if (NativeLibLoader.getOS() == NativeLibLoader.OS_MAC_OS_X) {
			if (!NativeLibLoader.isAvailable(BlueCoveImpl.NATIVE_LIB_OSX)) {
				throw new Error("Can't load DLL");
			}
		} else if (needDllWIDCOMM()) {
			if (!NativeTestInterfaces.loadDllWIDCOMM()) {
				throw new Error("Can't load DLL");
			}
		} else {
			if (!NativeTestInterfaces.loadDllMS()) {
				throw new Error("Can't load DLL");
			}
		}
	}

	boolean isEclipse() {
		StackTraceElement[] ste = new Throwable().getStackTrace();
		return (ste[ste.length - 1].getClassName().startsWith("org.eclipse.jdt"));
	}

	protected boolean needDllWIDCOMM() {
		return false;
	}
}

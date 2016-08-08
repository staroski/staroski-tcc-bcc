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
 *  @version $Id: EnvSettings.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */

package com.intel.bluetooth.test;

public abstract class EnvSettings {

	public static void setSystemProperties() {
		
		//System.getProperties().put("bluecove.debug", "true");
		
		// Used to avoid refresh in Eclipse during development
		//System.getProperties().put("bluecove.native.path", "./src/main/resources");
	}
	
	public static boolean isTestAddress(String bluetoothAddress) {
		// Only one device during development tests
		if (false) {
			//return bluetoothAddress.equalsIgnoreCase("0019639c4007");
			//return bluetoothAddress.equalsIgnoreCase("00123755ae71");
		}
		return true;
	}
}

/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *  @version $Id: System.java 2607 2008-12-17 23:51:33Z skarzhevskyy $
 */ 
package BluetoothTCKAgent;

import org.bluecove.tester.log.Logger;

/**
 * 
 * Small hack to redirect logs to application console.
 *
 */
public class System {
	
	public static Printer out = new Printer();
	
	public static class Printer {
		
		public void println(String message) {
			Logger.debug(message);
		}
		
		public void println(int message) {
			Logger.debug(String.valueOf(message));
		}
	}
	
	public static String getProperty(String key) {
		return java.lang.System.getProperty(key);
	}
	
}

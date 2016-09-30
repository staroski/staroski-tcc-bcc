/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2006-2009 Vlad Skarzhevskyy
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
 *  @author dmunsie
 *  @version $Id: IBMJ9Helper.java 2994 2009-06-23 06:04:41Z skarzhevskyy $
 */
package com.intel.bluetooth;

import com.ibm.oti.vm.VM;

import java.io.IOException;

/**
 *
 * IBM J9 helper module.
 *
 * <p>
 * <b><u>Your application should not use this class directly.</u></b>
 */
class IBMJ9Helper {
	
	static synchronized void loadLibrary(String libname) throws IOException {
		VM.loadLibrary(libname);
	}
	
	static void addShutdownClass(Runnable hook) {
		VM.addShutdownClass(hook);
	}
}

/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2008 Michael Lifshits
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
 *  @version $Id: EmuServer.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package com.intel.bluetooth.emu;

import com.intel.bluetooth.rmi.Server;

public class EmuServer /* extends HTTPServer */{

	// private final static Logger logger = Logger.getLogger(EmuServer.class);
	//
	// public EmuServer() throws Exception {
	// super();
	// }
	//

	public static void main(String[] args) throws Exception {
		// try {
		// new EmuServer().start();
		// } catch (IOException ioe) {
		// logger.error("Couldn't start server:", ioe);
		// System.exit(-1);
		// }

		Server.main(args);
	}

}

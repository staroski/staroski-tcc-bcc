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
 *  @version $Id: SafeRunnable.java 2607 2008-12-17 23:51:33Z skarzhevskyy $
 */
package org.bluecove.tester.util;

import org.bluecove.tester.log.Logger;

public class SafeRunnable implements Runnable {

	private final String name;

	private final Runnable target;

	public SafeRunnable(Runnable target, String name) {
		this.target = target;
		this.name = name;
	}

	public SafeRunnable(Runnable target) {
		this(target, "");
	}

	public void run() {
		try {
			target.run();
		} catch (Throwable e) {
			Logger.error(name + ".run()", e);
		}

	}

}

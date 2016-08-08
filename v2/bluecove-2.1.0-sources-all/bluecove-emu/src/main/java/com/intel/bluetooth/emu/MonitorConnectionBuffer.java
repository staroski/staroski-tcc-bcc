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
 *  @version $Id: MonitorConnectionBuffer.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package com.intel.bluetooth.emu;

/**
 * 
 */
public class MonitorConnectionBuffer implements MonitorItem {

	private static final long serialVersionUID = 1L;

	long readBytes;

	long writeBytes;

	long readOperations;

	long writeOperations;

	long closedTimeStamp;

	long readTimeStamp;

	long writeTimeStamp;

	public long getReadBytes() {
		return readBytes;
	}

	public long getWriteBytes() {
		return writeBytes;
	}

	public long getReadOperations() {
		return readOperations;
	}

	public long getWriteOperations() {
		return writeOperations;
	}

	public long getClosedTimeStamp() {
		return closedTimeStamp;
	}

	public long getReadTimeStamp() {
		return readTimeStamp;
	}

	public long getWriteTimeStamp() {
		return writeTimeStamp;
	}
}

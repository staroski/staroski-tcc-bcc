/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2008-2009 Michael Lifshits
 *  Copyright (C) 2008-2009 Vlad Skarzhevskyy
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
 *
 *  @version $Id: EmulatorUtils.java 2917 2009-03-13 17:16:12Z skarzhevskyy $
 */
package com.intel.bluetooth.emu;

import java.util.Arrays;
import java.util.Set;
import java.util.Vector;

import javax.bluetooth.DiscoveryAgent;

/**
 * 
 */
public class EmulatorUtils {

	public static long getNextAvailable(Vector<Long> handles, long firstAvalable, int step) {
		return getNextAvailable(handles.toArray(new Long[handles.size()]), firstAvalable, step);
	}

	public static long getNextAvailable(Set<Long> handles, long firstAvalable, int step) {
		return getNextAvailable(handles.toArray(new Long[handles.size()]), firstAvalable, step);
	}

	public static long getNextAvailable(Long[] handles, long firstAvalable, int step) {
		if (handles.length == 0) {
			return firstAvalable;
		}
		Arrays.sort(handles);
		for (int i = 0; i < handles.length; i++) {
			long expect = firstAvalable + i * step;
			if (((Long) handles[i]).longValue() != expect) {
				return expect;
			}
		}
		return ((Long) handles[handles.length - 1]).longValue() + step;
	}

	public static String discoverableModeString(int mode) {
		if (DiscoveryAgent.GIAC == mode) {
			return "GIAC";
		} else if (DiscoveryAgent.LIAC == mode) {
			return "LIAC";
		} else if (DiscoveryAgent.NOT_DISCOVERABLE == mode) {
			return "NOT_DISCOVERABLE";
		} else {
			return "0x" + Integer.toHexString(mode);
		}
	}
}

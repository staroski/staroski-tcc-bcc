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
 *  @version $Id: UUIDTest.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package javax.bluetooth;

import junit.framework.TestCase;

/**
 * 
 */
public class UUIDTest extends TestCase {

	public void testUUIDConstructor() {
		assertEquals("obex UUID", new UUID("0000110500001000800000805f9b34fb", false), new UUID(0x1105));
		assertEquals("UUID32", new UUID("2130110700001000800000805f9b34fb", false), new UUID(0x21301107));
	}
}

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
 *  @version $Id: BooleanVar.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */

package net.sf.bluecove.util;

/**
 * Class used instead of boolean since there are no reflection in J2ME. 
 */
public class BooleanVar {
	
	private boolean value;
	
	public BooleanVar() {
		this.value = false;
	}
	
	public BooleanVar(boolean value) {
		this.value = value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}
	
	public boolean booleanValue() {
		return this.value;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof BooleanVar) {
			return ((BooleanVar)obj).value = this.value;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return (new Boolean(value)).toString();
	}
}

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
 *  @version $Id: ConnectedOutputStream.java 2578 2008-12-14 05:29:37Z skarzhevskyy $
 */
package com.intel.bluetooth.emu;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 */
class ConnectedOutputStream extends OutputStream {

	private ConnectedInputStream receiver;

	public ConnectedOutputStream(ConnectedInputStream receiver) {
		this.receiver = receiver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[],int,int)
	 */
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		receiver.receive(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		receiver.receive(b);
	}
	
	public void flush() throws IOException {
	    receiver.receiveFlush();
    }

	@Override
	public void close() throws IOException {
		receiver.receiverClose();
	}

}

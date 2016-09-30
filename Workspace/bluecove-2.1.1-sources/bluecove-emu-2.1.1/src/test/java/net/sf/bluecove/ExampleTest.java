/**
 *  BlueCove - Java library for Bluetooth
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
 *  @version $Id: ExampleTest.java 2917 2009-03-13 17:16:12Z skarzhevskyy $
 */
package net.sf.bluecove;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.intel.bluetooth.EmulatorTestsHelper;

/**
 * 
 */
public class ExampleTest extends TestCase {

	private static final UUID uuid = new UUID(0x2108);

	private Thread serverThread;

	private static final String echoGreeting = "I echo";

	protected void setUp() throws Exception {
		super.setUp();
		EmulatorTestsHelper.startInProcessServer();
		EmulatorTestsHelper.useThreadLocalEmulator();
		serverThread = EmulatorTestsHelper.runNewEmulatorStack(new EchoServerRunnable());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if ((serverThread != null) && (serverThread.isAlive())) {
			serverThread.interrupt();
			serverThread.join();
		}
		EmulatorTestsHelper.stopInProcessServer();
	}

	private class EchoServerRunnable implements Runnable {

		public void run() {

			StreamConnectionNotifier service = null;

			try {
				String url = "btspp://localhost:" + uuid.toString() + ";name=TServer";
				service = (StreamConnectionNotifier) Connector.open(url);

				StreamConnection conn = (StreamConnection) service.acceptAndOpen();

				System.out.println("Server received connection");

				DataOutputStream dos = conn.openDataOutputStream();
				DataInputStream dis = conn.openDataInputStream();

				dos.writeUTF(echoGreeting);
				dos.flush();

				String received = dis.readUTF();
				System.out.println("Server received:" + received);

				dos.writeUTF(received);
				dos.flush();

				dos.close();
				dis.close();

				conn.close();
			} catch (Throwable e) {
				System.err.print(e.toString());
				e.printStackTrace();
			} finally {
				if (service != null) {
					try {
						service.close();
					} catch (IOException ignore) {
					}
				}
			}
		}
	}

	public void testConnection() throws Exception {
		DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		// Find service
		String serverURL = discoveryAgent.selectService(uuid, ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		Assert.assertNotNull("service not found", serverURL);

		StreamConnection conn = null;
		try {
			conn = (StreamConnection) Connector.open(serverURL);
			DataOutputStream dos = conn.openDataOutputStream();
			DataInputStream dis = conn.openDataInputStream();

			String received = dis.readUTF();
			Assert.assertEquals("handshake", echoGreeting, received);

			String message = "TestMe";
			System.out.println("Client Sending message:" + message);
			dos.writeUTF(message);

			received = dis.readUTF();
			Assert.assertEquals("echo", received, message);

			dos.close();
			dis.close();

		} catch (IOException e) {
			System.err.print(e.toString());
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (IOException ignore) {
				}
			}
		}
	}
}

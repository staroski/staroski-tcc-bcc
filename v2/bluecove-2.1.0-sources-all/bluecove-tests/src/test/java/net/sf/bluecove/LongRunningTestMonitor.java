/**
 *  BlueCove - Java library for Bluetooth
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
 *  @version $Id: LongRunningTestMonitor.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package net.sf.bluecove;

/**
 * 
 */
public class LongRunningTestMonitor extends Thread {

	private boolean testFinished = false;

	private Thread testThread;

	private int gracePeriod = 0;

	private String testName;

	LongRunningTestMonitor(int gracePeriod, String testName) {
		super("TestMonitor");
		this.gracePeriod = gracePeriod;
		this.testName = testName;
		testThread = Thread.currentThread();
	}

	public void run() {
		try {
			sleep(gracePeriod);
		} catch (InterruptedException e) {
			return;
		}

		int count = 0;
		while (!testFinished) {

			try {
				sleep(4 * 1000);
			} catch (InterruptedException e) {
				return;
			}

			if (!testFinished) {
				System.out.println("Long running test " + testName + " detected in thread:" + testThread.getName());
				StackTraceElement[] ste = testThread.getStackTrace();
				StringBuffer buf = new StringBuffer();
				buf.append("stack trace:\n");
				for (int i = 0; i < ste.length; i++) {
					buf.append("\t").append(ste[i].toString()).append('\n');
					if (ste[i].getClassName().startsWith("junit.framework")) {
						break;
					}
				}
				System.out.println(buf.toString());
				count++;
				if (count > 4) {
					System.out.println("Sending ThreadDeath");
					testThread.stop();
					break;
				} else {
					System.out.println("Sending InterruptedException");
					testThread.interrupt();
				}
			}
		}
	}

	public void finish() {
		testFinished = true;
		interrupt();
	}

}

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
 *  @version $Id: Console.java 2607 2008-12-17 23:51:33Z skarzhevskyy $
 */
package net.sf.bluecove.se;

import java.io.IOException;

import org.bluecove.tester.log.Logger;

import net.sf.bluecove.Configuration;
import net.sf.bluecove.Switcher;

/**
 * 
 */
public class Console {

	public static void main(String[] args) {
		JavaSECommon.initOnce();
		Configuration.storage = new FileStorage();
		help();

		while (true) {
			try {
				String cmd = readCommand();
				if (cmd == null) {
					quit();
					return;
				}
				if (cmd.length() == 0) {
					help();
					continue;
				}
				cmd = cmd.toUpperCase();
				char user_input = cmd.charAt(0);
				switch (user_input) {
				case 'Q':
					quit();
					break;
				case '\n':
				case '?':
				case 'H':
					help();
					break;
				case '4':
					UIHelper.printFailureLog();
					break;
				case '*':
					Switcher.startDiscovery();
					break;
				case '7':
					Switcher.startServicesSearch();
					break;
				case '2':
					Switcher.startClient();
					break;
				case '3':
					Switcher.clientShutdown();
					break;
				case '5':
					Switcher.startServer();
					break;
				case '6':
					Switcher.serverShutdown();
					break;
				case 'D':
					boolean dbg = BlueCoveSpecific.changeDebug();
					if (dbg) {
						System.out.println("BlueCove Debug ON");
					} else {
						System.out.println("BlueCove Debug OFF");
					}
					break;
				case 'T':
				    Switcher.startTCKAgent();
				    break;
				}
			} catch (IOException e) {
				return;
			}
		}
	}

	private static String readCommand() throws IOException {
		int b = System.in.read();
		if (b == -1) {
			return null;
		}
		return new String("" + (char) b);
	}

	private static void help() {
		System.out.println("BlueCove tester Console application (keyboard codes the same as in MIDP application)");
		System.out.println("\t2 - Start Client");
		System.out.println("\t3 - Stop Client");
		System.out.println("\t5 - Start Server");
		System.out.println("\t6 - Stop Server");
		System.out.println("\t* - Run Discovery");
		System.out.println("\t7 - Services Search");
		System.out.println("\td - toggle BlueCove Debug");
		System.out.println("\tT - Start TCK Agent");
		System.out.println("\tq - Quit");
		System.out.flush();
	}

	private static void quit() {
		Logger.debug("quit");
		Switcher.clientShutdown();
		Switcher.serverShutdownOnExit();
		System.exit(0);
	}
}

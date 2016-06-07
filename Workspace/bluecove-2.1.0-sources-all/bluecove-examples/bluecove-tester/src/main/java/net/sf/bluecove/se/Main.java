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
 *  @version $Id: Main.java 2539 2008-12-10 04:14:55Z skarzhevskyy $
 */
package net.sf.bluecove.se;

import java.awt.GraphicsEnvironment;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import net.sf.bluecove.util.CollectionUtils;

/**
 * This class selects the application UI
 * 
 */
public class Main {

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
		    if (args[i].equalsIgnoreCase("--debug") || args[i].equals("-d")) {
		        System.setProperty("bluecove.debug", "true");
            } else if (args[i].equalsIgnoreCase("--agui")) {
				runAGUI(args);
				return;
			} else if (args[i].equalsIgnoreCase("--swing") || args[i].equals("-s")) {
				runAGUI(args);
				return;
			} else if (args[i].equalsIgnoreCase("--awt") || args[i].equals("-a")) {
				runAWT(args);
				return;
			} else if (args[i].equalsIgnoreCase("--console") || args[i].equals("-c")) {
				runConsole(args);
				return;
			} else if (args[i].equalsIgnoreCase("--help") || args[i].equals("-h")) {
				System.out.println(" --swing|--agui|-s\n --console|-c\n --awt|-a  (--debug)");
				return;
			}
		}

		// printSystemProperties();
		if ("true".equals(System.getProperty("java.awt.headless"))) {
			runConsole(args);
			return;
		}
		if (GraphicsEnvironment.isHeadless()) {
			runConsole(args);
			return;
		}
		String gui = System.getProperty("awt.toolkit");
		if ((gui != null) && (gui.indexOf("AGUI") != -1)) {
			runAGUI(args);
			return;
		}

		String vm = System.getProperty("java.vm.name");
		if ((vm != null) && (vm.indexOf("CDC") != -1)) {
			runAGUI(args);
			return;
		}
		runAWT(args);
	}

	static void runConsole(String[] args) {
		Console.main(args);
	}

	static void runAGUI(String[] args) {
		net.sf.bluecove.swing.Main.main(args);
	}

	static void runAWT(String[] args) {
		net.sf.bluecove.awt.Main.main(args);
	}

	public static void printSystemProperties() {
		StringBuffer sysProperties = new StringBuffer();
		Properties properties = System.getProperties();
		// Sort the list
		Vector list2sort = new Vector();
		int max_key = 0;
		for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext();) {
			Object keyObj = (Object) iterator.next();
			String key = keyObj.toString();
			list2sort.add(key);
			int len = key.length();
			if (len > max_key) {
				max_key = len;
			}
		}
		CollectionUtils.sort(list2sort);
		if (max_key > 41) {
			max_key = 41;
		}

		for (Iterator iterator = list2sort.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			StringBuffer key_p = new StringBuffer(key);
			while (key_p.length() < max_key) {
				key_p.append(" ");
			}
			String value = (String) properties.get(key);
			if (value == null) {
				value = "{null}";
			}
			StringBuffer value_p = new StringBuffer(value);
			value_p.append("]");
			while (value_p.length() < 60) {
				value_p.append(" ");
			}
			sysProperties.append(" \t" + key_p.toString() + " = [" + value_p.toString() + "\n");
		}
		System.out.println("System Properties:\n" + sysProperties.toString());
	}
}

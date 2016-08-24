package de.avetana.bluetooth.util;


/**
 * <b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
 *
 * This file is part of the Avetana bluetooth API for Linux.<br><br>
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version. <br><br>
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.<br><br><br><br>
 *
 *
 * <b>Description: </b><br>The C-functions are "stored" in a library (.so under linux, .dll under Windows), which is
 * part of the Jar archive of the AvetanaBluetooth project. This class extracts the librairy from the jar
 * archive, copies it as a temporary system file and uses this temporary system file to perform function-calls.
 *
 */

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashSet;

import de.avetana.bluetooth.stack.BlueZ;

public class LibLoader {

	private static String btname;

	public static void loadMIDPLib() throws Exception {
		//com.ibm.oti.vm.VM.loadLibrary("avetanaBluetoothCEMS");
	}
	
	public static void loadBTLib () throws Exception {
		btname = loadLib ("avetanaBT");
	}
	
	public synchronized static String loadLib (String name) throws Exception {
		
		String confName = System.getProperty ("microedition.profiles", "");
		String forceLoad = System.getProperty ("avetana.forceNativeLibrary", "false");
		if (confName.indexOf("MIDP") != -1 && !forceLoad.equals("true")) {
			return "";
		}
		
		String libName = name;
		
	    String sysName = System.getProperty("os.name");
		if (name.equals("avetanaBT")) {
		    
		    if (sysName.toLowerCase().indexOf("windows") != -1) {
			    if (sysName.toLowerCase().indexOf("ce") != -1) {
			    	boolean cems = false;
			    	try {
			    		System.loadLibrary("BtSdkCE30");
			    	} catch (Throwable t) {
			    		cems = true;
			    	}
			    	
			    	name = name + "CE" + (cems ? "MS" : "");
			    }
			    else {
			    		String stack = "" + System.getProperty("avetanabt.stack");
			    		stack = System.getProperty("de.avetana.bluetooth.stack", stack);
			    		
			    		int checkHCI = BTCheck.checkHCI();
			    		
			    		if (checkHCI == 0) throw new Exception ("No supported stack installed or no dongle available");
			    		
			    		if ((checkHCI & 1) == 0 || ((stack.equalsIgnoreCase("microsoft") || stack.equalsIgnoreCase("ms")) && (checkHCI & 2) == 2)) name += "MS";
			    		
			    }
			    libName = name;
			    name = name + ".dll";
		    }
		    else if (sysName.toLowerCase().indexOf("linux") != -1) name = "lib" + name + ".so";
		    else if (sysName.toLowerCase().indexOf("mac os x") != -1) name = "lib" + name + ".jnilib";
		    else throw new Exception ("Unsupported operating system" + sysName);
		} else name = System.mapLibraryName(name);
		
	    InputStream is = getResourceAsStream(name);
	    if (is == null) is = getResourceAsStream("/" + name);
	    
		if (is == null) {
			try {
				System.out.println ("Loading library " + libName + " from ld.library.path");
				System.loadLibrary(libName);
				return libName;
			} catch (Throwable e) {
				throw new Exception ("Native Library " + libName + " not in CLASSPATH !");
			}
		}

	    File fd = null;
	    String tmppath = System.getProperty ("java.io.tmpdir");
	    
	    if (sysName.toLowerCase().indexOf("windows") != -1 && sysName.toLowerCase().indexOf("ce") != -1 && (tmppath == null || tmppath.endsWith("null")))
	    		tmppath = File.separator + "Temp";
	    		
	    while (fd == null) {
	      try {
	      	do {
	      		int count = (int)(100000f * Math.random());
	      		fd = new File (tmppath, "abt" + count);
	      	} while (fd.exists());
	      } catch (Exception e) { e.printStackTrace(); System.err.println ("Writing of temp lib-file failed " + fd.getAbsolutePath()); }
	    }
	    String path = fd.getAbsolutePath();
	    fd.delete();
	    final File f = new File(path);
	    f.mkdirs();
	    	
	    final File f2 = new File (f, name);
//		System.out.println ("Storing library in " + f2.getAbsolutePath());
	    FileOutputStream fos = new FileOutputStream (f2);

	    byte[] b = new byte[1000];
	    int len, tlen = 0;
	    while ((len = is.read(b)) >= 0) {
	      fos.write(b, 0, len);
	      tlen += len;
	    }
//	    System.out.println ("Stored " + tlen + " bytes.");
	    fos.close();
	    //if (sysName.toLowerCase().indexOf("windows") != -1 && sysName.toLowerCase().indexOf("ce") != -1) {
	    //		System.out.println ("System temp path " + System.getProperty("java.io.tmpdir"));
	    //		System.out.println ("System file.separator " + File.separator);
	    //}
	    
//	    System.out.println ("File exists " + f2.exists() + " length " + f2.length());
	    
	    
	    try {
	    		System.load(f2.getAbsolutePath());
	    } catch (UnsatisfiedLinkError e) {
	    		System.out.println ("Could not load own library " + f2.getAbsolutePath() + ". Will try from ld.library.path");
	    		System.loadLibrary(libName);
	    }
	    Runnable r = new Runnable() {
	    		public void run() {
	    			boolean delf1 = f2.delete();
	    			boolean delf2 = f.delete();
	    		}
	    };
	    try {
	    		Runtime.getRuntime().addShutdownHook(new Thread (r));
	    } catch (NoSuchMethodError e3) {
	    		try {
	    			f2.deleteOnExit();
	    			f.deleteOnExit();
	    		} catch (Throwable e) {
	    		}
	    }
	    
	    //Find old instances of the library and delete them
	    
		try {
		    File tmpdir = new File (tmppath);
		    if (tmpdir.isDirectory()) {
		    		File listSub[] = tmpdir.listFiles();
		    		for (int i = 0;i < listSub.length;i++) {
		    				if (listSub[i].isDirectory() && listSub[i].getName().startsWith("abt") &&
		    						listSub[i].listFiles()[0].getName().equalsIgnoreCase(name) && !listSub[i].listFiles()[0].equals(f2) ) {
		    							listSub[i].listFiles()[0].delete();
		    							listSub[i].delete();
		    						}
		    		}
		    }
		} catch (Throwable e) {}
	    
	    return name;
	    
	   }

	public static boolean tryload(String name) {
		try { 
			System.loadLibrary(name); 
		} catch (Throwable e) { 
			if (System.getProperty("btdebug", "false").equals("true")) e.printStackTrace();
			return false; 
		}
		
		return true;
	}

	/**
	 * @return Returns the name.
	 */
	public static String getName() {
		return btname;
	}

	public static void main(String args[]) {
		System.out.println (System.getProperty ("os.name"));
	}

	public static InputStream getResourceAsStream(String name) {
		try {
			InputStream is = LibLoader.class.getResourceAsStream(name);
			if (is == null) {
				is = LibLoader.class.getClassLoader().getResourceAsStream(name);
			}
			return is;
		} catch (NullPointerException e) { return null; }
	}

	public static HashSet cremeThreads = new HashSet();
	private static boolean cremeError = false;
	
	/**
	 * Call CrEme's Noblock.on() method to better handle native calls
	 * @param runnable 
	 *
	 */
	public static void cremeInit(Runnable runnable) {
		if (cremeError || System.getProperty("creme.noblock", "false").equals("false") || cremeThreads.contains(runnable)) return;
		Debug ("LibLoader::cremeInit " + runnable);
		try {
			 Class c = Class.forName("creme.Noblock");
			 if (c != null) {
				 Method m = c.getMethod("on", new Class[0]);
				 m.invoke(c, new Object[0]);
 			 	 cremeThreads.add(runnable);
			 } else cremeError = true;
		 } catch (Throwable e) {
			 e.printStackTrace();
			 cremeError = true;
		 }
	}

	private static boolean debug = System.getProperty("de.avetana.bluetooth.debug", "false").equals("true");
	
	public static void Debug(String string) {
		if (debug)
			System.out.println (((System.currentTimeMillis() % 100000) / 1000) + " - " + string);
	}

	public static void cremeOut(Runnable runnable) {
		if (cremeError || System.getProperty("creme.noblock", "false").equals("false") || !cremeThreads.contains(runnable)) return;
		cremeThreads.remove(runnable);
		try {
			 Class c = Class.forName("creme.Noblock");
			 if (c != null) {
				 Method m = c.getMethod("off", new Class[0]);
				 m.invoke(c, new Object[0]);
			 	 cremeThreads.add(runnable);
			 } else cremeError = true;
		 } catch (Throwable e) {
			 e.printStackTrace();
			 cremeError = true;
		 }
	}

	public static void DebugT(String string) {
		if (debug) {
			new Throwable().printStackTrace();
			System.out.println (((System.currentTimeMillis() % 100000) / 1000) + " - " + string);
		}
	}
	
}

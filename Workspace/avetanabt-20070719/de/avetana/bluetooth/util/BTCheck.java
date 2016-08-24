package de.avetana.bluetooth.util;

/**
 * Class used in windows to check if dongles are available to the MS or Widcomm stacks
 * @author gmelin
 *
 *
 */
public class BTCheck {

	private static boolean loadedMS = false, loadedWC = false;
	static {
		try {
//			System.out.println ("Trying to load BTCheckMS");
			LibLoader.loadLib("BTCheckMS");
//			System.out.println ("Loaded MS");
			loadedMS = true;
		} catch (Throwable e) {
			if (System.getProperty("btdebug", "false").equals("true")) e.printStackTrace();
		}
		try {
//			System.out.println ("Trying to load BTCheckWC");
			if (LibLoader.tryload("wbtapi")) {
				LibLoader.loadLib("BTCheckWC");
//			System.out.println ("Loaded WC");
				loadedWC = true;
			}
		} catch (Throwable e) {
			if (System.getProperty("btdebug", "false").equals("true")) e.printStackTrace();
		}
	}
	
	private static native int checkHCIMS();
	private static native int checkHCIWC();
	
	
	/**
	 * Returns 1 if the Widcomm stack has access to a dongle
	 * Returns 2 if the MS-Stack has access to a dongle
	 * Returns 3 if both stacks have access to a dongle
	 * @return
	 */
	public static int checkHCI() {
		int ret = 0;
		if (loadedWC) ret += checkHCIWC();
		if (loadedMS) ret += checkHCIMS();
		return ret;
	}
	
	public static void main (String args[]) {
		if (System.getProperty("btdebug", "false").equals("true"))  System.out.println ("Debugging turned on");
		System.out.println ("Result " + checkHCI() + " hasMS " + loadedMS + " hasWC " + loadedWC);
	}
	
}

package com.sun.cdc.io.j2me.btgoep;

/*
 * Created on 13.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import com.sun.cdc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import java.io.IOException;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Protocol implements ConnectionBaseInterface {

	public Connection openPrim (String name, int mode, boolean timeouts) throws IOException {
		return de.avetana.bluetooth.connection.Connector.open("btgoep:" + name, mode, timeouts);
	}

}

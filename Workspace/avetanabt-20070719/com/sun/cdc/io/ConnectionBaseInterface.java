package com.sun.cdc.io;

/*
 * Created on 13.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import javax.microedition.io.Connection;
import java.io.*;

/**
 * @author gmelin
 *
 * Interface used by SUNs Connector class to get the real Connector based on protocol and
 * Platform name.
 */
public interface ConnectionBaseInterface {
	public Connection openPrim (String name, int mode, boolean timeouts) throws IOException;
}

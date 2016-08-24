package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * Contributed to Avetana bluetooth
 * 
 * Solves Bytecode comatibilty problems. Application compiled with
 * AvetanaBluetooth.jar should run on java me platform.
 * 
 * @author vlads
 */

public class Connector {

	public static final int READ = 1;

	public static final int WRITE = 2;

	public static final int READ_WRITE = 3;

	public static Connection open(String name) throws IOException {
		return de.avetana.bluetooth.connection.Connector.open(name);
	}

	public static Connection open(String name, int mode) throws IOException {
		return de.avetana.bluetooth.connection.Connector.open(name, mode);
	}

	public static Connection open(String name, int mode, boolean timeouts)
			throws IOException {
		return de.avetana.bluetooth.connection.Connector.open(name, mode,
				timeouts);
	}

	public static DataInputStream openDataInputStream(String name)
			throws IOException {
		try {
			// TO-DO better fix throws in avetana
			return de.avetana.bluetooth.connection.Connector
					.openDataInputStream(name);
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new WrappedAvetanaIOException(e);
		}
	}

	public static DataOutputStream openDataOutputStream(String name)
			throws IOException {
		try {
			// TO-DO better fix throws in avetana
			return de.avetana.bluetooth.connection.Connector
					.openDataOutputStream(name);
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new WrappedAvetanaIOException(e);
		}
	}

	public static InputStream openInputStream(String name) throws IOException {
		try {
			// TO-DO better fix throws in avetana
			return de.avetana.bluetooth.connection.Connector
					.openInputStream(name);
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new WrappedAvetanaIOException(e);
		}
	}

	public static OutputStream openOutputStream(String name) throws IOException {
		try {
			// TO-DO better fix throws in avetana
			return de.avetana.bluetooth.connection.Connector
					.openOutputStream(name);
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new WrappedAvetanaIOException(e);
		}
	}

}

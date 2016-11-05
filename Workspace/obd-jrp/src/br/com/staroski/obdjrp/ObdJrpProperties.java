package br.com.staroski.obdjrp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.com.staroski.obdjrp.data.Package;

public final class ObdJrpProperties {

	private static final class Holder {

		public static final ObdJrpProperties INSTANCE;

		static {
			try {
				INSTANCE = new ObdJrpProperties();
			} catch (IOException e) {
				throw new RuntimeException("Could not load " + FILE_NAME, e);
			}
		}
	}

	private static final String FILE_NAME = "obd-jrp.properties";
	private static final String SAVE_PACKAGE_AS_XML = "save_package_as_xml";
	private static final String LOG_ELM327 = "log_elm327";
	private static final String WEB_SERVER = "web_server";
	private static final String VEHICLE = "vehicle";
	private static final String PARSER = "parser_";
	private static final String PACKAGE_MAX_SIZE = "package_max_size";

	private static final String PACKAGE_DIR = "package_dir";

	private static final String CONNECTION_TYPE = "connection_type";

	static final String SOCKET = "socket";
	static final String SOCKET_PORT = "socket_port";
	static final String SOCKET_ADDRESS = "socket_address";

	static final String BLUETOOTH = "bluetooth";
	static final String BLUETOOTH_SERVICE = "bluetooth_service";
	static final String BLUETOOTH_DEVICE = "bluetooth_device";

	public static ObdJrpProperties get() {
		return Holder.INSTANCE;
	}

	private static boolean isTrue(String value) {
		return ObdJrpUtils.isEmpty(value) ? false : "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
	}

	private final ObdJrpConnection connection;

	private final Properties properties;

	private ObdJrpProperties() throws IOException {
		InputStream file = ObdJrpProperties.class.getResourceAsStream("/" + FILE_NAME);
		properties = new Properties();
		properties.load(file);
		String connectionType = checkProperty(CONNECTION_TYPE);
		if (BLUETOOTH.equalsIgnoreCase(connectionType)) {
			connection = new ObdJrpConnectionBluetooth(this);
		} else if (SOCKET.equalsIgnoreCase(connectionType)) {
			connection = new ObdJrpConnectionSocket(this);
		} else {
			String message = String.format("No support for %s of type %s", CONNECTION_TYPE, connectionType);
			throw new IllegalStateException(message);
		}
	}

	public String getBluetoothDevice() {
		return getProperty(BLUETOOTH_DEVICE);
	}

	public String getBluetoothService() {
		return getProperty(BLUETOOTH_SERVICE);
	}

	public ObdJrpConnection getConnection() {
		return connection;
	}

	public File getPackageDir() {
		String value = getProperty(PACKAGE_DIR);
		File dir = ObdJrpUtils.isEmpty(value) ? Package.DEFAULT_DIR : new File(value);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public int getPackageMaxSize() {
		String value = getProperty(PACKAGE_MAX_SIZE);
		return ObdJrpUtils.isEmpty(value) ? Package.DEFAULT_MAX_SIZE : Integer.parseInt(value);
	}

	public String getParser(String pid) {
		return getProperty(PARSER + pid);
	}

	public String getSocketAddress() {
		return getProperty(SOCKET_ADDRESS);
	}

	public String getSocketPort() {
		return getProperty(SOCKET_PORT);
	}

	public String getVehicle() {
		String value = getProperty(VEHICLE);
		return ObdJrpUtils.isEmpty(value) ? Package.UNKNOWN_VEHICLE : value;
	}

	public String getWebServer() {
		return getProperty(WEB_SERVER);
	}

	public boolean isLogELM327() {
		return isTrue(getProperty(LOG_ELM327));
	}

	public boolean isSavePackageAsXml() {
		return isTrue(getProperty(SAVE_PACKAGE_AS_XML));
	}

	private String getProperty(String name) {
		return properties.getProperty(name);
	}

	String checkProperty(String name) {
		String value = getProperty(name);
		if (ObdJrpUtils.isEmpty(value)) {
			throw new IllegalStateException("property \"" + name + "\" not found in " + FILE_NAME);
		}
		return value;
	}
}

package br.com.staroski.obdjrp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.utils.Conversions;

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

	/**
	 * <pre>
	 * DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	 * </pre>
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

	private static final String FILE_NAME = "obd-jrp.properties";

	private static final String SAVE_PACKAGE_AS_XML = "save_package_as_xml";

	private static final String LOG_ELM327 = "log_elm327";

	private static final String WEB_SERVER = "web_server";
	private static final String VEHICLE = "vehicle";
	private static final String DATA_DIR = "data_dir";
	private static final String DATA_MAX_SCANS = "data_max_scans";
	private static final String CONNECTION_TYPE = "connection_type";
	private static final String PARSER = "parser_";
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
		return Conversions.isEmpty(value) ? false : "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
	}

	private final ObdJrpConnection connection;

	private final Properties properties;

	private ObdJrpProperties() throws IOException {
		InputStream file = ObdJrpProperties.class.getResourceAsStream("/" + FILE_NAME);
		properties = new Properties();
		properties.load(file);
		String connectionType = checkProperty(CONNECTION_TYPE);
		if (BLUETOOTH.equalsIgnoreCase(connectionType)) {
			connection = new ConnectionBluetooth(this);
		} else if (SOCKET.equalsIgnoreCase(connectionType)) {
			connection = new ConnectionSocket(this);
		} else {
			String message = String.format("No support for %s of type %s", CONNECTION_TYPE, connectionType);
			throw new IllegalStateException(message);
		}
	}

	public String bluetoothDevice() {
		return getProperty(BLUETOOTH_DEVICE);
	}

	public String bluetoothService() {
		return getProperty(BLUETOOTH_SERVICE);
	}

	public ObdJrpConnection connection() {
		return connection;
	}

	public File dataDir() {
		String value = getProperty(DATA_DIR);
		File dir = Conversions.isEmpty(value) ? Package.DEFAULT_DIR : new File(value);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public int dataMaxScans() {
		String value = getProperty(DATA_MAX_SCANS);
		return Conversions.isEmpty(value) ? Package.DEFAULT_MAX_SIZE : Integer.parseInt(value);
	}

	public String formatted(Date date) {
		return DATE_FORMAT.format(date);
	}

	public boolean loggingELM327() {
		return isTrue(getProperty(LOG_ELM327));
	}

	public String parser(String pid) {
		return getProperty(PARSER + pid);
	}

	public boolean savingPackageAsXml() {
		return isTrue(getProperty(SAVE_PACKAGE_AS_XML));
	}

	public String socketAddress() {
		return getProperty(SOCKET_ADDRESS);
	}

	public String socketPort() {
		return getProperty(SOCKET_PORT);
	}

	public String vehicle() {
		String value = getProperty(VEHICLE);
		return Conversions.isEmpty(value) ? Package.UNKNOWN_VEHICLE : value;
	}

	public String webServer() {
		return getProperty(WEB_SERVER);
	}

	private String getProperty(String name) {
		return properties.getProperty(name);
	}

	String checkProperty(String name) {
		String value = getProperty(name);
		if (Conversions.isEmpty(value)) {
			throw new IllegalStateException("property \"" + name + "\" not found in " + FILE_NAME);
		}
		return value;
	}
}

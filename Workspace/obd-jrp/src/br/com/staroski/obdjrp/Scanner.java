package br.com.staroski.obdjrp;

import static br.com.staroski.obdjrp.utils.Conversions.isEmpty;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.utils.Conversions;
import br.com.staroski.obdjrp.utils.Print;

public final class Scanner {

	private static final String OBD2_MODE_1 = "01";
	private static final String PROMPT_SYMBOL = ">";
	private static final int READ_TRYOUTS = 10;
	private static final Pattern PID_BITMASK = Pattern.compile("[0-9A-F]{12}");

	private static PrintStream createLogStream() throws IOException {
		if (Config.get().loggingELM327()) {
			String instant = Config.get().formatted(new Date());
			File file = new File(Config.get().dataDir(), "ELM327_" + instant + ".log");
			return new PrintStream(file);
		}
		return System.out;
	}

	private static String formatResponse(String mode, String pid, String response) {
		String text = response;
		String header = responseHeader(mode, pid);
		int offset = text.indexOf(header);
		if (offset != -1) {
			text = text.substring(offset).trim();
		}
		offset = text.indexOf(PROMPT_SYMBOL);
		if (offset != -1) {
			text = text.substring(0, offset).trim();
		}
		return text;
	}

	private static String responseHeader(String mode, String pid) {
		String response = "4" + mode.charAt(mode.length() - 1);
		return response + pid;
	}

	private static ELM327 startupELM327(IO connection) throws IOException, ELM327Error {
		PrintStream log = createLogStream();
		ELM327 elm327 = new ELM327(connection, log);
		elm327.execute("ATZ"); // reset
		elm327.execute("ATE0"); // desligando echo
		elm327.execute("ATH0"); // desligando envio dos cabeçalhos
		elm327.execute("ATS0"); // desligando espaços em branco
		elm327.execute("ATSP0"); // definindo detecção automática de protocolo
		return elm327;
	}

	private final EventMulticaster eventMulticaster;

	private final ELM327 elm327;

	private final ScanLoop scanLoop;

	private final List<String> supportedPIDs;

	public Scanner(IO connection) throws IOException, ELM327Error {
		elm327 = startupELM327(connection);
		supportedPIDs = loadSupportedPIDs();

		this.eventMulticaster = new EventMulticaster();
		this.scanLoop = new ScanLoop(this);
	}

	public void addListener(ScannerListener listener) {
		eventMulticaster.addListener(listener);
	}

	public void removeListener(ScannerListener listener) {
		eventMulticaster.removeListener(listener);
	}

	public void start() {
		scanLoop.start();
	}

	public void stop() {
		scanLoop.stop();
		elm327.disconnect();
	}

	private String ask(String mode, String pid) throws IOException {
		String response = elm327.execute(mode + pid).trim();
		return formatResponse(mode, pid, response);
	}

	private List<String> loadSupportedPIDs() throws IOException, ELM327Error {
		final List<String> reservedPIDs = Arrays.asList(new String[] { //
				"00", // return range 01-20
				"20", // return range 21-40
				"40", // return range 41-60
				"60", // return range 61-80
				"80", // return range 81-A0
				"A0", // return range A1-C0
				"C0", // return range C1-E0
				"E0", // return range E1-FF
		});
		List<String> supportedPIDs = new ArrayList<>();
		List<String> validPIDs = Arrays.asList(reservedPIDs.get(0));
		for (String pid : reservedPIDs) {
			if (!validPIDs.contains(pid)) {
				break;
			}
			String bitmask = readPIDsBitmask(pid);
			validPIDs = processBitmask(pid, bitmask);
			supportedPIDs.addAll(validPIDs);
		}
		supportedPIDs.removeAll(reservedPIDs);
		if (supportedPIDs.isEmpty()) {
			throw new UnsupportedOperationException("Vehicle doesn't provide any OBD2 PID!");
		}
		return supportedPIDs;
	}

	private List<String> processBitmask(String pid, String bytes) throws IOException {
		List<String> pids = new ArrayList<>();
		try {
			int offset = Integer.parseInt(pid, 16) + 1;
			char[] bitmask = Conversions.hexaToBinary(bytes, 32).toCharArray();
			for (int i = 0, value = offset; i < bitmask.length; i++, value++) {
				if (bitmask[i] == '1') {
					pids.add(Conversions.decimalToHexa(value, 8));
				}
			}
			return pids;
		} catch (NumberFormatException e) {
			return new ArrayList<>();
		}
	}

	private String readPIDsBitmask(String pid) throws IOException, ELM327Error {
		final String header = responseHeader(OBD2_MODE_1, pid);
		String bitmask = null;
		for (int i = 0; i < READ_TRYOUTS; i++) {
			bitmask = ask(OBD2_MODE_1, pid);
			if (!PID_BITMASK.matcher(bitmask).find()) {
				continue;
			}
			if (!bitmask.startsWith(header)) {
				continue;
			}
			return bitmask;
		}
		throw ELM327Error.DATA_ERROR;
	}

	private String removeHeader(String response, String header) {
		int offset = response.indexOf(header);
		if (offset != -1) {
			response = response.substring(offset + header.length());
		}
		return response;
	}

	void notifyError(ELM327Error error) {
		eventMulticaster.onError(error);
	}

	void notifyScanned(Scan scannedData) {
		eventMulticaster.onScanned(scannedData);
	}

	Scan scan() throws IOException, ELM327Error {
		Scan scan = new Scan(System.currentTimeMillis());
		List<Data> dataList = scan.getData();
		List<ELM327Error> errors = new ArrayList<>();
		for (String pid : supportedPIDs) {
			try {
				String response = ask(OBD2_MODE_1, pid);
				if (!isEmpty(response)) {
					String header = responseHeader(OBD2_MODE_1, pid);
					response = removeHeader(response, header);
					dataList.add(new Data(pid, response));
				}
			} catch (ELM327Error error) {
				errors.add(error);
				Print.message(error);
			}
		}
		if (errors.size() == supportedPIDs.size()) {
			throw errors.get(0);
		}
		return scan;
	}
}

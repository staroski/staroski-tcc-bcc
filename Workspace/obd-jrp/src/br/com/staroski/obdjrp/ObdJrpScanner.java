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

public final class ObdJrpScanner {

	private static final int MAX_READ_TRIES = 10;

	private static final String MODE_1 = "01"; // SHOW CURRENT DATA

	private static final String PROMPT = ">";

	private static final Pattern SUPPORTED_PIDS = Pattern.compile("[0-9A-F]{12}");

	private static PrintStream createLogStream() throws IOException {
		if (ObdJrpProperties.get().loggingELM327()) {
			String instant = ObdJrpProperties.get().formatted(new Date());
			File file = new File(ObdJrpProperties.get().dataDir(), "ELM327_" + instant + ".log");
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
		offset = text.indexOf(PROMPT);
		if (offset != -1) {
			text = text.substring(0, offset).trim();
		}
		return text;
	}

	private static String responseHeader(String mode, String pid) {
		String response = "4" + mode.charAt(mode.length() - 1);
		return response + pid;
	}

	private static ELM327 startupELM327(ObdJrpConnection connection) throws IOException, ELM327Error {
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

	public ObdJrpScanner(ObdJrpConnection connection) throws IOException, ELM327Error {
		elm327 = startupELM327(connection);
		supportedPIDs = loadSupportedPIDs();

		this.eventMulticaster = new EventMulticaster();
		this.scanLoop = new ScanLoop(this);
	}

	public void addListener(ObdJrpListener listener) {
		eventMulticaster.addListener(listener);
	}

	public void removeListener(ObdJrpListener listener) {
		eventMulticaster.removeListener(listener);
	}

	public void startScanning() {
		scanLoop.start();
	}

	public void stopScanning() {
		scanLoop.stop();
		elm327.disconnect();
	}

	private String execute(String mode, String pid) throws IOException {
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
		final String header = responseHeader(MODE_1, pid);
		String bitmask = null;
		for (int i = 0; i < MAX_READ_TRIES; i++) {
			bitmask = execute(MODE_1, pid);
			if (!SUPPORTED_PIDS.matcher(bitmask).find()) {
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
				String response = execute(MODE_1, pid);
				if (!isEmpty(response)) {
					String header = responseHeader(MODE_1, pid);
					response = removeHeader(response, header);
					dataList.add(new Data(pid, response));
				}
			} catch (ELM327Error error) {
				System.out.printf("%s: %s%n", //
						error.getClass().getSimpleName(), //
						error.getMessage());
				errors.add(error);
			}
		}
		if (!errors.isEmpty() && errors.size() == supportedPIDs.size()) {
			throw errors.get(0);
		}
		return scan;
	}
}

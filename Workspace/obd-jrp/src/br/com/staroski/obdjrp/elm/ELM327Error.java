package br.com.staroski.obdjrp.elm;

import java.util.regex.Pattern;

public final class ELM327Error extends Error {

	private static final long serialVersionUID = 1;

	public static final ELM327Error ACT_ALERT;
	public static final ELM327Error BUFFER_FULL;
	public static final ELM327Error BUS_BUSY;
	public static final ELM327Error BUS_ERROR;
	public static final ELM327Error CAN_ERROR;
	public static final ELM327Error DATA_ERROR;
	public static final ELM327Error ERR;
	public static final ELM327Error FB_ERROR;
	public static final ELM327Error LP_ALERT;
	public static final ELM327Error LV_RESET;
	public static final ELM327Error NO_DATA;
	public static final ELM327Error RX_ERROR;
	public static final ELM327Error STOPPED;
	public static final ELM327Error UNABLE_TO_CONNECT;

	private static final ELM327Error[] ALL_ERRORS;

	private static final String QB = "\\Q"; // Quote Begin
	private static final String QE = "\\E"; // Quote Begin
	private static final String HEX = "[0-9A-F]"; // Hexa Digit
	private static final String ALO = "+"; // At Least One

	static {
		ALL_ERRORS = new ELM327Error[] { //
				(ACT_ALERT = new ELM327Error(QB + "ACT ALERT" + QE + ALO)), //
				(BUFFER_FULL = new ELM327Error(QB + "BUFFER FULL" + QE + ALO)), //
				(BUS_BUSY = new ELM327Error(QB + "BUS BUSY" + QE + ALO)), //
				(BUS_ERROR = new ELM327Error(QB + "BUS ERROR" + QE + ALO)), //
				(CAN_ERROR = new ELM327Error(QB + "CAN ERROR" + QE + ALO)), //
				(DATA_ERROR = new ELM327Error(QB + "DATA ERROR" + QE + ALO)), //
				(ERR = new ELM327Error("(" + QB + "ERR" + QE + HEX + HEX + ")" + ALO)), //
				(FB_ERROR = new ELM327Error(QB + "FB ERROR" + QE + ALO)), //
				(LP_ALERT = new ELM327Error(QB + "LP ALERT" + QE + ALO)), //
				(LV_RESET = new ELM327Error(QB + "LV RESET" + QE + ALO)), //
				(NO_DATA = new ELM327Error(QB + "NO DATA" + QE + ALO)), //
				(RX_ERROR = new ELM327Error(QB + "RX ERROR" + QE + ALO)), //
				(STOPPED = new ELM327Error(QB + "STOPPED" + QE + ALO)), //
				(UNABLE_TO_CONNECT = new ELM327Error(QB + "UNABLE TO CONNECT" + QE + ALO)) //
		};
	}

	public static ELM327Error findError(String response) {
		for (ELM327Error error : ALL_ERRORS) {
			if (error.isIn(response)) {
				return error;
			}
		}
		return null;
	}

	public static ELM327Error wrap(Throwable error) {
		String message = String.format("%s: %s%n", //
				error.getClass().getSimpleName(), //
				error.getMessage());
		return new ELM327Error(QB + message + QE + ALO);
	}

	private static String getDescription(String regex) {
		final int begin = regex.indexOf(QB) + QB.length();
		final int end = regex.indexOf(QE);
		return regex.substring(begin, end);
	}

	private final Pattern pattern;

	private ELM327Error(String regex) {
		this(regex, null);
	}

	private ELM327Error(String regex, Throwable cause) {
		super(getDescription(regex), cause);
		this.pattern = Pattern.compile(regex);
	}

	public boolean isIn(String response) {
		return pattern.matcher(response).find();
	}
}

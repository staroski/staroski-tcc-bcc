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

	static {
		ALL_ERRORS = new ELM327Error[] { //
				(ACT_ALERT = new ELM327Error("\\QACT ALERT\\E+")), //
				(BUFFER_FULL = new ELM327Error("\\QBUFFER FULL\\E+")), //
				(BUS_BUSY = new ELM327Error("\\QBUS BUSY\\E+")), //
				(BUS_ERROR = new ELM327Error("\\QBUS ERROR\\E+")), //
				(CAN_ERROR = new ELM327Error("\\QCAN ERROR\\E+")), //
				(DATA_ERROR = new ELM327Error("\\QDATA ERROR\\E+")), //
				(ERR = new ELM327Error("(\\QERR\\E[0-9A-F][0-9A-F])+")), //
				(FB_ERROR = new ELM327Error("\\QFB ERROR\\E+")), //
				(LP_ALERT = new ELM327Error("\\QLP ALERT\\E+")), //
				(LV_RESET = new ELM327Error("\\QLV RESET\\E+")), //
				(NO_DATA = new ELM327Error("\\QNO DATA\\E+")), //
				(RX_ERROR = new ELM327Error("\\QRX ERROR\\E+")), //
				(STOPPED = new ELM327Error("\\QSTOPPED\\E+")), //
				(UNABLE_TO_CONNECT = new ELM327Error("\\QUNABLE TO CONNECT\\E+")) //
		};
	}

	public static ELM327Error getError(String response) {
		for (ELM327Error error : ALL_ERRORS) {
			if (error.isContainedIn(response)) {
				return error;
			}
		}
		return null;
	}

	private static String getDescription(String regex) {
		final int begin = regex.indexOf("\\Q") + 2;
		final int end = regex.indexOf("\\E");
		return regex.substring(begin, end);
	}

	private final Pattern pattern;

	private ELM327Error(String regex) {
		super(getDescription(regex));
		this.pattern = Pattern.compile(regex);
	}

	public boolean isContainedIn(String response) {
		return pattern.matcher(response).find();
	}

	public void raise() throws ELM327Error {
		throw this;
	}
}

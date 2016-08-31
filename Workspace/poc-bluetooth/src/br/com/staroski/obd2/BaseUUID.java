package br.com.staroski.obd2;

import javax.bluetooth.UUID;

/**
 * The Base UUID is used for calculating 128-bit UUIDs from "short UUIDs" (uuid16 and uuid32) as described in the SDP Specification. See Service Discovery Protocol (SDP) in the Bluetooth Core
 * Specification<BR>
 * See <a href= "https://www.bluetooth.com/specifications/assigned-numbers/service-discovery"> Service Discovery</a>
 */
final class BaseUUID {

	private static final String SDP_BASE_UUID_MASK = "yyyyxxxx00001000800000805F9B34FB";

	/**
	 * A 16-bit Attribute UUID replaces the x’s in the following:
	 * 
	 * <pre>
	 * 00000000-0000-1000-8000-00805F9B34FB // BASE UUID
	 * 0000xxxx-0000-1000-8000-00805F9B34FB
	 * </pre>
	 */
	public static UUID merge16bits(short uuid) {
		String merged = SDP_BASE_UUID_MASK.replace("yyyy", "0000").replace("xxxx", hexa(uuid, 16));
		return new UUID(merged, false);
	}

	/**
	 * A 32-bit Attribute UUID replaces the x's in the following:
	 * 
	 * <pre>
	 * 00000000-0000-1000-8000-00805F9B34FB // BASE UUID
	 * xxxxxxxx-0000-1000-8000-00805F9B34FB
	 * </pre>
	 */
	public static UUID merge32bits(int uuid) {
		String merged = SDP_BASE_UUID_MASK.replace("yyyyxxxx", hexa(uuid, 32));
		return new UUID(merged, false);
	}

	private static String hexa(int value, int bits) {
		StringBuilder hexa = new StringBuilder(Integer.toHexString(value));
		int bytes = bits / 8;
		int digits = bytes * 2;
		while (hexa.length() < digits) {
			hexa.insert(0, '0');
		}
		return hexa.toString();
	}

	private BaseUUID() {}
}
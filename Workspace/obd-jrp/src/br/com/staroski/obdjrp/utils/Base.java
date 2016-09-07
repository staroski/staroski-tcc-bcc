package br.com.staroski.obdjrp.utils;

public final class Base {

	public static String decToHexa(int value, int bits) {
		StringBuilder hexa = new StringBuilder(Integer.toHexString(value).toUpperCase());
		int bytes = bits / 8;
		int digits = bytes * 2;
		while (hexa.length() < digits) {
			hexa.insert(0, '0');
		}
		return hexa.toString();
	}

	public static String hexaToBin(String hexa, int bits) {
		int bytes = bits / 8;
		int digits = 2;
		int offset = hexa.length() - (digits * bytes);
		String result = hexa.substring(offset < 0 ? 0 : offset);
		long dec = Long.parseLong(result, 16);
		StringBuilder bin = new StringBuilder(Long.toBinaryString(dec));
		while (bin.length() < bits) {
			bin.insert(0, '0');
		}
		return bin.toString();
	}

	private Base() {}
}

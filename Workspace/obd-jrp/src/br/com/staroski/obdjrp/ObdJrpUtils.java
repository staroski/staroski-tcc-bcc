package br.com.staroski.obdjrp;

import java.nio.charset.StandardCharsets;

public final class ObdJrpUtils {

	public static String bytesToHexas(byte[] value) {
		StringBuilder hexa = new StringBuilder();
		for (int i = 0, n = value.length; i < n; i++) {
			hexa.append(decimalToHexa(unsigned(value[i]), 8));
		}
		return hexa.toString();
	}

	public static String byteToHexa(byte value) {
		return decimalToHexa(unsigned(value), 8);
	}

	public static String decimalToHexa(int value, int bits) {
		StringBuilder hexa = new StringBuilder(Integer.toHexString(value).toUpperCase());
		int bytes = bits / 8;
		int digits = bytes * 2;
		while (hexa.length() < digits) {
			hexa.insert(0, '0');
		}
		return hexa.toString();
	}

	public static byte[] hexasToBytes(String hexaBytes) {
		int size = hexaBytes.length() / 2;
		byte[] bytes = new byte[size];
		for (int i = 0, start = 0, end = 2; i < size; i++, start += 2, end += 2) {
			bytes[i] = hexaToByte(hexaBytes.substring(start, end));
		}
		return bytes;
	}

	public static String hexaToASCII(String hexa) {
		byte[] bytes = new byte[hexa.length() / 2];
		for (int i = 0, n = 0; i < hexa.length(); i += 2, n++) {
			String hexByte = hexa.substring(i, i + 2);
			bytes[n] = ObdJrpUtils.hexaToByte(hexByte);
		}
		String ascii = new String(bytes, StandardCharsets.US_ASCII);
		return ascii;
	}

	public static String hexaToBinary(String hexa, int bits) {
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

	public static byte hexaToByte(String hexaByte) {
		return (byte) Integer.parseInt(hexaByte, 16);
	}

	public static boolean isEmpty(String value) {
		return value == null || (value = value.trim()).isEmpty();
	}

	public static short unsigned(byte value) {
		return (short) (value & 0xFF);
	}

	private ObdJrpUtils() {}
}

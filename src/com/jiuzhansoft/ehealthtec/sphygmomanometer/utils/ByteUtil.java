package com.jiuzhansoft.ehealthtec.sphygmomanometer.utils;

public class ByteUtil {

	public static int ByteToInt(byte[] b) {
		int targets = (b[0] & 0xff) | ((b[1] << 8) & 0xff00)
				| ((b[2] << 24) >>> 8) | (b[3] << 24);

		return targets;

	}

	public static float ByteToFloat(byte[] b) {
		return Float.intBitsToFloat(ByteToInt(b));
	}

	public static int FloatToInt(float f) {
		return Float.floatToIntBits(f);
	}

	public static byte[] FloatToByte(float f) {
		return IntToByte(Float.floatToIntBits(f));
	}

	public static float IntToFloat(int i) {
		return Float.intBitsToFloat(i);
	}

	public static byte[] IntToByte(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static byte[] getPartByte(byte[] buffer, int length, int srcPos) {
		byte[] b = new byte[length];
		System.arraycopy(buffer, srcPos, b, 0, length);
		return b;
	}
}

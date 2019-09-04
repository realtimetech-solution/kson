package com.realtimetech.kson.reflection;

public class ArrayUtils {
	public static Object get(Object array, int index) {
		Class<?> c = array.getClass();
		if (int[].class == c) {
			return ((int[]) array)[index];
		} else if (float[].class == c) {
			return ((float[]) array)[index];
		} else if (boolean[].class == c) {
			return ((boolean[]) array)[index];
		} else if (char[].class == c) {
			return ((char[]) array)[index];
		} else if (double[].class == c) {
			return ((double[]) array)[index];
		} else if (long[].class == c) {
			return ((long[]) array)[index];
		} else if (short[].class == c) {
			return ((short[]) array)[index];
		} else if (byte[].class == c) {
			return ((byte[]) array)[index];
		}
		return ((Object[]) array)[index];
	}

	public static void set(Object array, int index, Object value) {
		Class<?> c = array.getClass();
		
		if (int[].class == c) {
			((int[]) array)[index] = (int) value;
		} else if (float[].class == c) {
			((float[]) array)[index] = (float) value;
		} else if (boolean[].class == c) {
			((boolean[]) array)[index] = (boolean) value;
		} else if (char[].class == c) {
			((char[]) array)[index] = (char) value;
		} else if (double[].class == c) {
			((double[]) array)[index] = (double) value;
		} else if (long[].class == c) {
			((long[]) array)[index] = (long) value;
		} else if (short[].class == c) {
			((short[]) array)[index] = (short) value;
		} else if (byte[].class == c) {
			((byte[]) array)[index] = (byte) value;
		} else {
			((Object[]) array)[index] = value;
		}
	}

}

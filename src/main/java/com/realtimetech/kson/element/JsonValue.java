package com.realtimetech.kson.element;

import com.realtimetech.kson.util.string.StringMaker;

public interface JsonValue {
	StringMaker stringMaker = new StringMaker();
	
	default public String toKsonString() {
		return toString(true);
	}

	default public String toJsonString() {
		return toString(false);
	}

	public static String escape(String string) {
		if (string == null) {
			return null;
		}
		stringMaker.reset();
		
		for (int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			switch (character) {
			case '"':
				stringMaker.add('\\');
				stringMaker.add('\"');
				break;
			case '\\':
				stringMaker.add('\\');
				stringMaker.add('\\');
				break;
			case '\b':
				stringMaker.add('\\');
				stringMaker.add('b');
				break;
			case '\f':
				stringMaker.add('\\');
				stringMaker.add('f');
				break;
			case '\n':
				stringMaker.add('\\');
				stringMaker.add('n');
				break;
			case '\r':
				stringMaker.add('\\');
				stringMaker.add('r');
				break;
			case '\t':
				stringMaker.add('\\');
				stringMaker.add('t');
				break;
			default:
				stringMaker.add(character);
			}
		}
		
		return stringMaker.toString();
	}

	default public String toString(Object object, boolean useKsonStandard) {
		if (object == null)
			return "null";

		if (object instanceof JsonValue) {
			JsonValue ksonValue = (JsonValue) object;
			return ksonValue.toString(useKsonStandard);
		} else if (object instanceof String) {
			String string = (String) object;
			return "\"" + escape(string) + "\"";
		} else {
			if (useKsonStandard) {
				if (object instanceof Float) {
					return object + "F";
				} else if (object instanceof Double) {
					return object + "D";
				} else if (object instanceof Long) {
					return object + "L";
				} else if (object instanceof Byte) {
					return object + "B";
				} else {
					return object.toString();
				}
			} else {
				return object.toString();
			}
		}
	}

	public String toString(boolean useKsonStandard);
}

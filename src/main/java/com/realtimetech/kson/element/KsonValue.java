package com.realtimetech.kson.element;

public interface KsonValue {
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
		
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			switch (character) {
			case '"':
				stringBuffer.append("\\\"");
				break;
			case '\\':
				stringBuffer.append("\\\\");
				break;
			case '\b':
				stringBuffer.append("\\b");
				break;
			case '\f':
				stringBuffer.append("\\f");
				break;
			case '\n':
				stringBuffer.append("\\n");
				break;
			case '\r':
				stringBuffer.append("\\r");
				break;
			case '\t':
				stringBuffer.append("\\t");
				break;
			default:
				stringBuffer.append(character);
			}
		}
		return stringBuffer.toString();
	}

	default public String toString(Object object, boolean useKsonStandard) {
		if (object == null)
			return "null";

		if (object instanceof KsonValue) {
			KsonValue ksonValue = (KsonValue) object;
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

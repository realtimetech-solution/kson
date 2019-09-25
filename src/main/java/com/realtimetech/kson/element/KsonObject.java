package com.realtimetech.kson.element;

import java.util.HashMap;

public class KsonObject extends HashMap<Object, Object> implements KsonValue {

	/**
	 * 기본 Serial UID
	 */
	private static final long serialVersionUID = -6357620110797218097L;
	
	private final StringBuffer stringBuffer = new StringBuffer();

	@Override
	public String toString(boolean useKsonStandard) {
		boolean firstElement = true;

		stringBuffer.setLength(0);
		
		stringBuffer.append("{");
		for (Object key : keySet()) {
			Object value = get(key);

			if (firstElement) {
				firstElement = false;
				stringBuffer.append(this.toString(key, useKsonStandard));
				stringBuffer.append(": ");
				stringBuffer.append(this.toString(value, useKsonStandard));
			} else {
				stringBuffer.append(", ");
				stringBuffer.append(this.toString(key, useKsonStandard));
				stringBuffer.append(": ");
				stringBuffer.append(this.toString(value, useKsonStandard));
			}
		}
		stringBuffer.append("}");

		return stringBuffer.toString();
	}

	@Override
	public String toString() {
		return toKsonString();
	}
}

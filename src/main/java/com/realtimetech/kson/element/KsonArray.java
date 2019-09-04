package com.realtimetech.kson.element;

import java.util.ArrayList;

public class KsonArray extends ArrayList<Object> implements KsonValue {

	/**
	 * 기본 Serial UID
	 */
	private static final long serialVersionUID = 5513748119461105760L;
	
	private final StringBuffer stringBuffer = new StringBuffer();

	@Override
	public String toString(boolean useKsonStandard) {
		boolean firstElement = true;

		stringBuffer.setLength(0);
		
		stringBuffer.append("[");
		for (Object object : this) {
			if (firstElement) {
				firstElement = false;
				stringBuffer.append(this.toString(object, useKsonStandard));
			} else {
				stringBuffer.append(", ");
				stringBuffer.append(this.toString(object, useKsonStandard));
			}
		}
		stringBuffer.append("]");

		return stringBuffer.toString();
	}

	@Override
	public String toString() {
		return toKsonString();
	}
}

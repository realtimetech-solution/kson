package com.realtimetech.kson.element;

public interface JsonValue {
	default public String toKsonString() {
		return toString(true);
	}

	default public String toJsonString() {
		return toString(false);
	}

	public String toString(boolean useKsonStandard);
}

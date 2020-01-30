package com.realtimetech.kson.element;

import java.util.Random;

public interface JsonValue {
	static final Random RANDOM = new Random();

	default public String toKsonString() {
		return toString(true);
	}

	default public String toJsonString() {
		return toString(false);
	}

	public int unique();

	public void unique(int unique);

	public String toString(boolean useKsonStandard);
	
	public int actualHash();
}

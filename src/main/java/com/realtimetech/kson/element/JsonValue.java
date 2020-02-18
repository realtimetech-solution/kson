package com.realtimetech.kson.element;

import java.io.IOException;
import java.util.Random;

import com.realtimetech.kson.builder.KsonBuilder;
import com.realtimetech.kson.util.pool.KsonPool;

public interface JsonValue {
	static final Random RANDOM = new Random();
	static final KsonPool WRITER_POOL = new KsonPool(new KsonBuilder());

	default public String toKsonString() throws IOException {
		return toString(true);
	}

	default public String toJsonString() throws IOException {
		return toString(false);
	}

	public int unique();

	public void unique(int unique);

	public String toString(boolean useKsonStandard)throws IOException ;
	
	public int actualHash();
}

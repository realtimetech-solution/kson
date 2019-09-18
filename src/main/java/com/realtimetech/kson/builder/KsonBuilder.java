package com.realtimetech.kson.builder;

import java.util.HashMap;

import com.realtimetech.kson.KsonContext;
import com.realtimetech.kson.transform.Transformer;

public class KsonBuilder {
	private HashMap<Class<?>, Transformer<?>> registeredTransformers;

	private int stackSize;
	private int stringBufferSize;

	public KsonBuilder() {
		this.registeredTransformers = new HashMap<Class<? extends Object>, Transformer<? extends Object>>();
		this.stackSize = 10;
		this.stringBufferSize = 100;
	}

	public KsonBuilder registerTransformer(Class<?> clazz, Transformer<?> preTransformer) {
		this.registeredTransformers.put(clazz, preTransformer);

		return this;
	}

	public int getStackSize() {
		return stackSize;
	}

	public KsonBuilder setStackSize(int stackSize) {
		this.stackSize = stackSize;

		return this;
	}

	public int getStringBufferSize() {
		return stringBufferSize;
	}

	public KsonBuilder setStringBufferSize(int stringBufferSize) {
		this.stringBufferSize = stringBufferSize;

		return this;
	}

	public KsonContext build() {
		KsonContext ksonContext = new KsonContext(this.stackSize, this.stringBufferSize);

		for (Class<?> clazz : this.registeredTransformers.keySet()) {
			ksonContext.registerTransformer(clazz, this.registeredTransformers.get(clazz));
		}

		return ksonContext;
	}
}

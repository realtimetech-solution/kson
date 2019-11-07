package com.realtimetech.kson.builder;

import java.util.HashMap;

import com.realtimetech.kson.KsonContext;
import com.realtimetech.kson.transform.Transformer;

public class KsonBuilder {
	private boolean useCustomTag;

	private HashMap<Class<?>, Transformer<?>> registeredTransformers;

	private ClassLoader classLoader;
	private int stackSize;
	private int stringBufferSize;

	public KsonBuilder() {
		this.useCustomTag = true;
		
		this.registeredTransformers = new HashMap<Class<? extends Object>, Transformer<? extends Object>>();
		
		this.stackSize = 10;
		this.stringBufferSize = 100;
	}

	public KsonBuilder registerTransformer(Class<?> clazz, Transformer<?> preTransformer) {
		this.registeredTransformers.put(clazz, preTransformer);

		return this;
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	
	public KsonBuilder setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		
		return this;
	}
	
	public boolean isUseCustomTag() {
		return useCustomTag;
	}
	
	public KsonBuilder setUseCustomTag(boolean useCustomTag) {
		this.useCustomTag = useCustomTag;
		
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
		KsonContext ksonContext = new KsonContext(this.classLoader, this.stackSize, this.stringBufferSize);

		ksonContext.setUseCustomTag(this.useCustomTag);
		
		for (Class<?> clazz : this.registeredTransformers.keySet()) {
			ksonContext.registerTransformer(clazz, this.registeredTransformers.get(clazz));
		}

		return ksonContext;
	}
}

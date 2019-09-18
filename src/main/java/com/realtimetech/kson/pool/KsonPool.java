package com.realtimetech.kson.pool;

import java.util.HashMap;

import com.realtimetech.kson.KsonContext;
import com.realtimetech.kson.builder.KsonBuilder;

public class KsonPool {
	private HashMap<Thread, KsonContext> contextPools;

	private KsonBuilder ksonBuilder;

	public KsonPool(KsonBuilder ksonBuilder) {
		this.contextPools = new HashMap<Thread, KsonContext>();

		this.ksonBuilder = ksonBuilder;
	}

	public synchronized KsonContext get() {
		Thread currentThread = Thread.currentThread();

		if (!this.contextPools.containsKey(currentThread)) {
			KsonContext freeContext = null;

			for (Thread thread : this.contextPools.keySet()) {
				if (!thread.isAlive()) {
					freeContext = this.contextPools.get(thread);
					this.contextPools.remove(thread);
					break;
				}
			}

			if (freeContext == null) {
				freeContext = ksonBuilder.build();
			}
			
			this.contextPools.put(currentThread, freeContext);
		}

		return this.contextPools.get(currentThread);
	}
}

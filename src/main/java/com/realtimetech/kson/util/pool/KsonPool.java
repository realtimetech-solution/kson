package com.realtimetech.kson.util.pool;

import java.util.HashMap;

import com.realtimetech.kson.KsonContext;
import com.realtimetech.kson.builder.KsonBuilder;
import com.realtimetech.kson.writer.KsonWriter;

public class KsonPool {
	private HashMap<Thread, KsonContext> contextPools;
	private HashMap<Thread, KsonWriter> writerPools;

	private KsonBuilder ksonBuilder;

	public KsonPool(KsonBuilder ksonBuilder) {
		this.contextPools = new HashMap<Thread, KsonContext>();
		this.writerPools = new HashMap<Thread, KsonWriter>();

		this.ksonBuilder = ksonBuilder;
	}

	public KsonContext get() {
		synchronized (contextPools) {
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

	public synchronized KsonWriter writer() {
		synchronized (writerPools) {
			Thread currentThread = Thread.currentThread();

			if (!this.writerPools.containsKey(currentThread)) {
				KsonWriter freeWriter = null;

				for (Thread thread : this.writerPools.keySet()) {
					if (!thread.isAlive()) {
						freeWriter = this.writerPools.get(thread);
						this.contextPools.remove(thread);
						break;
					}
				}

				if (freeWriter == null) {
					freeWriter = new KsonWriter();
				}

				this.writerPools.put(currentThread, freeWriter);
			}

			return this.writerPools.get(currentThread);
		}
	}
}

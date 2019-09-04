package com.realtimetech.kson.transform;

import com.realtimetech.kson.KsonContext;

public interface Transformer<T> {
	public Object serialize(KsonContext ksonContext, T value);

	public T deserialize(KsonContext ksonContext, Object value);
}

package com.realtimetech.kson.element;

import java.util.HashMap;

import com.realtimetech.kson.writer.KsonWriter;

public class JsonObject extends HashMap<Object, Object> implements JsonValue {

	/**
	 * 기본 Serial UID
	 */
	private static final long serialVersionUID = -6357620110797218097L;

	protected KsonWriter ksonWriter = null;

	@Override
	public String toString(boolean useKsonStandard) {
		if (ksonWriter == null)
			this.ksonWriter = new KsonWriter();

		this.ksonWriter.setUseKson(useKsonStandard);
		
		return this.ksonWriter.toString(this);
	}

	@Override
	public String toString() {
		return toKsonString();
	}
}

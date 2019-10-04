package com.realtimetech.kson.element;

import java.util.ArrayList;

import com.realtimetech.kson.writer.KsonWriter;

public class JsonArray extends ArrayList<Object> implements JsonValue {

	/**
	 * 기본 Serial UID
	 */
	private static final long serialVersionUID = 5513748119461105760L;

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

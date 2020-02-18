package com.realtimetech.kson.element;

import java.io.IOException;
import java.util.ArrayList;

import com.realtimetech.kson.annotation.Ignore;

public class JsonArray extends ArrayList<Object> implements JsonValue {

	/**
	 * 기본 Serial UID
	 */
	private static final long serialVersionUID = 5513748119461105760L;

	@Override
	public String toString(boolean useKsonStandard) throws IOException {
		return WRITER_POOL.writer().toString(this, useKsonStandard);
	}

	@Override
	public String toString() {
		try {
			return toKsonString();
		} catch (IOException e) {
			return null;
		}
	}

	@Ignore
	private int unique = RANDOM.nextInt();

	@Override
	public int unique() {
		return unique;
	}
	
	@Override
	public void unique(int unique) {
		this.unique = unique;
	}
	
	@Override
	public int hashCode() {
		return unique;
	}
	
	@Override
	public int actualHash() {
		return super.hashCode();
	}
}

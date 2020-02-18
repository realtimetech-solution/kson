package com.realtimetech.kson.element;

import java.io.IOException;
import java.util.HashMap;

import com.realtimetech.kson.annotation.Ignore;

public class JsonObject extends HashMap<Object, Object> implements JsonValue {
	/**
	 * 기본 Serial UID
	 */
	private static final long serialVersionUID = -6357620110797218097L;

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

	@Override
	public Object get(Object key) {
		Object object = super.get(key);

		if (object == null && key instanceof JsonValue) {
			JsonValue keyJsonValue = (JsonValue) key;
			int keyHash = keyJsonValue.actualHash();

			for (Object keyObject : keySet()) {

				if (keyObject instanceof JsonValue) {
					JsonValue targetJsonValue = (JsonValue) keyObject;
					int targetHash = targetJsonValue.actualHash();

					if (targetHash == keyHash) {
						keyJsonValue.unique(targetJsonValue.unique());

						object = super.get(keyObject);

						break;
					}
				}
			}
		}

		return object;
	}

	@Override
	public boolean containsKey(Object key) {
		boolean result = super.containsKey(key);

		if (!result && key instanceof JsonValue) {
			JsonValue keyJsonValue = (JsonValue) key;
			int keyHash = keyJsonValue.actualHash();

			for (Object keyObject : keySet()) {

				if (keyObject instanceof JsonValue) {
					JsonValue targetJsonValue = (JsonValue) keyObject;
					int targetHash = targetJsonValue.actualHash();

					if (targetHash == keyHash) {
						keyJsonValue.unique(targetJsonValue.unique());

						result = true;

						break;
					}
				}
			}
		}

		return result;
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
package com.realtimetech.kson.writer;

import java.io.IOException;
import java.util.Iterator;

import com.realtimetech.kson.element.JsonArray;
import com.realtimetech.kson.element.JsonObject;
import com.realtimetech.kson.element.JsonValue;
import com.realtimetech.kson.util.string.StringWriter;

public class KsonWriter {
	private static final char[] CONST_U2028 = "\\u2028".toCharArray();
	private static final char[] CONST_U2029 = "\\u2029".toCharArray();

	private static final char[] CONST_NULL_STRING = "null".toCharArray();

	private static final char CONST_SEPARATOR_STRING = ',';

	private static final char CONST_OBJECT_OPEN_STRING = '{';
	private static final char CONST_OBJECT_MAP_STRING = ':';
	private static final char CONST_OBJECT_CLOSE_STRING = '}';

	private static final char CONST_ARRAY_OPEN_STRING = '[';
	private static final char CONST_ARRAY_CLOSE_STRING = ']';

	private static final char[][] CONST_REPLACEMENT_CHARS;

	static {
		CONST_REPLACEMENT_CHARS = new char[128][];
		for (int i = 0; i <= 0x1f; i++) {
			CONST_REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i).toCharArray();
		}
		CONST_REPLACEMENT_CHARS['"'] = new char[] { '\\', '\"' };
		CONST_REPLACEMENT_CHARS['\\'] = new char[] { '\\', '\\' };
		CONST_REPLACEMENT_CHARS['\t'] = new char[] { '\\', 't' };
		CONST_REPLACEMENT_CHARS['\b'] = new char[] { '\\', 'b' };
		CONST_REPLACEMENT_CHARS['\n'] = new char[] { '\\', 'n' };
		CONST_REPLACEMENT_CHARS['\r'] = new char[] { '\\', 'r' };
		CONST_REPLACEMENT_CHARS['\f'] = new char[] { '\\', 'f' };
	}

	private StringWriter stringWriter;

	private boolean useKson;

	public KsonWriter() {
		this.stringWriter = new StringWriter();
	}

	public String toString(JsonValue jsonValue) throws IOException {
		return this.toString(jsonValue, true);
	}

	public String toString(JsonValue jsonValue, boolean useKson) throws IOException {
		this.useKson = useKson;

		this.stringWriter.reset();

		this.recursiveWrite(jsonValue);

		return this.stringWriter.toString();
	}

	public void recursiveWrite(Object object) throws IOException {
		if (object == null) {
			stringWriter.write(CONST_NULL_STRING);
			return;
		}

		if (JsonObject.class.isInstance(object)) {
			JsonObject jsonObject = (JsonObject) object;

			stringWriter.write(CONST_OBJECT_OPEN_STRING);
			int count = 0;
			for (Object key : jsonObject.keySet()) {
				Object value = jsonObject.get(key);

				if (count != 0) {
					stringWriter.write(CONST_SEPARATOR_STRING);
				}

				this.recursiveWrite(key);
				stringWriter.write(CONST_OBJECT_MAP_STRING);
				this.recursiveWrite(value);

				count++;
			}
			stringWriter.write(CONST_OBJECT_CLOSE_STRING);
			return;
		} else if (JsonArray.class.isInstance(object)) {
			JsonArray jsonArray = (JsonArray) object;

			stringWriter.write(CONST_ARRAY_OPEN_STRING);
			Iterator<Object> iterator = jsonArray.iterator();

			int count = 0;
			while (iterator.hasNext()) {
				Object value = iterator.next();

				if (count != 0) {
					stringWriter.write(CONST_SEPARATOR_STRING);
				}

				this.recursiveWrite(value);

				count++;
			}
			stringWriter.write(CONST_ARRAY_CLOSE_STRING);
			return;
		} else if (String.class.isInstance(object)) {
			writeString((String) object);
			return;
		} else if (this.useKson) {
			writeNumber(object);
			return;
		} else {
			stringWriter.write(object.toString().toCharArray());
			return;
		}
	}

	private void writeNumber(Object object) throws IOException {
		stringWriter.write(object.toString().toCharArray());

		if (object instanceof Float) {
			stringWriter.write('F');
		} else if (object instanceof Double) {
			stringWriter.write('D');
		} else if (object instanceof Long) {
			stringWriter.write('L');
		} else if (object instanceof Byte) {
			stringWriter.write('B');
		}
	}

	private void writeString(String value) throws IOException {
		char[] charArray = value.toCharArray();
		stringWriter.write('\"');

		int last = 0;
		int length = value.length();

		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			char[] replacement = null;
			if (c < 128) {
				replacement = CONST_REPLACEMENT_CHARS[c];
			} else if (c == '\u2028') {
				replacement = CONST_U2028;
			} else if (c == '\u2029') {
				replacement = CONST_U2029;
			}

			if(replacement != null){
				if (last < i) {
					stringWriter.write(charArray, last, i - last);
				}

				stringWriter.write(replacement);
				last = i + 1;
			}
		}

		if (last < length) {
			stringWriter.write(charArray, last, length - last);
		}

		stringWriter.write('\"');

	}
}

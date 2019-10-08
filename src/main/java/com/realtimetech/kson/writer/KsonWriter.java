package com.realtimetech.kson.writer;

import com.realtimetech.kson.element.JsonArray;
import com.realtimetech.kson.element.JsonObject;
import com.realtimetech.kson.element.JsonValue;
import com.realtimetech.kson.util.stack.FastStack;

public class KsonWriter {
	private final char[] NULL_CHARS = new char[] { 'n', 'u', 'l', 'l' };

	private boolean useKson;

	private FastStack<char[]> charsStack;

	private char[] characters;
	private char[] stringBuffer;
	private int charIndex;

	public KsonWriter() {
		this.charsStack = new FastStack<char[]>(100);
		this.useKson = true;
		this.characters = new char[0];
		this.stringBuffer = new char[0];
	}

	public boolean isUseKson() {
		return useKson;
	}

	public void setUseKson(boolean useKson) {
		this.useKson = useKson;
	}

	public String toString(JsonValue jsonValue) {
		this.charsStack.reset();
		int calc = this.prepareConvert(jsonValue);

		if (this.characters.length != calc) {
			this.characters = new char[calc];
		}
		this.charIndex = 0;
		this.convertString(jsonValue);

		return new String(characters);
	}

	private char[] convertValueToChars(Object value) {
		if (String.class.isInstance(value)) {
			String string = (String) value;

			int size = 2;
			char[] charArray = string.toCharArray();
			for (int i = 0; i < string.length(); i++) {
				char character = charArray[i];
				switch (character) {
				case '"':
					size += 2;
					break;
				case '\\':
					size += 2;
					break;
				case '\b':
					size += 2;
					break;
				case '\f':
					size += 2;
					break;
				case '\n':
					size += 2;
					break;
				case '\r':
					size += 2;
					break;
				case '\t':
					size += 2;
					break;
				default:
					size += 1;
				}
			}

			if (this.stringBuffer.length != size) {
				this.stringBuffer = new char[size];
			}
			int index = 0;

			this.stringBuffer[index++] = '\"';
			for (int i = 0; i < string.length(); i++) {
				char character = charArray[i];
				switch (character) {
				case '"':
					this.stringBuffer[index++] = '\\';
					this.stringBuffer[index++] = '\"';
					break;
				case '\\':
					this.stringBuffer[index++] = '\\';
					this.stringBuffer[index++] = '\\';
					break;
				case '\b':
					this.stringBuffer[index++] = '\\';
					this.stringBuffer[index++] = 'b';
					break;
				case '\f':
					this.stringBuffer[index++] = '\\';
					this.stringBuffer[index++] = 'f';
					break;
				case '\n':
					this.stringBuffer[index++] = '\\';
					this.stringBuffer[index++] = 'n';
					break;
				case '\r':
					this.stringBuffer[index++] = '\\';
					this.stringBuffer[index++] = 'r';
					break;
				case '\t':
					this.stringBuffer[index++] = '\\';
					this.stringBuffer[index++] = 't';
					break;
				default:
					this.stringBuffer[index++] = character;
				}
			}
			this.stringBuffer[index++] = '\"';

			return this.stringBuffer;
		} else if (value == null) {
			return NULL_CHARS;
		} else if (useKson) {
			if (value instanceof Float) {
				return (value.toString().concat("F")).toCharArray();
			} else if (value instanceof Double) {
				return (value.toString().concat("D")).toCharArray();
			} else if (value instanceof Long) {
				return (value.toString().concat("L")).toCharArray();
			} else if (value instanceof Byte) {
				return (value.toString().concat("B")).toCharArray();
			} else {
				return value.toString().toCharArray();
			}
		} else {
			return value.toString().toCharArray();
		}
	}

	private int prepareConvert(Object element) {
		int result = 0;

		if (JsonObject.class.isInstance(element)) {
			JsonObject jsonObject = (JsonObject) element;
			int elementLength = jsonObject.size() - 1;

			if (elementLength < 0)
				elementLength = 0;

			result += 2 + jsonObject.size() + elementLength;
			for (Object key : jsonObject.keySet()) {
				Object value = jsonObject.get(key);

				result += prepareConvert(key);
				result += prepareConvert(value);
			}
		} else if (JsonArray.class.isInstance(element)) {
			JsonArray jsonArray = (JsonArray) element;

			int elementLength = jsonArray.size() - 1;

			if (elementLength < 0)
				elementLength = 0;

			result += 2 + elementLength;
			for (Object object : jsonArray) {
				result += prepareConvert(object);
			}
		} else {
			char[] makeString = convertValueToChars(element);

			charsStack.push(makeString);

			result += makeString.length;
		}

		return result;

	}

	private void convertString(Object element) {
		if (JsonObject.class.isInstance(element)) {
			JsonObject jsonObject = (JsonObject) element;
			boolean firstElement = true;

			characters[charIndex++] = '{';
			for (Object key : jsonObject.keySet()) {
				Object value = jsonObject.get(key);

				if (firstElement) {
					firstElement = false;
					convertString(key);
					characters[charIndex++] = ':';
					convertString(value);
				} else {
					characters[charIndex++] = ',';
					convertString(key);
					characters[charIndex++] = ':';
					convertString(value);
				}
			}
			characters[charIndex++] = '}';
		} else if (JsonArray.class.isInstance(element)) {
			JsonArray jsonArray = (JsonArray) element;
			boolean firstElement = true;

			characters[charIndex++] = '[';
			for (Object object : jsonArray) {
				if (firstElement) {
					firstElement = false;
					convertString(object);
				} else {
					characters[charIndex++] = ',';
					convertString(object);
				}
			}
			characters[charIndex++] = ']';
		} else {
			char[] preparedChars = charsStack.shift();

			for (char character : preparedChars) {
				characters[charIndex++] = character;
			}
		}
	}
}

package com.realtimetech.kson.writer;

import com.realtimetech.kson.element.JsonArray;
import com.realtimetech.kson.element.JsonObject;
import com.realtimetech.kson.element.JsonValue;
import com.realtimetech.kson.util.stack.FastStack;
import com.realtimetech.kson.util.string.StringMaker;

public class KsonWriter {
	private boolean useKson;

	private FastStack<char[]> charsStack;
	private StringMaker stringMaker;

	private char[] characters;
	private int charIndex;

	public KsonWriter() {
		this.charsStack = new FastStack<char[]>(100);
		this.stringMaker = new StringMaker(10);
		this.useKson = true;
	}

	public boolean isUseKson() {
		return useKson;
	}

	public void setUseKson(boolean useKson) {
		this.useKson = useKson;
	}

	public String toString(JsonValue jsonValue) {
		this.charsStack.reset();
		int calc = prepareConvert(jsonValue);

		this.characters = new char[calc];
		this.charIndex = 0;
		this.convertString(jsonValue);

		return new String(characters);
	}

	private char[] convertValueToChars(Object value) {
		if (String.class.isInstance(value)) {
			String string = (String) value;
			stringMaker.reset();

			stringMaker.add('\"');
			char[] charArray = string.toCharArray();
			for (int i = 0; i < string.length(); i++) {
				char character = charArray[i];
				switch (character) {
				case '"':
					stringMaker.add('\\');
					stringMaker.add('\"');
					break;
				case '\\':
					stringMaker.add('\\');
					stringMaker.add('\\');
					break;
				case '\b':
					stringMaker.add('\\');
					stringMaker.add('b');
					break;
				case '\f':
					stringMaker.add('\\');
					stringMaker.add('f');
					break;
				case '\n':
					stringMaker.add('\\');
					stringMaker.add('n');
					break;
				case '\r':
					stringMaker.add('\\');
					stringMaker.add('r');
					break;
				case '\t':
					stringMaker.add('\\');
					stringMaker.add('t');
					break;
				default:
					stringMaker.add(character);
				}
			}
			stringMaker.add('\"');

			return stringMaker.toArray();
		} else if (useKson) {
			if (value instanceof Float) {
				return (value.toString() + "F").toCharArray();
			} else if (value instanceof Double) {
				return (value.toString() + "D").toCharArray();
			} else if (value instanceof Long) {
				return (value.toString() + "L").toCharArray();
			} else if (value instanceof Byte) {
				return (value.toString() + "B").toCharArray();
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

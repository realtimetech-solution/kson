package com.realtimetech.kson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import com.realtimetech.kson.annotation.Ignore;
import com.realtimetech.kson.annotation.PrimaryKey;
import com.realtimetech.kson.element.JsonArray;
import com.realtimetech.kson.element.JsonObject;
import com.realtimetech.kson.element.JsonValue;
import com.realtimetech.kson.exception.DeserializeException;
import com.realtimetech.kson.exception.SerializeException;
import com.realtimetech.kson.util.stack.FastStack;
import com.realtimetech.kson.util.string.StringMaker;
import com.realtimetech.kson.transform.Transformer;
import com.realtimetech.reflection.access.ArrayAccessor;
import com.realtimetech.reflection.allocate.UnsafeAllocator;

@SuppressWarnings("unchecked")
public class KsonContext {
	private static enum ValueMode {
		NONE, OBJECT, ARRAY, STRING, NUMBER
	}

	// for parse
	private FastStack<Object> valueStack;
	private FastStack<ValueMode> modeStack;
	private StringMaker stringMaker;

	// for object
	private FastStack<Object> objectStack;
	private FastStack<JsonValue> jsonStack;
	private boolean working;

	private boolean useCustomTag;

	private HashMap<Class<?>, Transformer<?>> registeredTransformers;

	private HashMap<Class<?>, Transformer<?>> transformers;

	private HashMap<Class<?>, Field> primaryKeys;

	private HashMap<Class<?>, HashMap<Object, Object>> primaryObjects;

	private HashMap<Class<?>, Field[]> cachedFields;

	private HashMap<String, Class<?>> cachedClasses;

	public KsonContext() {
		this(10, 100);
	}

	public KsonContext(int stackSize, int stringBufferSize) {
		this.working = false;
		this.valueStack = new FastStack<Object>(stackSize);
		this.modeStack = new FastStack<ValueMode>(stackSize);
		this.stringMaker = new StringMaker(stringBufferSize);

		this.objectStack = new FastStack<Object>(stackSize);
		this.jsonStack = new FastStack<JsonValue>(stackSize);

		this.useCustomTag = true;

		this.registeredTransformers = new HashMap<Class<? extends Object>, Transformer<? extends Object>>();

		this.transformers = new HashMap<Class<? extends Object>, Transformer<? extends Object>>();

		this.primaryKeys = new HashMap<Class<?>, Field>();
		this.primaryObjects = new HashMap<Class<?>, HashMap<Object, Object>>();

		this.cachedFields = new HashMap<Class<?>, Field[]>();

		this.cachedClasses = new HashMap<String, Class<?>>();

		this.registerTransformer(Charset.class, new Transformer<Charset>() {
			@Override
			public Object serialize(KsonContext ksonContext, Charset value) {
				return value.toString();
			}

			@Override
			public Charset deserialize(KsonContext ksonContext, Class<?> object, Object value) {
				return Charset.forName(value.toString());
			}
		});

		this.registerTransformer(File.class, new Transformer<File>() {
			@Override
			public Object serialize(KsonContext ksonContext, File value) {
				return value.getAbsolutePath();
			}

			@Override
			public File deserialize(KsonContext ksonContext, Class<?> object, Object value) {
				return new File(value.toString());
			}
		});

		this.registeredTransformers.put(Date.class, new Transformer<Date>() {
			@Override
			public Object serialize(KsonContext ksonContext, Date value) {
				return value.getTime();
			}

			@Override
			public Date deserialize(KsonContext ksonContext, Class<?> fieldType, Object value) {
				return new Date((Long) value);
			}
		});

		this.registeredTransformers.put(Collection.class, new Transformer<Collection<?>>() {
			@Override
			public Object serialize(KsonContext ksonContext, Collection<?> value) {
				JsonArray jsonArray = new JsonArray();

				for (Object object : value) {
					try {
						jsonArray.add(ksonContext.addFromObjectStack(object));
					} catch (SerializeException e) {
						e.printStackTrace();
					}
				}

				return jsonArray;
			}

			@SuppressWarnings("deprecation")
			@Override
			public Collection<?> deserialize(KsonContext ksonContext, Class<?> fieldType, Object value) {
				Collection<Object> collections = null;

				try {
					collections = (Collection<Object>) fieldType.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (Object object : (JsonArray) value) {
					try {
						collections.add(ksonContext.addToObjectStack(object.getClass(), object));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return collections;
			}
		});

		this.registeredTransformers.put(Map.class, new Transformer<Map<?, ?>>() {
			@Override
			public Object serialize(KsonContext ksonContext, Map<?, ?> value) {
				JsonObject jsonObject = new JsonObject();

				for (Object keyObject : value.keySet()) {
					Object valueObject = value.get(keyObject);

					try {
						Object keyKson = ksonContext.addFromObjectStack(keyObject);
						Object valueKson = ksonContext.addFromObjectStack(valueObject);
						jsonObject.put(keyKson, valueKson);
					} catch (SerializeException e) {
						e.printStackTrace();
					}
				}

				return jsonObject;
			}

			@SuppressWarnings("deprecation")
			@Override
			public Map<?, ?> deserialize(KsonContext ksonContext, Class<?> fieldType, Object value) {
				Map<Object, Object> map = null;

				try {
					map = (Map<Object, Object>) fieldType.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				JsonObject jsonObject = (JsonObject) value;
				for (Object keyObject : jsonObject.keySet()) {
					Object valueObject = jsonObject.get(keyObject);

					try {
						map.put(ksonContext.addToObjectStack(keyObject.getClass(), keyObject), ksonContext.addToObjectStack(valueObject.getClass(), valueObject));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return map;
			}
		});

		this.registerTransformer(UUID.class, new Transformer<UUID>() {
			@Override
			public Object serialize(KsonContext ksonContext, UUID value) {
				return value.toString();
			}

			@Override
			public UUID deserialize(KsonContext ksonContext, Class<?> object, Object value) {
				return UUID.fromString((String) value);
			}
		});
	}

	private Class<?> getClassByName(String name) throws ClassNotFoundException {
		if (!this.cachedClasses.containsKey(name)) {
			this.cachedClasses.put(name, Class.forName(name));
		}

		return this.cachedClasses.get(name);
	}

	private Field[] getAccessibleFields(Class<?> clazz) {
		if (!this.cachedFields.containsKey(clazz)) {
			LinkedList<Field> fields = new LinkedList<Field>();

			if (clazz != null) {
				if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
					Field[] superFields = getAccessibleFields(clazz.getSuperclass());
					for (Field superField : superFields) {
						if (!fields.contains(superField) && !superField.isAnnotationPresent(Ignore.class)) {
							fields.add(superField);
						}
					}
				}

				for (Field field : clazz.getDeclaredFields()) {
					field.setAccessible(true);
					if (!fields.contains(field) && !field.isAnnotationPresent(Ignore.class) && !Modifier.isStatic(field.getModifiers())) {
						fields.add(field);
					}
				}
			}

			this.cachedFields.put(clazz, fields.toArray(new Field[fields.size()]));
		}

		return this.cachedFields.get(clazz);
	}

	public void registerTransformer(Class<?> clazz, Transformer<?> preTransformer) {
		this.registeredTransformers.put(clazz, preTransformer);
	}

	public void setUseCustomTag(boolean useCustomTag) {
		this.useCustomTag = useCustomTag;
	}

	public boolean isUseCustomTag() {
		return useCustomTag;
	}

	private static final Class<?>[] SERIALIZE_WHITE_LIST = new Class[] { JsonObject.class, JsonArray.class, boolean.class, Boolean.class, int.class, Integer.class, double.class, Double.class, float.class, Float.class, long.class, Long.class, byte.class, Byte.class, short.class, Short.class, String.class };

	private boolean isNeedSerialize(Class<?> clazz) {
		for (Class<?> whiteClass : SERIALIZE_WHITE_LIST) {
			if (whiteClass.isAssignableFrom(clazz) || whiteClass == clazz) {
				return false;
			}
		}
		return true;
	}

	public Transformer<?> getTransformer(Class<?> type) {
		if (!this.transformers.containsKey(type)) {
			boolean matched = false;

			for (Class<?> preTransformClass : this.registeredTransformers.keySet()) {
				if (preTransformClass.isAssignableFrom(type) || preTransformClass == type) {
					Transformer<Object> preTransformer = (Transformer<Object>) this.registeredTransformers.get(preTransformClass);

					this.transformers.put(type, preTransformer);
					matched = true;
					break;
				}
			}

			if (!matched) {
				this.transformers.put(type, null);
			}
		}

		return this.transformers.get(type);
	}

	public Field getPrimaryKeyField(Class<?> type) {
		if (!this.primaryKeys.containsKey(type)) {
			boolean matched = false;

			for (Field field : this.getAccessibleFields(type)) {
				if (field.isAnnotationPresent(PrimaryKey.class)) {
					this.primaryKeys.put(type, field);
					matched = true;
					break;
				}
			}

			if (!matched) {
				this.primaryKeys.put(type, null);
			}
		}

		return this.primaryKeys.get(type);
	}

	private Object castToType(Class<?> type, Object object) {
		if (object instanceof Number) {
			Number number = (Number) object;

			if (type == Integer.class || type == int.class) {
				return number.intValue();
			}

			if (type == Double.class || type == double.class) {
				return number.doubleValue();
			}

			if (type == Float.class || type == float.class) {
				return number.floatValue();
			}

			if (type == Byte.class || type == byte.class) {
				return number.byteValue();
			}

			if (type == Long.class || type == long.class) {
				return number.longValue();
			}

			if (type == Short.class || type == short.class) {
				return number.shortValue();
			}
		}

		try {
			return type.cast(object);
		} catch (ClassCastException e) {
			return object;
		}
	}

	public <T> T toObject(Class<T> clazz, Object object) throws DeserializeException {
		if (object instanceof String) {
			try {
				object = this.fromString((String) object);
			} catch (IOException e) {

			}
		}

		if (object instanceof JsonValue) {
			return (T) this.addToObjectStack(clazz, (JsonValue) object);
		}

		return (T) object;
	}

	public Object addToObjectStack(Object object) throws DeserializeException {
		return this.addToObjectStack(Object.class, object);
	}

	public Object addToObjectStack(Class<?> clazz, Object object) throws DeserializeException {
		Object result = this.createAtToObject(true, clazz, object);

		if (!this.working) {
			this.working = true;
			while (!this.objectStack.isEmpty()) {
				Object targetObject = this.objectStack.pop();
				JsonValue targetKson = this.jsonStack.pop();

				Class<? extends Object> targetObjectClass = targetObject.getClass();

				if (targetKson instanceof JsonObject) {
					JsonObject jsonValue = (JsonObject) targetKson;

					for (Field field : this.getAccessibleFields(targetObjectClass)) {
						try {
							Object createAtToObject = createAtToObject(false, field.getType(), jsonValue.get(field.getName()));

							if (createAtToObject.getClass() != field.getType()) {
								createAtToObject = castToType(field.getType(), createAtToObject);
							}

							field.set(targetObject, createAtToObject);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							throw new DeserializeException("Deserialize failed because can't access the field.");
						}
					}
				} else {
					JsonArray jsonValue = (JsonArray) targetKson;
					int size = jsonValue.size();

					for (int index = 0; index < size; index++) {
						Class<?> arrayComponentType = targetObjectClass.getComponentType();

						if (arrayComponentType == null) {
							arrayComponentType = targetObjectClass;
						}

						Object createAtToObject = createAtToObject(false, arrayComponentType, jsonValue.get(index));

						if (createAtToObject.getClass() != arrayComponentType) {
							createAtToObject = castToType(arrayComponentType, createAtToObject);
						}

						ArrayAccessor.set(targetObject, index, createAtToObject);
					}
				}
			}
			this.working = false;
			this.objectStack.reset();
			this.jsonStack.reset();
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	private Object createAtToObject(boolean first, Class<?> type, Object originalValue) throws DeserializeException {
		Object primaryId = null;

		if (originalValue == null)
			return null;

		if (type.isEnum())
			return Enum.valueOf((Class<Enum>) type, originalValue.toString());

		if (originalValue instanceof JsonObject) {
			JsonObject wrappingObject = (JsonObject) originalValue;

			if (wrappingObject.containsKey("#class")) {
				try {
					type = this.getClassByName(wrappingObject.get("#class").toString());
				} catch (ClassNotFoundException e) {
					throw new DeserializeException("Deserialize failed because can't find target class.");
				}
				originalValue = wrappingObject.get("#data");
			} else if (wrappingObject.containsKey("@id")) {
				primaryId = wrappingObject.get("@id");
			} else if (first) {
				Field primaryKeyField = getPrimaryKeyField(type);

				if (primaryKeyField != null) {
					primaryId = wrappingObject.get(primaryKeyField.getName());
				}
			}
		}

		if (primaryId == null) {
			Transformer<Object> transformer = (Transformer<Object>) this.getTransformer(type);

			if (transformer != null) {
				Class<?> realType = type;

				type = originalValue.getClass();
				originalValue = transformer.deserialize(this, realType, originalValue);
			}
		}

		Object convertedValue = originalValue;

		if (this.isNeedSerialize(type)) {
			boolean useStack = true;

			if (convertedValue instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray) convertedValue;
				Class<?> componentType = type.getComponentType();

				if (componentType == null) {
					componentType = type.getClass();
				}

				convertedValue = Array.newInstance(componentType, jsonArray.size());
			} else if (convertedValue instanceof JsonObject) {
				if (primaryId == null) {
					try {
						convertedValue = UnsafeAllocator.newInstance(type);
					} catch (Exception e) {
						throw new DeserializeException("Deserialize failed because can't allocation object.");
					}
				} else {
					if (!this.primaryObjects.containsKey(type)) {
						this.primaryObjects.put(type, new HashMap<Object, Object>());
					}

					HashMap<Object, Object> hashMap = this.primaryObjects.get(type);

					if (!hashMap.containsKey(primaryId)) {
						try {
							hashMap.put(primaryId, UnsafeAllocator.newInstance(type));
						} catch (Exception e) {
							throw new DeserializeException("Deserialize failed because can't allocation primary object.");
						}
					}

					convertedValue = hashMap.get(primaryId);
					useStack = first;
				}
			}

			if (useStack && convertedValue != null) {
				this.jsonStack.push((JsonValue) originalValue);
				this.objectStack.push(convertedValue);
			}
		}

		return convertedValue;
	}

	public JsonValue fromObject(Object object) throws SerializeException {
		if (!this.working) {
			return (JsonValue) addFromObjectStack(object);
		} else {
			throw new SerializeException("This context already running serialize!");
		}
	}

	public Object addFromObjectStack(Object object) throws SerializeException {
		Object result = null;

		if (!this.working) {
			this.working = true;
			result = this.createAtFromObject(true, object.getClass(), object);

			while (!this.objectStack.isEmpty()) {
				Object targetObject = this.objectStack.pop();
				JsonValue targetKson = this.jsonStack.pop();

				if (targetKson instanceof JsonObject) {
					JsonObject jsonValue = (JsonObject) targetKson;
					for (Field field : this.getAccessibleFields(targetObject.getClass())) {
						try {
							jsonValue.put(field.getName(), this.createAtFromObject(false, field.getType(), field.get(targetObject)));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new SerializeException("Serialize failed because object could can't get from field.");
						}
					}
				} else {
					JsonArray jsonValue = (JsonArray) targetKson;
					int length = Array.getLength(targetObject);

					for (int index = 0; index < length; index++) {
						Class<?> arrayComponentType = targetObject.getClass().getComponentType();

						if (arrayComponentType == null) {
							arrayComponentType = targetObject.getClass();
						}

						jsonValue.add(this.createAtFromObject(false, arrayComponentType, ArrayAccessor.get(targetObject, index)));
					}
				}
			}

			this.objectStack.reset();
			this.jsonStack.reset();
			this.working = false;
		} else {
			result = this.createAtFromObject(false, Object.class, object);
		}

		return result;
	}

	private Object createAtFromObject(boolean first, Class<?> type, Object originalValue) throws SerializeException {
		if (originalValue == null)
			return null;

		if (originalValue.getClass().isEnum())
			return originalValue.toString();

		Class<? extends Object> originalValueType = originalValue.getClass();

		Transformer<Object> transformer = (Transformer<Object>) this.getTransformer(originalValueType);

		if (transformer != null) {
			originalValue = transformer.serialize(this, originalValue);
		}

		if (!first && this.useCustomTag) {
			Field primaryKeyField = getPrimaryKeyField(originalValueType);

			if (primaryKeyField != null) {
				JsonObject wrappingObject = new JsonObject();

				try {
					wrappingObject.put("@id", primaryKeyField.get(originalValue));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new SerializeException("Serialize failed because primary key can't get from field.");
				}

				originalValue = wrappingObject;
			}
		}

		Object convertedKsonValue = originalValue;

		if (this.isNeedSerialize(convertedKsonValue.getClass())) {
			if (originalValue.getClass().isArray()) {
				convertedKsonValue = new JsonArray();
			} else {
				convertedKsonValue = new JsonObject();
			}

			this.objectStack.push(originalValue);
			this.jsonStack.push((JsonValue) convertedKsonValue);
		}

		if (this.isNeedSerialize(originalValueType) && type != originalValueType && this.useCustomTag) {
			JsonObject wrappingObject = new JsonObject();

			wrappingObject.put("#class", originalValueType.getName());
			wrappingObject.put("#data", convertedKsonValue);

			convertedKsonValue = wrappingObject;
		}

		return convertedKsonValue;
	}

	public JsonValue fromString(String kson) throws IOException {
		this.valueStack.reset();
		this.modeStack.reset();

		modeStack.push(ValueMode.NONE);

		int pointer = 0;

		int length = kson.length();
		char[] charArray = kson.toCharArray();
		boolean decimal = false;
		char lastValidChar = ' ';
		try {
			ValueMode currentMode = modeStack.peek();
			while (pointer <= length - 1) {
				char currentChar = charArray[pointer];

				if (currentMode == ValueMode.NONE || currentMode == ValueMode.OBJECT || currentMode == ValueMode.ARRAY) {
					if (!(currentChar == ' ' || currentChar == '\t' || currentChar == '\n')) {
						if (currentChar == '{') {
							valueStack.push(new JsonObject());
							modeStack.push(ValueMode.OBJECT);
							currentMode = modeStack.peek();
						} else if (currentChar == '[') {
							valueStack.push(new JsonArray());
							modeStack.push(ValueMode.ARRAY);
							currentMode = modeStack.peek();
						} else if (currentChar == '\"') {
							modeStack.push(ValueMode.STRING);
							currentMode = modeStack.peek();
							this.stringMaker.reset();
						} else if ((currentChar >= '0' && currentChar <= '9') || currentChar == '-') {
							modeStack.push(ValueMode.NUMBER);
							currentMode = modeStack.peek();
							decimal = false;
							this.stringMaker.reset();
							pointer--;
						} else if (currentChar == 't') {
							valueStack.push(true);
							pointer += 3;
						} else if (currentChar == 'f') {
							valueStack.push(false);
							pointer += 4;
						} else if (currentChar == 'n') {
							valueStack.push(null);
							pointer += 3;
						} else {
							if (currentMode == ValueMode.OBJECT) {
								if (currentChar == ',' || currentChar == '}') {
									if (lastValidChar != '{') {
										Object value = valueStack.pop();
										Object key = valueStack.pop();

										JsonObject jsonObject = (JsonObject) valueStack.peek();
										jsonObject.put(key, value);
									}

									if (currentChar == '}') {
										modeStack.pop();
										currentMode = modeStack.peek();
									}
								}
							} else if (currentMode == ValueMode.ARRAY) {
								if (currentChar == ',' || currentChar == ']') {
									if (lastValidChar != '[') {
										Object value = valueStack.pop();

										JsonArray jsonArray = (JsonArray) valueStack.peek();
										jsonArray.add(value);
									}

									if (currentChar == ']') {
										modeStack.pop();
										currentMode = modeStack.peek();
									}
								}
							}
						}

						lastValidChar = currentChar;
					}
				} else if (currentMode == ValueMode.STRING) {
					if (currentChar == '\\') {
						char nextChar = charArray[pointer + 1];

						switch (nextChar) {
						case '"':
							this.stringMaker.add('\"');
							break;
						case '\\':
							this.stringMaker.add('\\');
							break;
						case 'b':
							this.stringMaker.add('\b');
							break;
						case 'f':
							this.stringMaker.add('\f');
							break;
						case 'n':
							this.stringMaker.add('\n');
							break;
						case 'r':
							this.stringMaker.add('\r');
							break;
						case 't':
							this.stringMaker.add('\t');
							break;
						}

						pointer++;
					} else if (currentChar == '\"') {
						valueStack.push(new String(this.stringMaker.toString()));
						modeStack.pop();
						currentMode = modeStack.peek();
					} else {
						this.stringMaker.add(currentChar);
					}
				} else if (currentMode == ValueMode.NUMBER) {
					if (!(currentChar >= '0' && currentChar <= '9') && currentChar != '-' && currentChar != 'D' && currentChar != 'd' && currentChar != 'F' && currentChar != 'f' && currentChar != 'L' && currentChar != 'l' && currentChar != 'B' && currentChar != 'b' && currentChar != '.') {
						modeStack.pop();
						currentMode = modeStack.peek();

						char last = this.stringMaker.last();

						switch (last) {
						case 'd':
						case 'D':
							this.stringMaker.remove();
							this.valueStack.push(Double.parseDouble(this.stringMaker.toString()));
							break;
						case 'f':
						case 'F':
							this.stringMaker.remove();
							this.valueStack.push(Float.parseFloat(this.stringMaker.toString()));
							break;
						case 'l':
						case 'L':
							this.stringMaker.remove();
							this.valueStack.push(Long.parseLong(this.stringMaker.toString()));
							break;
						case 'b':
						case 'B':
							this.stringMaker.remove();
							this.valueStack.push(Byte.parseByte(this.stringMaker.toString()));
							break;
						default:
							if (decimal) {
								double value = Double.parseDouble(this.stringMaker.toString());

								if (value == (double) (float) value) {
									this.valueStack.push((float) value);
								} else {
									this.valueStack.push(value);
								}
							} else {
								long value = Long.parseLong(this.stringMaker.toString());

								if (value == (long) (int) value) {
									this.valueStack.push((int) value);
								} else {
									this.valueStack.push(value);
								}
							}
						}

						pointer--;
					} else {
						if (!decimal && currentChar == '.') {
							decimal = true;
						}

						this.stringMaker.add(currentChar);
					}
				}

				pointer++;

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("An exception occurred at " + (pointer - 1) + " position character(" + charArray[(pointer - 1)] + ")");
		}

		return (JsonValue) valueStack.pop();
	}
}

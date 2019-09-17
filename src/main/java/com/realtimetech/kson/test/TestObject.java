package com.realtimetech.kson.test;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.realtimetech.kson.annotation.ExceptionField;
import com.realtimetech.reflection.access.ArrayAccessor;

public class TestObject {
	private Test test;

	@ExceptionField
	private byte[] bytes;

	private Test[] testArray;

	private int[] intArray;
	private String[] stringArray;

	private int integer;
	private String string;

	private TestInterfaceImpl testInterfaceImpl;
	private TestAbstractImpl testAbstractImpl;

	private TestInterface testInterface;
	private TestAbstract testAbstract;

	private TestInterfaceImpl[] testInterfaceImplArray;
	private TestAbstractImpl[] testAbstractImplArray;

	private TestInterface[] testInterfaceArray;
	private TestAbstract[] testAbstractArray;

	private Date date;
	private Date[] dateArray;

	private ArrayList<String> stringArrayList;
	private List<String> stringList;

	private ArrayList<Date> dateArrayList;
	private List<Date> dateList;

	private HashMap<String, String> stringMap;
	private HashMap<String, Date> dateMap;
	private HashMap<HashMap<String, String>, HashMap<String, String>> mapMap;

	public String validationObject(TestObject collectObject) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : collectObject.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(ExceptionField.class)) {
				Object originalObject = field.get(this);
				Object targetObject = field.get(collectObject);

				if (!validation(originalObject, targetObject)) {
					return field.getName();
				}
			}
		}

		return null;
	}

	private boolean validation(Object originalObject, Object targetObject) {
		if (originalObject.getClass() == targetObject.getClass()) {
			if (originalObject.getClass().isArray() && targetObject.getClass().isArray()) {
				int originalLength = Array.getLength(originalObject);
				int targetLength = Array.getLength(targetObject);

				if (originalLength == targetLength) {
					for (int index = 0; index < originalLength; index++) {
						Object originalElementObject = ArrayAccessor.get(originalObject, index);
						Object targetElementObject = ArrayAccessor.get(targetObject, index);

						if (originalElementObject.getClass() != targetElementObject.getClass()) {
							return false;
						}
					}
				} else {
					return false;
				}
			} else if (originalObject instanceof Map && targetObject instanceof Map) {
				Map<?, ?> originalMap = (Map<?, ?>) originalObject;
				Map<?, ?> targetMap = (Map<?, ?>) targetObject;

				if (originalMap.keySet().size() == targetMap.keySet().size()) {

					for (Object originalKeyObject : originalMap.keySet()) {
						if (targetMap.containsKey(originalKeyObject)) {
							Object originalValueObject = originalMap.get(originalKeyObject);
							Object targetValueObject = targetMap.get(originalKeyObject);

							if (!validation(originalValueObject, targetValueObject)) {
								return false;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			} else if (originalObject instanceof List && targetObject instanceof List) {
				List<?> originalList = (List<?>) originalObject;
				List<?> targetList = (List<?>) targetObject;

				if (originalList.size() == targetList.size()) {
					for (int i = 0; i < originalList.size(); i++) {
						if (!validation(originalList.get(i), targetList.get(i))) {
							return false;
						}
					}

				} else {
					return false;
				}
			} else {
				if (!originalObject.toString().contains("@")) {
					if (!originalObject.toString().contentEquals(targetObject.toString())) {
						return false;
					}
				}

			}
		} else {
			return false;
		}

		return true;
	}

	public TestObject(Test test) {
		this.test = test;

		this.testArray = new Test[] { test, test };

		this.intArray = new int[] { 1, 2, 3, 4 };
		this.integer = 1;

		this.stringArray = new String[] { "A", "B", "C" };
		this.string = "ABC";

		this.testInterfaceImpl = new TestInterfaceImpl();
		this.testAbstractImpl = new TestAbstractImpl();

		this.testInterface = new TestInterfaceImpl();
		this.testAbstract = new TestAbstractImpl();

		this.bytes = new byte[255];
		{
			for (byte i = -128; i < Byte.MAX_VALUE; i++) {
				this.bytes[i + 128] = i;
			}
		}

		this.testInterfaceImplArray = new TestInterfaceImpl[2];
		{
			this.testInterfaceImplArray[0] = new TestInterfaceImpl();
			this.testInterfaceImplArray[1] = new TestInterfaceImpl();
		}

		this.testAbstractImplArray = new TestAbstractImpl[2];
		{
			this.testAbstractImplArray[0] = new TestAbstractImpl();
			this.testAbstractImplArray[1] = new TestAbstractImpl();
		}

		this.testInterfaceArray = new TestInterface[2];
		{
			this.testInterfaceArray[0] = new TestInterfaceImpl();
			this.testInterfaceArray[1] = new TestInterfaceImpl();
		}

		this.testAbstractArray = new TestAbstract[2];
		{
			this.testAbstractArray[0] = new TestAbstractImpl();
			this.testAbstractArray[1] = new TestAbstractImpl();
		}

		this.date = new Date();

		this.dateArray = new Date[2];
		{
			this.dateArray[0] = new Date();
			this.dateArray[1] = new Date();
		}

		this.stringArrayList = new ArrayList<String>();
		{
			this.stringArrayList.add("ABC");
			this.stringArrayList.add("ABC");
			this.stringArrayList.add("ABC");
			this.stringArrayList.add("ABC");
		}

		this.stringList = new ArrayList<String>();
		{
			this.stringList.add("ABC");
			this.stringList.add("ABC");
			this.stringList.add("ABC");
			this.stringList.add("ABC");
		}

		this.dateArrayList = new ArrayList<Date>();
		{
			this.dateArrayList.add(new Date());
			this.dateArrayList.add(new Date());
			this.dateArrayList.add(new Date());
			this.dateArrayList.add(new Date());
		}

		this.dateList = new ArrayList<Date>();
		{
			this.dateList.add(new Date());
			this.dateList.add(new Date());
			this.dateList.add(new Date());
			this.dateList.add(new Date());
		}

		this.stringMap = new HashMap<String, String>();
		{
			this.stringMap.put("A", "ABC");
			this.stringMap.put("B", "ABC");
		}

		this.dateMap = new HashMap<String, Date>();
		{
			this.dateMap.put("A", new Date());
			this.dateMap.put("B", new Date());
		}

		this.mapMap = new HashMap<HashMap<String, String>, HashMap<String, String>>();
		{
			{
				HashMap<String, String> keyMap = new HashMap<String, String>();
				HashMap<String, String> valueMap = new HashMap<String, String>();
				keyMap.put("A", "ABC");
				keyMap.put("B", "ABC");
				valueMap.put("A", "ABC");
				valueMap.put("B", "ABC");
				this.mapMap.put(keyMap, valueMap);
			}
			{
				HashMap<String, String> keyMap = new HashMap<String, String>();
				HashMap<String, String> valueMap = new HashMap<String, String>();
				keyMap.put("C", "ABC");
				keyMap.put("D", "ABC");
				valueMap.put("C", "ABC");
				valueMap.put("D", "ABC");
				this.mapMap.put(keyMap, valueMap);
			}
		}
	}

	public Test getTest() {
		return test;
	}

	public int[] getIntArray() {
		return intArray;
	}

	public int getInteger() {
		return integer;
	}

	public String[] getStringArray() {
		return stringArray;
	}

	public String getString() {
		return string;
	}

	public Test[] getTestArray() {
		return testArray;
	}

	public TestAbstract getTestAbstract() {
		return testAbstract;
	}

	public TestAbstract[] getTestAbstractArray() {
		return testAbstractArray;
	}

	public TestAbstractImpl getTestAbstractImpl() {
		return testAbstractImpl;
	}

	public TestAbstractImpl[] getTestAbstractImplArray() {
		return testAbstractImplArray;
	}

	public TestInterface getTestInterface() {
		return testInterface;
	}

	public TestInterface[] getTestInterfaceArray() {
		return testInterfaceArray;
	}

	public TestInterfaceImpl getTestInterfaceImpl() {
		return testInterfaceImpl;
	}

	public TestInterfaceImpl[] getTestInterfaceImplArray() {
		return testInterfaceImplArray;
	}

	public Date getDate() {
		return date;
	}

	public Date[] getDateArray() {
		return dateArray;
	}

	public ArrayList<Date> getDateArrayList() {
		return dateArrayList;
	}

	public List<Date> getDateList() {
		return dateList;
	}

	public ArrayList<String> getStringArrayList() {
		return stringArrayList;
	}

	public List<String> getStringList() {
		return stringList;
	}

	public HashMap<String, Date> getDateMap() {
		return dateMap;
	}

	public HashMap<HashMap<String, String>, HashMap<String, String>> getMapMap() {
		return mapMap;
	}

	public HashMap<String, String> getStringMap() {
		return stringMap;
	}
}

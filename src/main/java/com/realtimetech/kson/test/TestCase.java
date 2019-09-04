package com.realtimetech.kson.test;

import com.realtimetech.kson.KsonContext;
import com.realtimetech.kson.element.KsonObject;

public class TestCase {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		KsonContext ksonContext = new KsonContext();

		{
			String fieldTest;

			Test test1 = new Test(10, "Hello");
			TestObject testObject1 = new TestObject(test1);

			System.out.println("## First Converting");
			System.out.println();

			KsonObject testObjectKsonObject1 = (KsonObject) ksonContext.fromObject(testObject1);
			System.out.println("   TestObject\t: " + testObjectKsonObject1);

			KsonObject testKsonObject1 = (KsonObject) ksonContext.fromObject(test1);
			System.out.println("   Test\t\t: " + testKsonObject1);

			System.out.println();
			System.out.println("   Result\t= " + testObjectKsonObject1);
			System.out.println();
			System.out.println();

			System.out.println("## Seconds Converting");
			System.out.println();

			TestObject testObject2 = ksonContext.toObject(TestObject.class, testObjectKsonObject1);
			System.out.print("   Validation late works............... ");
			if ((fieldTest = testObject2.validationObject(testObject1)) != null) {
				System.out.println("OK!");
			} else {
				System.out.println("FAIL.");
			}
			Test test2 = ksonContext.toObject(Test.class, testKsonObject1);

			System.out.print("   Validation two object............... ");
			if ((fieldTest = testObject2.validationObject(testObject1)) == null) {
				System.out.println("OK!");
			} else {
				System.out.println("FAIL. " + fieldTest);
			}
			System.out.println();

			KsonObject testObjectKsonObject2 = (KsonObject) ksonContext.fromObject(testObject2);
			System.out.println("   TestObject\t: " + testObjectKsonObject2);

			KsonObject testKsonObject2 = (KsonObject) ksonContext.fromObject(test1);
			System.out.println("   Test\t\t: " + testKsonObject2);

			System.out.println();
			System.out.println("   Result\t= " + testObjectKsonObject2);
			System.out.println();
			System.out.println();
		}

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		{
			TestObject collectObject = new TestObject(new Test(10, "100"));
			Long startTime = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				KsonObject fromObject = (KsonObject) ksonContext.fromObject(collectObject);
				TestObject good = ksonContext.toObject(TestObject.class,
						ksonContext.fromString(fromObject.toJsonString()));
				KsonObject toObject = (KsonObject) ksonContext.fromObject(good);
			}
			Long endTime = System.currentTimeMillis();

			System.out.println("Kson : " + ((endTime - startTime)) + "ms");
		}

		while (true) {
			Thread.sleep(100000);
		}

	}
}

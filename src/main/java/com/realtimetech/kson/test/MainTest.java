package com.realtimetech.kson.test;

import com.realtimetech.kson.KsonContext;
import com.realtimetech.kson.builder.KsonBuilder;
import com.realtimetech.kson.element.JsonObject;
import com.realtimetech.kson.element.JsonValue;
import com.realtimetech.kson.exception.SerializeException;
import com.realtimetech.kson.util.pool.KsonPool;

public class MainTest {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		KsonBuilder ksonBuilder = new KsonBuilder();
		final KsonPool ksonPool = new KsonPool(ksonBuilder);

		System.out.println("## Thread Starting");
		Thread[] threads = new Thread[5];
		for (int i = 0; i < 5; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					TestObject testObject = new TestObject(new Test(1234, "Yallo"));
					KsonContext ksonContext = ksonPool.get();

					System.out.println("   Context Start: " + ksonContext);
					for (int i = 0; i < 100000; i++) {
						try {
							JsonValue fromObject = ksonContext.fromObject(testObject);
						} catch (SerializeException e) {
							e.printStackTrace();
						}
					}
					System.out.println("   Context Done: " + ksonContext);
				}
			});

			threads[i].start();
			Thread.sleep(1000);
		}

		for (int i = 0; i < 5; i++) {
			if (threads[i] != null)
				threads[i].join();
		}

		KsonContext ksonContext = ksonBuilder.build();

		{
			String fieldTest;

			Test test1 = new Test(10, "Hello");
			TestObject testObject1 = new TestObject(test1);

			System.out.println();
			System.out.println("## First Converting");
			System.out.println();

			JsonObject testObjectKsonObject1 = (JsonObject) ksonContext.fromObject(testObject1);
			System.out.println("   TestObject\t: " + testObjectKsonObject1);

			JsonObject testKsonObject1 = (JsonObject) ksonContext.fromObject(test1);
			System.out.println("   Test\t\t: " + testKsonObject1);

			System.out.println();
			System.out.println("   Result\t= " + testObjectKsonObject1);
			System.out.println();
			System.out.println();

			System.out.println("## Seconds Converting");
			System.out.println();

			TestObject testObject2 = ksonContext.toObject(TestObject.class, testObjectKsonObject1.toKsonString());
			System.out.print("   Validation late works............... ");
			if ((fieldTest = testObject2.validationObject(testObject1)) != null) {
				System.out.println("OK!");
			} else {
				System.out.println("FAIL.");
			}
			Test test2 = ksonContext.toObject(Test.class, testKsonObject1.toKsonString());

			System.out.print("   Validation two object............... ");
			if ((fieldTest = testObject2.validationObject(testObject1)) == null) {
				System.out.println("OK!");
			} else {
				System.out.println("FAIL. " + fieldTest);
			}
			System.out.println();

			JsonObject testObjectKsonObject2 = (JsonObject) ksonContext.fromObject(testObject2);
			System.out.println("   TestObject\t: " + testObjectKsonObject2);

			JsonObject testKsonObject2 = (JsonObject) ksonContext.fromObject(test1);
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
			for (int i = 0; i < 100000; i++) {
				JsonObject fromObject = (JsonObject) ksonContext.fromObject(collectObject);
				TestObject good = ksonContext.toObject(TestObject.class, fromObject.toKsonString());
				JsonObject toObject = (JsonObject) ksonContext.fromObject(good);
			}
			Long endTime = System.currentTimeMillis();

			System.out.println("Kson : " + ((endTime - startTime)) + "ms");
		}

		while (true) {
			Thread.sleep(100000);
		}

	}
}

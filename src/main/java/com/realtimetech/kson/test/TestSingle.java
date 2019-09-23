package com.realtimetech.kson.test;

import java.util.LinkedList;

public class TestSingle {
	private LinkedList<TestSmall> testSmallList;

	public TestSingle() {
		this.testSmallList = new LinkedList<TestSmall>();
		{
			this.testSmallList.add(new TestSmall());
			this.testSmallList.add(new TestSmall());
			this.testSmallList.add(new TestSmall());
		}
	}
}

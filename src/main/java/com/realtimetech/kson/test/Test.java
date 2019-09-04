package com.realtimetech.kson.test;

import java.util.Random;

import com.realtimetech.kson.primary.PrimaryKey;

public class Test {
	@PrimaryKey
	private int id;

	private int value1;
	private String value2;

	public Test(int value1, String value2) {
		this.id = (new Random()).nextInt();
		this.value1 = value1;
		this.value2 = value2;
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public String toString() {
		return id + "";
	}
}

package com.realtimetech.kson.test;

public enum TestEnum {
	TYPE_1(1), TYPE_2(2);

	private int code;

	private TestEnum(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}

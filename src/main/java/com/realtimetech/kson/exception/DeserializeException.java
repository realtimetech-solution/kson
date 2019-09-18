package com.realtimetech.kson.exception;

public class DeserializeException extends Exception {
	private static final long serialVersionUID = -2041738655817004217L;

	private String message;

	public DeserializeException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String getLocalizedMessage() {
		return message;
	}
}

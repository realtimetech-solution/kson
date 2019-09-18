package com.realtimetech.kson.exception;

public class SerializeException extends Exception{
	private static final long serialVersionUID = -8508412598488843333L;

	private String message;

	public SerializeException(String message) {
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

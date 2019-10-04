package com.realtimetech.kson.util.string;

import java.util.Arrays;

public class StringMaker {
	private int raiseSize;

	private char[] chars;

	private int currentIndex;
	private int scope;

	public StringMaker() {
		this(10);
	}

	public StringMaker(int raiseSize) {
		this.raiseSize = raiseSize;
		this.currentIndex = -1;
		this.scope = 0;

		this.raiseArrays();
	}

	private void raiseArrays() {
		this.scope++;

		char[] oldObjects = this.chars;

		this.chars = new char[scope * this.raiseSize];

		if (oldObjects != null) {
			for (int i = 0; i <= currentIndex; i++) {
				this.chars[i] = oldObjects[i];
			}
		}
	}

	public void add(char object) {
		if (this.currentIndex + 2 >= this.scope * this.raiseSize) {
			raiseArrays();
		}

		this.currentIndex++;
		this.chars[this.currentIndex] = object;
	}

	public char get(int index) {
		return this.chars[index];
	}

	public char last() {
		return this.chars[this.currentIndex];
	}

	public void remove() {
		this.currentIndex--;
	}

	public char[] toArray() {
		return Arrays.copyOfRange(chars, 0, this.currentIndex + 1);
	}
	
	public String toString() {
		return new String(chars, 0, this.currentIndex + 1);
	}

	public void reset() {
		this.currentIndex = -1;
	}

	public int getSize() {
		return currentIndex + 1;
	}
}

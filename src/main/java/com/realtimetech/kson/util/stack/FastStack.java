package com.realtimetech.kson.util.stack;

import java.util.EmptyStackException;

public class FastStack<T> {
	private int raiseSize;

	private T[] objects;

	private int currentIndex;
	private int scope;
	
	private int startIndex;

	public FastStack() {
		this(10);
	}

	public FastStack(int raiseSize) {
		this.raiseSize = raiseSize;
		this.currentIndex = -1;
		this.startIndex = 0;
		this.scope = 0;

		this.raiseArrays();
	}

	@SuppressWarnings("unchecked")
	private void raiseArrays() {
		this.scope++;

		T[] oldObjects = this.objects;

		this.objects = (T[]) new Object[scope * this.raiseSize];

		if (oldObjects != null) {
			for (int i = 0; i <= currentIndex; i++) {
				this.objects[i] = oldObjects[i];
			}
		}
	}

	public void push(T object) {
		if (this.currentIndex + 2 >= this.scope * this.raiseSize) {
			raiseArrays();
		}

		this.currentIndex++;
		this.objects[this.currentIndex] = object;
	}

	public T[] getArray() {
		return objects;
	}
	
	public boolean isEmpty() {
		return this.currentIndex == -1;
	}

	public T peek() {
		if (this.currentIndex == -1)
			throw new EmptyStackException();

		return this.objects[this.currentIndex];
	}

	public T pop() {
		if (this.currentIndex == -1)
			throw new EmptyStackException();

		T object = this.objects[this.currentIndex];

		this.objects[this.currentIndex] = null;
		this.currentIndex--;

		return object;
	}

	public T shift() {
		if (this.currentIndex == -1)
			throw new EmptyStackException();

		return this.objects[this.startIndex++];
	}

	public void reset() {
		this.currentIndex = -1;
		this.startIndex = 0;
	}

	public int getSize() {
		return this.currentIndex + 1;
	}
}

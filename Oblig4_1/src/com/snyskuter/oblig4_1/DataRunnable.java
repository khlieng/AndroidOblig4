package com.snyskuter.oblig4_1;

public abstract class DataRunnable<T> implements Runnable {
	private T data;
	public void setData(T data) { this.data = data; }		
	public T getData() { return data; }
	
	public abstract void run();
}

package com.hdbsnc.smartiot.util.queue;

public interface Queue {

	public void enqueue(Object item);

	public Object dequeue();

	public int size();

	public boolean isEmpty();
}

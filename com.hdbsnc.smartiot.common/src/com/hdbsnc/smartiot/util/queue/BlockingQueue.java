package com.hdbsnc.smartiot.util.queue;

import java.util.LinkedList;

import com.hdbsnc.smartiot.util.queue.Queue;

public class BlockingQueue implements Queue{

	private LinkedList queue;

	public BlockingQueue(){
		queue = new LinkedList();
	}

	public Object dequeue() {
		synchronized(queue){
			return queue.removeFirst();
		}
	}

	public Object blockingDequeue() throws InterruptedException{

		synchronized(queue){
			while (queue.isEmpty()){
				queue.wait();
			}
			return queue.removeFirst();
		}
	}

	public void blockingenqueue(Object item){
		synchronized(queue){
			queue.addLast(item);
			queue.notify();
		}

	}

	public void enqueue(Object item) {
		synchronized(queue){
			queue.addLast(item);
		}
	}

	public boolean isEmpty() {
		synchronized(queue){
			return queue.isEmpty();
		}
	}

	public int size() {
		return queue.size();
	}
}

package com.hdbsnc.smartiot.util.servicepool.pool;

import java.util.LinkedList;

import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;


public class WorkQueue {

	private LinkedList workList = new LinkedList();

	private boolean closed = false;

	public synchronized void enqueue(Runnable work) throws AlreadyClosedException {
		if (closed) {
			throw new AlreadyClosedException();
		}
		workList.addLast(work);
		notify();
	}

	public synchronized Runnable dequeue() throws AlreadyClosedException, InterruptedException {
		while( workList.size() <= 0 ) {
			wait();
			if ( closed ) {
				throw new AlreadyClosedException();
			}
		}
		return (Runnable)workList.removeFirst();
	}

	public synchronized int size() {
		return workList.size();
	}

	public synchronized boolean isEmpty() {
		return workList.size() == 0;
	}

	public synchronized void close() {
		closed = true;
		notifyAll();
	}
}

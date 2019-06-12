package com.hdbsnc.smartiot.util.servicepool.block;


public class BlockObject {

	private int count;

	public BlockObject() {
		count = 0;
	}

	public synchronized void increase() {
		count++;
		notifyAll();
	}
	public synchronized void decrease() {
		count--;
		notifyAll();
	}

	public synchronized void waitTermination() {
		try {
			while (count > 0) {
				wait(3000);
			}
		} catch(InterruptedException ex) {

		}
	}
}
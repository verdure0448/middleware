package com.hdbsnc.smartiot.util.servicepool.future;


public class FutureThread extends Thread {

	private FutureRunnable target = null;
	private Object result = null;

	public FutureThread(FutureRunnable target) {
		this.target = target;
		this.start();
	}

	public void run() {
		this.result = target.run();
	}

	public Object getResult() throws InterruptedException {
		if (Thread.currentThread() != null) {
			this.join();
		}
		return result;
	}

	public Object getResult(long timeout) throws InterruptedException {
		if (Thread.currentThread() != null) {
			this.join(timeout);
		}
		return result;
	}
}

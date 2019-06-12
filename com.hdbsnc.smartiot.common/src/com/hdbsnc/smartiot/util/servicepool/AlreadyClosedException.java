package com.hdbsnc.smartiot.util.servicepool;

/**
 * WorkQueue 클래스의 enqueue(Runnable work) 메소드와 dequeue() 메소드를 호출할 때,
 * 이미 WorkQueue가 닫힌 상태일 경우 발생한다.
 *
 *
 */
public class AlreadyClosedException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -3922016816344398308L;

	public AlreadyClosedException(String msg) {
		super(msg);
	}

	public AlreadyClosedException() {
		super();
	}
}

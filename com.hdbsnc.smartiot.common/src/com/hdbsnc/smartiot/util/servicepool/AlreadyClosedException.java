package com.hdbsnc.smartiot.util.servicepool;

/**
 * WorkQueue Ŭ������ enqueue(Runnable work) �޼ҵ�� dequeue() �޼ҵ带 ȣ���� ��,
 * �̹� WorkQueue�� ���� ������ ��� �߻��Ѵ�.
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

package com.hdbsnc.smartiot.adapter.zeromq.api;


import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class ZeromqApi {

	public interface IEvent {
		void onRecv(byte[] msg);
	}

	private ZMQ.Context mContext = null;
	private ZMQ.Socket mSocket = null;

	private int mIoThreads;
	private SocketType mSocketYype;

	private String mAddr = null;
	private IEvent mEvent = null;

	private Thread mEventThread = null;

	/**
	 * 생성자
	 * 
	 * @param ioThreads 입출력 스레스수
	 * @param type      소켓 타입
	 */
	public ZeromqApi(int ioThreads, SocketType type, String addr, IEvent event) {
		this.mIoThreads = ioThreads;
		this.mSocketYype = type;
		this.mAddr = addr;
		this.mEvent = event;
	}

	/**
	 * 소켓 시작
	 * 
	 * @param addr 바이딩 어드레스
	 */
	public void start() {
		// TODO 기동상태 체크 후 기동중일 경우 에러 처리

		this.mContext = ZMQ.context(this.mIoThreads);
		this.mSocket = mContext.socket(this.mSocketYype);

		this.mSocket.bind(this.mAddr);

		this.mEventThread = new Thread(new EventProcess());

		mEventThread.start();
	}

	/**
	 * 소켓 중지
	 * 
	 * @throws InterruptedException
	 */
	public void stop() throws InterruptedException {

		mEventThread.interrupt();

		// TODO 스레드 종료 때까지 대기하도록 처리 변경 필요 임시로 30ms대기
		Thread.sleep(30);

		if (this.mSocket != null) {
			this.mSocket.close();
			this.mSocket = null;
		}

		if (this.mContext == null) {
			this.mContext.term();
			this.mContext = null;
		}
	}

	public void send(byte[] msg) {

		// mSocket.sendMore(data)

		mSocket.send(msg);

	}

	class EventProcess implements Runnable {

		@Override
		public void run() {
			try {
				System.out.println("recv 시작.");
				while (!Thread.currentThread().isInterrupted()) {
					byte[] req = mSocket.recv(0);

					// '\r\n' 코드 체크 -> 사실상 필요 없음

					mEvent.onRecv(req);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}

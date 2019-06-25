package com.hdbsnc.smartiot.adapter.zeromq.api;

import javax.activation.UnsupportedDataTypeException;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZeromqApi {

	public interface IEvent {
		void onRecv(byte[] msg);
	}

	private ZMQ.Context mContext = null;

	private ZContext mZctx = null;

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
	public ZeromqApi(int ioThreads, SocketType type, String addr) {
		this.mIoThreads = ioThreads;
		this.mSocketYype = type;
		this.mAddr = addr;
		
	}

	/**
	 * 소켓 시작
	 * 
	 * @param addr 바이딩 어드레스
	 * @throws Exception
	 */
	public void start(IEvent event) throws UnsupportedDataTypeException {
		// TODO 기동상태 체크 후 기동중일 경우 에러 처리
		
		
		this.mEvent = event;
		
		switch (this.mSocketYype) {
		case REP:
			this.mContext = ZMQ.context(this.mIoThreads);
			this.mSocket = mContext.socket(this.mSocketYype);

			this.mSocket.bind(this.mAddr);

			if (mSocketYype == SocketType.REP) {
				this.mEventThread = new Thread(new EventProcess());

				mEventThread.start();
			}
			break;
		case PUB:
			this.mZctx = new ZContext();
			this.mSocket = this.mZctx.createSocket(this.mSocketYype);
			this.mSocket.bind(this.mAddr);
			break;
		default:
			throw new UnsupportedDataTypeException("Unsupported function.");
		}

	}

	/**
	 * 소켓 중지
	 * 
	 * @throws InterruptedException
	 */
	public void stop() throws InterruptedException {

		if (this.mSocket != null) {
			this.mSocket.close();
			this.mSocket = null;
		}

		switch (this.mSocketYype) {
		case REP:
			mEventThread.interrupt();
			// TODO 스레드 종료 때까지 대기하도록 처리 변경 필요 임시로 30ms대기
			Thread.sleep(30);

			if (this.mContext == null) {
				this.mContext.term();
				this.mContext = null;
			}
			break;
		case PUB:
			mZctx.close();
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * 
	 * @param msg
	 * @throws Exception
	 */
	public void send(byte[] msg) throws Exception {
		if(this.mSocketYype != SocketType.REP) {
			throw new UnsupportedDataTypeException("Unsupported function.");
		}
		mSocket.send(msg);
	}

	/**
	 * 
	 * @param topic
	 * @param msg
	 * @throws UnsupportedDataTypeException 
	 */
	public void publish(byte[] topic, byte[] msg) throws UnsupportedDataTypeException {

		if(this.mSocketYype != SocketType.PUB) {
			throw new UnsupportedDataTypeException("Unsupported function.");
		}
		
		byte[] sendData = new byte[topic.length + msg.length + 1];
		System.arraycopy(topic, 0, sendData, 0, topic.length);
		sendData[topic.length] = 0x20;
		System.arraycopy(msg, 0, sendData, topic.length + 1, msg.length);

		System.out.println(new String(sendData));
		mSocket.send(sendData, 0);
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
				// 예외 무시
			}

		}

	}
}

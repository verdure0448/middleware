package com.hdbsnc.smartiot.adapter.zeromq.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.adapter.zeromq.api.frame.AbstractBlocksFrame;
import com.hdbsnc.smartiot.adapter.zeromq.api.frame.BatchReadWriteProtocol;
import com.hdbsnc.smartiot.adapter.zeromq.api.frame.MultipleBlockBatchReadWriteProtocol;
import com.hdbsnc.smartiot.adapter.zeromq.api.frame.AbstractBlocksFrame.Command;
import com.hdbsnc.smartiot.adapter.zeromq.api.frame.AbstractBlocksFrame.SubCommand;
import com.hdbsnc.smartiot.adapter.zeromq.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.zeromq.api.frame.exception.MitsubishiQSeriesMCCompleteException;
import com.hdbsnc.smartiot.adapter.zeromq.obj.RequestReadObj;
import com.hdbsnc.smartiot.adapter.zeromq.obj.RequestWriteObj;
import com.hdbsnc.smartiot.adapter.zeromq.util.EditUtil;
import com.hdbsnc.smartiot.util.logger.Log;

/**
 * @author dbkim
 * Zero MQ라이브러리 중 사용할 부분만 한번 감싼 Wrapper API
 */
public class MessageQueuePubSubApi {

	private Socket socket;
	private String ip;
	private int port;

	private List<Integer> portArray;
	private int curtPortIndex;

	private Log log;

	public MessageQueuePubSubApi(Log log) {

		this.socket = null;
		
		this.log=log;
	}

	public void open() {

		try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");
		}
	}
	/**
	 * PLC로 연결할 경우 호출한다.
	 * @param ip
	 * @param ports
	 * @throws IOException
	 */
	public void recv(String ip, String ports) throws IOException {
		
		////////////////////////////////////////////////////////////////////////////////////
		// REQ와 RES 에대한 동기화 처리 혹은 풀 관리 예정
		////////////////////////////////////////////////////////////////////////////////////
        byte[] reply = socket.recv(0);
            System.out.println(
                "Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]"
            );
        }
	}



	/**
	 * @param reqData
	 * @return
	 * @throws IOException
	 */
	public byte[] send(byte[] reqData) throws IOException {
		
		////////////////////////////////////////////////////////////////////////////////////
		// REQ와 RES 에대한 동기화 처리 혹은 풀 관리 예정
		////////////////////////////////////////////////////////////////////////////////////
	    String response = "world";
        socket.send(response.getBytes(ZMQ.CHARSET), 0);

        Thread.sleep(1000); //  Do some 'work'
		return result;
	}

	/**
	 * 현재 연결되 있는 PLC와의 접속을 해제한다.
	 * @throws IOException
	 */
	public synchronized void disconnect() throws IOException {
		if (this.socket != null) {
			this.socket.close();
			this.socket=null;
		}
	}

	/**
	 * 현재 연결이 유효한지 확인한다.
	 * @return
	 */
	public synchronized boolean isConnected() {
		if (this.socket != null) {
			if (this.socket.isConnected() && !this.socket.isClosed())
				return true;
		}
		return false;
	}

}
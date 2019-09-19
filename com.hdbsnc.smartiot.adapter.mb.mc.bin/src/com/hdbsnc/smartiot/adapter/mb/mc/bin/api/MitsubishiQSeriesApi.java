package com.hdbsnc.smartiot.adapter.mb.mc.bin.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.Command;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.SubCommand;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.BatchReadWriteProtocol;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MCProtocolResponseException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;
import com.hdbsnc.smartiot.util.logger.Log;

public class MitsubishiQSeriesApi {

	private Socket _socket;
	private String _ip;
	private int _port;

	private TransMode _transMode;
	private Log _log;
	
	private Object sync = new Object();

	public MitsubishiQSeriesApi(TransMode pmode,Log log) {

		_transMode = pmode;
		_socket = null;
		
		_log=log;
	}

	/**
	 * PLC로 연결할 경우 호출한다.
	 * @param ip
	 * @param ports
	 * @throws Exception
	 * @throws MCProtocolResponseException
	 */
	public void connect(String ip, int port) throws IOException, MCProtocolResponseException {
		//포트를 List에 담을 수있도록 파서 한다.
		_ip = ip;		
		try {
			_socket = new Socket();
			_socket.setKeepAlive(false);
			_socket.setReuseAddress(false);
			_socket.setSoTimeout(5000);
			this._port = port;	
			this._socket.connect(new InetSocketAddress(ip, port), 2000);
			_log.info("MELSEC CONNECTION SUCESS, IP : " + this._ip + " 포트 : "+ this._port);		
		} catch (IOException e) {
			if(e.getMessage().contains("connect timed out")) {
				throw new MCProtocolResponseException("-33002", String.format(e.getMessage()+" : IP(%s), Port(%s)입니다", _ip,_port));
			}
			if(e.getMessage().contains("Connection refused: connect")) {
				throw new MCProtocolResponseException("-33003", String.format(e.getMessage()+" : IP(%s), Port(%s)입니다", _ip,_port));	
			}
			throw e;
		}
	}

	/**
	 * 연결이  해제 될 경우 호출한다.
	 * @throws IOException
	 */
	public void reConnect() throws MCProtocolResponseException, IOException {
		synchronized(sync) {
			try {
				this._socket = new Socket();
				this._socket.setKeepAlive(false);
				this._socket.setReuseAddress(false);
				this._socket.setSoTimeout(5000);
				this._socket.connect(new InetSocketAddress(_ip, _port), 2000);
				_log.info("MELSEC CONNECTION SUCESS, IP : " + this._ip + " 포트 : "+ this._port);		
			} catch (IOException e) {
				if(e.getMessage().contains("connect timed out")) {
					throw new MCProtocolResponseException("-33002", String.format(e.getMessage()+" : IP(%s), Port(%s)입니다", _ip,_port));
				}
				if(e.getMessage().contains("Connection refused: connect")) {
					throw new MCProtocolResponseException("-33003", String.format(e.getMessage()+" : IP(%s), Port(%s)입니다", _ip,_port));	
				}
				throw e;
			}
		}
	}
	
	/**
	 * 현재 연결되 있는 PLC와의 접속을 해제한다.
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		synchronized(sync) {
			if (this._socket != null) {
				this._socket.close();
				this._socket=null;
			}
		}
	}

	/**
	 * 현재 연결이 유효한지 확인한다.
	 * @return
	 */
	public boolean isConnected() {
		if (this._socket != null) {
			if (this._socket.isConnected() && !this._socket.isClosed())
				return true;
		}
		return false;
	}


	/**
	 * Request 데이터를 PLC로 전송한다.
	 * @param reqData
	 * @return
	 * @throws MCProtocolResponseException
	 * @throws IOException
	 */
	private byte[] sendData(byte[] reqData) throws MCProtocolResponseException, IOException {
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		byte[] result;
	
		if (_socket == null) {
			throw new MCProtocolResponseException("-33004", "사용자 명령에 의해 중지된 Event입니다");
		}

		try {
			out = new ByteArrayOutputStream();
			in = new BufferedInputStream(this._socket.getInputStream());

			byte[] buffer = new byte[4096];

			this._socket.getOutputStream().write(reqData);

			out.reset();

			int cnt = 0;
			while ((cnt = in.read(buffer)) > 0) {
				out.write(buffer, 0, cnt);
				if (in.available() < 1)
					break;
			}
			result = out.toByteArray();
		} catch (IOException ex) {
			throw ex;
		}

		return result;
	}

	/**
	 * PLC로 부터 일괄 읽기 프로토콜을 통해 데이터를 읽는다.
	 * @param devCode - 디바이스 코드
	 * @param devNum - 디바이스 메모리번지
	 * @param devScore - 읽을 갯수 (1score = 2byte)
	 * @return
	 * @throws IOException - 외부 에러 처리
	 * @throws MCProtocolResponseException  - 외부 에러 처리
	 * @throws ApplicationException  - 외부 에러 처리
	 */
	public synchronized String read(String devCode, String devNum, String devScore) throws IOException, ApplicationException, MCProtocolResponseException, Exception {
		 
		//프레임의 전송방식 및 쓰기 읽기 선언 및 워드읽기 형식
		AbstractBlocksFrame frame = new BatchReadWriteProtocol(_transMode, Command.BATCH_READ, SubCommand.WORD);

		frame.addReadRequest(devCode, devNum, devScore);

		ByteBuffer reqData = frame.getRequestPacket();
		_log.trace("[REQ] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		_log.trace("[RES] : "+ EditUtil.bytesToHexStr(resData));
		frame.setResponsePacket(resData);

		// 만약 에러코드가 날아오면 Exception 처리
		if (!(frame.getResponseCode().equals("0000"))) {
			throw new MCProtocolResponseException("-33000", String.format("PLC로부터 에러가 발생하였습니다(%s)", frame.getResponseData()));
		} else {
			// 성공적이라면 데이터 부만 받아서 리턴
			return frame.getResponseData();
		}
	}

	public String getIp() {
		return _ip;
	}

	public int getPort() {
		return _port;
	}
	
}
package com.hdbsnc.smartiot.adapter.mb.mc.bin.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.Command;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.SubCommand;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.BatchReadWriteProtocol;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.MultipleBlockBatchReadWriteProtocol;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MitsubishiQSeriesMCCompleteException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.DeleteJsonVo;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.WritePlcVo;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;
import com.hdbsnc.smartiot.util.logger.Log;

public class MitsubishiQSeriesApi {

	private Socket _socket;
	private String _ip;
	private int _port;

	private TransMode _transMode;
	private Log _log;

	public MitsubishiQSeriesApi(TransMode pmode,Log log) {

		_transMode = pmode;
		_socket = null;
		
		_log=log;
	}

	/**
	 * PLC로 연결할 경우 호출한다.
	 * @param ip
	 * @param ports
	 * @throws IOException
	 */
	public synchronized void connect(String ip, int port) throws IOException {
		//포트를 List에 담을 수있도록 파서 한다.
		
		
		_ip = ip;		
		_socket = new Socket();
		_socket.setKeepAlive(false);
		_socket.setReuseAddress(false);
		_socket.setSoTimeout(5000);
		this._port = port;	
		
		try {
			this._socket.connect(new InetSocketAddress(ip, port), 2000);
			_log.info("MELSEC CONNECTION SUCESS, IP : " + this._ip + " 포트 : "+ this._port);		
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 연결이  해제 될 경우 호출한다.
	 * @throws IOException
	 */
	public synchronized void reConnect() throws IOException {
		this._socket = new Socket();
		this._socket.setKeepAlive(false);
		this._socket.setReuseAddress(false);
		this._socket.setSoTimeout(5000);

		try {
			this._socket.connect(new InetSocketAddress(_ip, _port), 2000);
			_log.info("MELSEC CONNECTION SUCESS, IP : " + this._ip + " 포트 : "+ this._port);		
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 현재 연결되 있는 PLC와의 접속을 해제한다.
	 * @throws IOException
	 */
	public synchronized void disconnect() throws IOException {
		if (this._socket != null) {
			this._socket.close();
			this._socket=null;
		}
	}

	/**
	 * 현재 연결이 유효한지 확인한다.
	 * @return
	 */
	public synchronized boolean isConnected() {
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
	 * @throws IOException
	 */
	private synchronized byte[] sendData(byte[] reqData) throws IOException {
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		byte[] result;

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

	public void multipleWrite(List<WritePlcVo> writeObjList) throws Exception {

		AbstractBlocksFrame frame = new MultipleBlockBatchReadWriteProtocol(_transMode, Command.MULTIPLE_BLCOK_WRITE, SubCommand.WORD);

		// TODO DATATYPE에 대해 아직 처리하지 않았음
		String devCode, devNum, devScore, dataType, data;
		for (int i = 0; i < writeObjList.size(); i++) {
			devCode = writeObjList.get(i).getDevCode();
			devNum = writeObjList.get(i).getDevNum();
			devScore = writeObjList.get(i).getDevScore();
			data = writeObjList.get(i).getData();
			dataType = writeObjList.get(i).getDataType(); 

			frame.addWriteRequest(devCode, devNum, devScore, dataType, data);
		}


		ByteBuffer reqData = frame.getRequestPacket();
		_log.trace("[WRITE REQ 프로토콜] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		frame.setResponsePacket(resData);
		_log.trace("[WRITE RES 프로토콜] : "+EditUtil.bytesToHexStr(resData));

		// 만약 에러코드가 날아오면 Exception 처리
		if (!(frame.getResponseCode().equals("0000"))) {
			throw new MitsubishiQSeriesMCCompleteException(frame.getResponseCode(), frame.getResponseData());
		} else {
			// 성공적이라면 데이터 부가 없음
			return;
		}
	}

	/**
	 * PLC로 부터 데이터를 읽음
	 * 
	 * @param readObjList
	 * @return
	 * @throws Exception
	 */
	public String multipleRead(List<DeleteJsonVo> readObjList) throws Exception {
				
		 //프레임의 전송방식 및 쓰기 읽기 선언 및 워드읽기 형식
		AbstractBlocksFrame frame = new MultipleBlockBatchReadWriteProtocol(_transMode, Command.MULTIPLE_BLCOK_READ, SubCommand.WORD);

		// 블럭의 데이터를 frame에 추가함
		String devCode, devNum, devScore;
		for (int i = 0; i < readObjList.size(); i++) {
			devCode = readObjList.get(i).getDevCode();
			devNum = readObjList.get(i).getDevNum();
			devScore = readObjList.get(i).getDevScore();

			frame.addReadRequest(devCode, devNum, devScore);
		}

		ByteBuffer reqData = frame.getRequestPacket();
		_log.trace("[REQ] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		_log.trace("[RES] : "+ EditUtil.bytesToHexStr(resData));
		frame.setResponsePacket(resData);

		// 만약 에러코드가 날아오면 Exception 처리
		if (!(frame.getResponseCode().equals("0000"))) {
			throw new MitsubishiQSeriesMCCompleteException(frame.getResponseCode(), frame.getResponseData());
		} else {
			// 성공적이라면 데이터 부만 받아서 리턴
			return frame.getResponseData();
		}
	}

	public void write(WritePlcVo writeObj) throws Exception {

		AbstractBlocksFrame frame = new BatchReadWriteProtocol(_transMode, Command.BATCH_WRITE, SubCommand.WORD);

		// TODO DATATYPE에 대해 아직 처리하지 않았음
		String devCode = writeObj.getDevCode();
		String devNum = writeObj.getDevNum();
		String devScore = writeObj.getDevScore();
		String data = writeObj.getData();
		String dataType = writeObj.getDataType(); 

		frame.addWriteRequest(devCode, devNum, devScore, dataType, data);


		ByteBuffer reqData = frame.getRequestPacket();
		_log.trace("[WRITE REQ 프로토콜] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		frame.setResponsePacket(resData);
		_log.trace("[WRITE RES 프로토콜] : "+EditUtil.bytesToHexStr(resData));
		
		// 만약 에러코드가 날아오면 Exception 처리
		if (!(frame.getResponseCode().equals("0000"))) {
			throw new MitsubishiQSeriesMCCompleteException(frame.getResponseCode(), frame.getResponseData());
		} else {
			// 성공적이라면 데이터 부가 없음
			return;
		}
	}

	/**
	 * PLC로 부터 데이터를 읽음
	 * 
	 * @param readObj
	 * @return
	 * @throws Exception
	 */
	public String read(DeleteJsonVo readObj) throws Exception {
		 
		//프레임의 전송방식 및 쓰기 읽기 선언 및 워드읽기 형식
		AbstractBlocksFrame frame = new BatchReadWriteProtocol(_transMode, Command.BATCH_READ, SubCommand.WORD);

		// 블럭의 데이터를 frame에 추가함
		String devCode = readObj.getDevCode();
		String devNum = readObj.getDevNum();
		String devScore = readObj.getDevScore();

		frame.addReadRequest(devCode, devNum, devScore);

		ByteBuffer reqData = frame.getRequestPacket();
		_log.trace("[REQ] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		_log.trace("[RES] : "+ EditUtil.bytesToHexStr(resData));
		frame.setResponsePacket(resData);

		// 만약 에러코드가 날아오면 Exception 처리
		if (!(frame.getResponseCode().equals("0000"))) {
			throw new MitsubishiQSeriesMCCompleteException(frame.getResponseCode(), frame.getResponseData());
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
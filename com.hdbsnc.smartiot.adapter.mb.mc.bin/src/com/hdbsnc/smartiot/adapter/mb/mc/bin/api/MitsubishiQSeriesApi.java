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
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestReadObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.obj.RequestWriteObj;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;
import com.hdbsnc.smartiot.util.logger.Log;

public class MitsubishiQSeriesApi {

	private Socket socket;
	private String ip;
	private int port;

	private List<Integer> portArray;
	private int curtPortIndex;

	private TransMode transMode;
	private Log log;

	public MitsubishiQSeriesApi(TransMode pmode,Log log) {

		this.transMode = pmode;
		this.socket = null;
		
		this.log=log;
	}

	/**
	 * PLC로 연결할 경우 호출한다.
	 * @param ip
	 * @param ports
	 * @throws IOException
	 */
	public synchronized void connect(String ip, String ports) throws IOException {
		//포트를 List에 담을 수있도록 파서 한다.
		
		if(portArray == null) {
			this.portArray = portsParser(ports);
		}
		
		this.ip = ip;		
		this.socket = new Socket();
		this.socket.setKeepAlive(false);
		this.socket.setReuseAddress(false);
		this.socket.setSoTimeout(5000);
		//사용할 포트를 가지고 온다.
		this.port = getPort();
		log.info("MELSEC CONNECTION IP : " + this.ip + " 포트 : "+ this.port);			
		
		try {
			this.socket.connect(new InetSocketAddress(ip, port), 2000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 포트를 변경한다.
	 * @return
	 */
	private int getPort() {
		
		int iResult = portArray.get(curtPortIndex);
		
		//포트를 끝까지 다 한번 씩 사용했다면 다시 0번 Index로 초기화한다.
		if(curtPortIndex == portArray.size()-1) {
			curtPortIndex = 0;
		}else{
			curtPortIndex++;
		}		
		
		return iResult;
	}
	
	/**
	 * 연결이  해제 될 경우 호출한다.
	 * @throws IOException
	 */
	public synchronized void reConnect() throws IOException {
		this.socket = new Socket();
		this.socket.setKeepAlive(false);
		this.socket.setReuseAddress(false);
		this.socket.setSoTimeout(5000);
		this.port = getPort();

		try {
			log.info("MELSEC CONNECTION IP : " + this.ip + " 포트 : "+ this.port);			
			this.socket.connect(new InetSocketAddress(ip, port), 2000);
		} catch (Exception e) {
			//에러 발생시 사용할 포트를 다시 가지고 온다.
			throw e;
		}
	}
	
	/**
	 * SAMPLE 8193,8194,8195,8196,8197
	 * 포트를 Parser하여 리스트 형태로 반환한다.
	 * @param ports
	 * @return
	 */
	private List<Integer> portsParser(String ports) {
		
		List<Integer> iResult = new ArrayList<Integer>();
	
		String[] tempPortArray = ports.split(","); 
		
		for(int i=0; i<tempPortArray.length; i++) {
			iResult.add(Integer.parseInt(tempPortArray[i]));
		}
		
		return iResult;
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
			in = new BufferedInputStream(this.socket.getInputStream());

			byte[] buffer = new byte[4096];

			this.socket.getOutputStream().write(reqData);
			
			
			//TODO 이쪽 부분 PLC오면 테스트 해보기
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

	public void multipleWrite(List<RequestWriteObj> writeObjList) throws Exception {

		AbstractBlocksFrame frame = new MultipleBlockBatchReadWriteProtocol(transMode, Command.MULTIPLE_BLCOK_WRITE, SubCommand.WORD);

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
		log.trace("[WRITE REQ 프로토콜] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		frame.setResponsePacket(resData);
		log.trace("[WRITE RES 프로토콜] : "+EditUtil.bytesToHexStr(resData));

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
	public String multipleRead(List<RequestReadObj> readObjList) throws Exception {
		//f1 RowData
//		return "0000010000000000000029FC06F5FC050000E600544D3132303646343030000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000303646343030000000000000000000000000000000000000000000000000000000000000E002E302E002E002E302E3020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000A0C00000A040000000000035AF470041AF470001A4470010A447000034C20080A3470086A3470080A347008CA347808CA347009100000000000000000000000000000000000000000000000000000000000000000000000000000000E3FFFFFFE7FFFFFFE6FFFFFFE4FF0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009F0202048F407A545555C5091B2020020B02AA02290000052800000525000005260000052600000526000005C941A41485959555452905940155045599C5E91588A51E51A6B5AD48157D15050000000000000000000000000000000000000000000000008121A215C515FF5502A00000000000009000060801026722011F011F011F011F011F011F011F011F0837080400000000000555C600060006000605C0000A80000A550600060006000605000005000005151500006209100500000000000000000000000000000000000000000000000000000040000000400000004041A000000000000841A000000000000841A000000000000841A0000000000008009D000000000008009D00000000000800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006020000000000000000000000000008832408004F01C01519C001C0000000903333331500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000200020202001200800200000200000A0208100000082000000000000000000000000000000000003000110306001000010401040104010401040104010401040280000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000008A809080108090801080A0802080A080200200020000000000000000000000000080800200000000008080030000000000808001000000000080800100000000000716000000000000031600000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001020000000000000000000000000000
				
		 //프레임의 전송방식 및 쓰기 읽기 선언 및 워드읽기 형식
		AbstractBlocksFrame frame = new MultipleBlockBatchReadWriteProtocol(transMode, Command.MULTIPLE_BLCOK_READ, SubCommand.WORD);

		// 블럭의 데이터를 frame에 추가함
		String devCode, devNum, devScore;
		for (int i = 0; i < readObjList.size(); i++) {
			devCode = readObjList.get(i).getDevCode();
			devNum = readObjList.get(i).getDevNum();
			devScore = readObjList.get(i).getDevScore();

			frame.addReadRequest(devCode, devNum, devScore);
		}

		ByteBuffer reqData = frame.getRequestPacket();
		log.trace("[REQ] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		log.trace("[RES] : "+ EditUtil.bytesToHexStr(resData));
		frame.setResponsePacket(resData);

		// 만약 에러코드가 날아오면 Exception 처리
		if (!(frame.getResponseCode().equals("0000"))) {
			throw new MitsubishiQSeriesMCCompleteException(frame.getResponseCode(), frame.getResponseData());
		} else {
			// 성공적이라면 데이터 부만 받아서 리턴
			return frame.getResponseData();
		}
	}

	public void write(RequestWriteObj writeObj) throws Exception {

		AbstractBlocksFrame frame = new BatchReadWriteProtocol(transMode, Command.BATCH_WRITE, SubCommand.WORD);

		// TODO DATATYPE에 대해 아직 처리하지 않았음
		String devCode = writeObj.getDevCode();
		String devNum = writeObj.getDevNum();
		String devScore = writeObj.getDevScore();
		String data = writeObj.getData();
		String dataType = writeObj.getDataType(); 

		frame.addWriteRequest(devCode, devNum, devScore, dataType, data);


		ByteBuffer reqData = frame.getRequestPacket();
		log.trace("[WRITE REQ 프로토콜] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		frame.setResponsePacket(resData);
		log.trace("[WRITE RES 프로토콜] : "+EditUtil.bytesToHexStr(resData));
		
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
	public String read(RequestReadObj readObj) throws Exception {
		//f1 RowData
//		return "0000010000000000000029FC06F5FC050000E600544D3132303646343030000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000303646343030000000000000000000000000000000000000000000000000000000000000E002E302E002E002E302E3020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000A0C00000A040000000000035AF470041AF470001A4470010A447000034C20080A3470086A3470080A347008CA347808CA347009100000000000000000000000000000000000000000000000000000000000000000000000000000000E3FFFFFFE7FFFFFFE6FFFFFFE4FF0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009F0202048F407A545555C5091B2020020B02AA02290000052800000525000005260000052600000526000005C941A41485959555452905940155045599C5E91588A51E51A6B5AD48157D15050000000000000000000000000000000000000000000000008121A215C515FF5502A00000000000009000060801026722011F011F011F011F011F011F011F011F0837080400000000000555C600060006000605C0000A80000A550600060006000605000005000005151500006209100500000000000000000000000000000000000000000000000000000040000000400000004041A000000000000841A000000000000841A000000000000841A0000000000008009D000000000008009D00000000000800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006020000000000000000000000000008832408004F01C01519C001C0000000903333331500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000200020202001200800200000200000A0208100000082000000000000000000000000000000000003000110306001000010401040104010401040104010401040280000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000008A809080108090801080A0802080A080200200020000000000000000000000000080800200000000008080030000000000808001000000000080800100000000000716000000000000031600000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001020000000000000000000000000000
				
		 //프레임의 전송방식 및 쓰기 읽기 선언 및 워드읽기 형식
		AbstractBlocksFrame frame = new BatchReadWriteProtocol(transMode, Command.BATCH_READ, SubCommand.WORD);

		// 블럭의 데이터를 frame에 추가함
		String devCode = readObj.getDevCode();
		String devNum = readObj.getDevNum();
		String devScore = readObj.getDevScore();

		frame.addReadRequest(devCode, devNum, devScore);

		ByteBuffer reqData = frame.getRequestPacket();
		log.trace("[REQ] : "+EditUtil.bytesToHexStr(reqData.array()));
		
		// plc 요청 프로토콜 전송
		byte[] resData = sendData(reqData.array());
		log.trace("[RES] : "+ EditUtil.bytesToHexStr(resData));
		frame.setResponsePacket(resData);

		// 만약 에러코드가 날아오면 Exception 처리
		if (!(frame.getResponseCode().equals("0000"))) {
			throw new MitsubishiQSeriesMCCompleteException(frame.getResponseCode(), frame.getResponseData());
		} else {
			// 성공적이라면 데이터 부만 받아서 리턴
			return frame.getResponseData();
		}
	}
}
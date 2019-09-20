package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.ApplicationException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MCProtocolResponseException;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;

public class BatchReadWriteProtocol extends AbstractBlocksFrame {
	
	private static final int HEADER_END_POINT = 23;
	private static final int MAX_SCORE = 960;

	/** 커맨드 **/
	private byte[] bCommand = null;
	/** 서브 커맨드 **/
	private byte[] bSubCommand = null;
	
	private RequestWriteDataObj _requestWriteData;
	private RequestReadDataObj _requestReadData;
	
	private QHeader header = null;

	private String _packet;
	
	private int _score;
	
	public BatchReadWriteProtocol(TransMode pMode, Command pCmd, SubCommand pSubCmd) {
		super(pMode, pCmd, pSubCmd);

		this.header = new QHeader();
		this.bCommand = pCmd.getBytes();
		this.bSubCommand = pSubCmd.getBytes();
	}

	/**
	 * 요청 패킷 가져오기
	 */
	@Override
	public ByteBuffer getRequestPacket() throws Exception {
		// 헤더 + 커멘드 + 서브 커멘드 + WORD BLOCK 사이즈 + BIT BLOCK 사이즈 + READ or WRITE
		byte[] cmd = getCommandBytes();
		byte[] subCmd = getSubCommandBytes();

		//요청데이터부의 데이터 바이트 배열로 가공
		byte[] data = getDataBytes();
		byte[] head = header.getByte(getTransMode(), getBodySize(cmd, subCmd, data));

		byte[] result = EditUtil.byteCopy(head, cmd, subCmd, data);
		return ByteBuffer.wrap(result);
	}

	@Override
	public void setResponsePacket(byte[] val) throws  MCProtocolResponseException, Exception {

		String sPacket = null;
		//프로토콜에 포함된 응답데이터길이
		int resDataLength;
		//프로토콜의 데이터 길이
		int dataLength;
 		byte bLength[] = {0x00, 0x00};
 	// 바이너리 모드면 HexString
		if (getTransMode() == TransMode.BINARY) {

			//응답데이터 길이를 가지고 옴(LittleEndian 이기 때문에 순서 뒤집기)
			bLength[0] = val[8];
			bLength[1] = val[7];
			resDataLength = EditUtil.bytesToShort(bLength);
			//프로토콜전체길이에서 해더 부분 제거 
			dataLength = val.length-9; 
			
			//응답받은 프로토콜길이와 실제 프로토콜 길이가 같은지 확인
			if(dataLength != resDataLength) {
				throw new MCProtocolResponseException("-33010", String.format("Request에 대한 데이터 길이(%s)와 Response의 데이터 길이(%s)가 올바르지 않습니다", resDataLength, dataLength));
			}

			sPacket = EditUtil.bytesToHexStr(val);
			
		} else {
			//TODO 위와 같은 길이에 대한 처리를 해야함 !!! 에러 처리.
			// 아스키 모드면 new String
			sPacket = new String(val);
		}
		
		//응답데이터 길이 및 종료 코드 획득 후 변수에 저장 필요

		_packet = sPacket;
	}

	/**
	 * 수신 패킷 가져오기
	 */
	@Override
	public String getResponsePacket() {
		return _packet;
	}
	

	/**
	 * 결과 코드 반환
	 * @throws Exception 
	 */
	@Override
	public String getResponseCode() {
		return _packet.substring(18, 22);
	}

	/**
	 * 데이터 부분 반환
	 */
	@Override
	public String getResponseData() {
		return _packet.substring(HEADER_END_POINT - 1, _packet.length());
	}

	@Override
	public void addReadRequest(String code, String num, String score) throws ApplicationException {
		if(_requestReadData != null)
			throw new ApplicationException("일괄읽기에서는 한개의 블록 설정만 가능합니다");
		if (getCommand() != Command.BATCH_READ)
			throw new ApplicationException("읽기 요구를 할수 없는 명령어 입니다");
		if (num.length() > 6)
			throw new ApplicationException("-33006", String.format("잘못된 형식의 디바이스 번호(%s)입니다", num));
		if (score.length() > 4)
			throw new ApplicationException("-33007", String.format("잘못된 형식의 스코어(%s) 입니다", score));
		
		_score = Integer.parseInt(score);
		
		if (_score >= MAX_SCORE) {
			throw new ApplicationException("-33008", String.format("최대스코어(%s)를 초과 하였습니다", score));
		}
		
		_requestReadData = new RequestReadDataObj(code, num, score);
	}

	public void addWriteRequest(String code, String num, String score, String dataType, String data) throws ApplicationException {
//		if(_requestWriteData != null)
//			throw new ApplicationException("일괄읽기에서는 한개의 블록 설정만 가능합니다.");
//		if (getCommand() != Command.BATCH_WRITE)
//			throw new ApplicationException("쓰기 요구를 할수 없는 명령어 입니다.");
//		if (num.length() > 6)
//			throw new ApplicationException("잘못된 형식의 디바이스 번호입니다.");
//		if (score.length() > 4)
//			throw new ApplicationException("잘못된 형식의 스코어 입니다.");
//		//데이터 타입이 유효한지 체크
//		//ASCII, SHORT, HEX
//		if(!checkDataType(dataType)) {
//			throw new ApplicationException("지원하지 않는 DataType입니다.");
//		}
//		
//		int iScore = Integer.parseInt(score);
//		//HEX라면 0000의 4자리가 들어오고 ASCII라면 2자리가 들어고기 때문에 정확한 자릿수가 들어왔는지 예외처리
//		if ((dataType.equals("HEX")&&iScore != data.length() / 4)||(dataType.equals("ASCII") && iScore != data.length() / 2))
//			throw new ApplicationException("스코어에 해당하는 쓰기 데이터 길이가 불일치 합니다.");
//
//		_score = Integer.parseInt(score);
//		
//		if (_score >= MAX_SCORE) {
//			throw new ApplicationException("최대스코어를 초과 하였습니다.");
//		}
//
//
//		_requestWriteData = new RequestWriteDataObj(code, num, score, dataType, data);
	}

	/**
	 * command 데이터를 배열로 리턴하여 가지고 온다.
	 * 
	 * @return
	 * @throws Exception
	 */
	private byte[] getCommandBytes() throws Exception {
		byte[] result = null;
		if (getTransMode() == TransMode.ASCII) {
			result = EditUtil.bytesToHexStr(this.bCommand).getBytes();
		} else {
			result = EditUtil.byte4ToBigEndianBytes(this.bCommand);

		}
		return result;
	}


	/**
	 * subCmd 데이터를 배열로 리턴하여 가지고 온다.
	 * 
	 * @return
	 * @throws Exception
	 */
	private byte[] getSubCommandBytes() throws Exception {
		byte[] result = null;
		if (getTransMode() == TransMode.ASCII) {
			result = EditUtil.bytesToHexStr(this.bSubCommand).getBytes();

		} else {
			result = EditUtil.byte4ToBigEndianBytes(this.bSubCommand);
		}
		return result;
	}
	
	/**
	 * 블럭 데이터를 바이트 배열로 리턴하여 가지고 온다.
	 * 
	 * @return
	 * @throws Exception
	 * @throws ApplicationException
	 */
	private byte[] getDataBytes() throws ApplicationException, Exception {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] dataByte;

		if (getCommand() == Command.BATCH_READ) {
//			baos.write(_requestReadData.getBytes(getTransMode()));
			dataByte = _requestReadData.getBytes(getTransMode());
		} else if(getCommand() == Command.BATCH_WRITE){ // Write 명령
//			baos.write(_requestWriteData.getBytes(getTransMode()));
			dataByte = _requestWriteData.getBytes(getTransMode());
		} else {
			throw new ApplicationException("올바르지 않은 커맨드 입니다");
		}
		return dataByte;
	}

	/**
	 * 모든 길이의 합을 가지고 온다.
	 * 
	 * @param values
	 * @return
	 */
	private short getBodySize(byte[]... values) {
		short result = 0;
		for (byte[] value : values)
			result += value.length;

		return result;
	}

	/**
	 * 데이터 타입이 ASCII, HEX, SHORT 가 맞는지 체크 합니다.
	 * @param dataType
	 * @return
	 */
	private boolean checkDataType(String dataType) {
		Pattern p1 = Pattern.compile("ASCII|SHORT|HEX");

		Matcher m1 = p1.matcher(dataType);
		if (!m1.find()) {
			return false;
		}
		return true;
	}

	/**
	 * 읽기 요청 데이터
	 * 
	 */
	class RequestReadDataObj {
		private String sDeviceCode = null;
		private String sDeviceNum = null;
		private String sDeviceScore = null;

		public RequestReadDataObj(String code, String num, String score) {
			this.sDeviceCode = code;
			this.sDeviceNum = num;
			this.sDeviceScore = score;
		}

		public byte[] getBytes(TransMode mode) throws ApplicationException, Exception {

			byte[] code = null;
			byte[] deviceNum = null;
			byte[] score = null;

			DeviceCodeDefine def = getDeviceCode(this.sDeviceCode);
			if(def == null) {
				throw new ApplicationException("-33009", String.format("올바르지 않는 디바이스 코드(%s)입니다", this.sDeviceCode));
			}
			
			if (mode == TransMode.BINARY) {
				code = new byte[] { def.getCodeByte() };
				score = EditUtil.hexStr4ToBigEndianBytes(EditUtil.fillZero(EditUtil.decStrToHexStr(sDeviceScore), 4));
				if ("HEX".equals(def.getConverter())) {
					deviceNum = EditUtil.hexStr6ToBigEndianBytes(EditUtil.fillZero(sDeviceNum, 6));
				} else { // DEC
					deviceNum = EditUtil.hexStr6ToBigEndianBytes(EditUtil.fillZero(EditUtil.decStrToHexStr(sDeviceNum), 6));
				}
				return EditUtil.byteCopy(deviceNum, code, score);
			} else { // ASCII모드
				code = def.getCodeString().getBytes();
				deviceNum = EditUtil.fillZero(sDeviceNum, 6).getBytes();
				score = EditUtil.fillZero(EditUtil.decStrToHexStr(sDeviceScore), 4).getBytes();

				return EditUtil.byteCopy(code, deviceNum, score);
			}

		}

	}

	/**
	 * 쓰기 요청 데이터
	 * 
	 */
	private class RequestWriteDataObj {
		private String sDeviceCode = null;
		private String sDeviceNum = null;
		private String sDeviceScore = null;
		private String sDataType = null;

		private String sWriteData = null;

		public RequestWriteDataObj(String code, String num, String score, String dataType, String data) {
			this.sDeviceCode = code;
			this.sDeviceNum = num;
			this.sDeviceScore = score;
			this.sWriteData = data;
			this.sDataType = dataType;
		}

		public byte[] getBytes(TransMode mode) throws Exception {

			byte[] code = null;
			byte[] deviceNum = null;
			byte[] score = null;
			byte[] data = null;

			DeviceCodeDefine def = getDeviceCode(this.sDeviceCode);
			if (mode == TransMode.BINARY) {
				code = new byte[] { def.getCodeByte() };
				score = EditUtil.hexStr4ToBigEndianBytes(EditUtil.fillZero(EditUtil.decStrToHexStr(sDeviceScore), 4));
				if ("HEX".equals(def.getConverter())) {
					deviceNum = EditUtil.hexStr6ToBigEndianBytes(EditUtil.fillZero(sDeviceNum, 6));
				} else { // DEC
					deviceNum = EditUtil
							.hexStr6ToBigEndianBytes(EditUtil.fillZero(EditUtil.decStrToHexStr(sDeviceNum), 6));
				}

				data = EditUtil.byte4ToBigEndianBytes(DatatypeConverter.parseHexBinary(sWriteData));
				return EditUtil.byteCopy(deviceNum, code, score, data);
			} else { // ASCII모드
				code = def.getCodeString().getBytes();
				deviceNum = EditUtil.fillZero(sDeviceNum, 6).getBytes();
				score = EditUtil.fillZero(EditUtil.decStrToHexStr(sDeviceScore), 4).getBytes();
				data = sWriteData.getBytes();

				return EditUtil.byteCopy(code, deviceNum, score, data);
			}

		}
	}
}

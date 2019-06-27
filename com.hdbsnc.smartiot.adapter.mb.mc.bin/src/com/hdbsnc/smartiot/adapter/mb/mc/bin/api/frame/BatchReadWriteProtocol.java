package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception.MCProtocolException;
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
	public void setResponsePacket(byte[] val) throws Exception {

		String sPacket = null;
		if (getTransMode() == TransMode.BINARY) {
			// 바이너리 모드면 HexString
			sPacket = EditUtil.bytesToHexStr(val);
		} else {
			// 아스키 모드면 new String
			sPacket = new String(val);
		}

		_packet = sPacket;
	}

	/**
	 * 수신 패킷 가져오기
	 */
	@Override
	public String getResponsePacket() throws Exception {
		return _packet;
	}
	

	/**
	 * 결과 코드 반환
	 * @throws Exception 
	 */
	@Override
	public String getResponseCode() throws Exception {
		return _packet.substring(18, 22);
	}

	/**
	 * 데이터 부분 반환
	 */
	@Override
	public String getResponseData() throws Exception {
		return _packet.substring(HEADER_END_POINT - 1, _packet.length());
	}

	@Override
	public void addReadRequest(String code, String num, String score) throws MCProtocolException {
		if(_requestReadData != null)
			throw new MCProtocolException("일괄읽기에서는 한개의 블록 설정만 가능합니다.");
		if (getCommand() != Command.BATCH_READ)
			throw new MCProtocolException("읽기 요구를 할수 없는 명령어 입니다.");
		if (num.length() > 6)
			throw new MCProtocolException("잘못된 형식의 디바이스 번호입니다.");
		if (score.length() > 4)
			throw new MCProtocolException("잘못된 형식의 스코어 입니다.");
		
		_score = Integer.parseInt(score);
		
		if (_score >= MAX_SCORE) {
			throw new MCProtocolException("최대스코어를 초과 하였습니다.");
		}
		
		_requestReadData = new RequestReadDataObj(code, num, score);
	}

	@Override
	public void addWriteRequest(String code, String num, String score, String dataType, String data) throws Exception {
		if(_requestWriteData != null)
			throw new MCProtocolException("일괄읽기에서는 한개의 블록 설정만 가능합니다.");
		if (getCommand() != Command.BATCH_WRITE)
			throw new MCProtocolException("쓰기 요구를 할수 없는 명령어 입니다.");
		if (num.length() > 6)
			throw new MCProtocolException("잘못된 형식의 디바이스 번호입니다.");
		if (score.length() > 4)
			throw new MCProtocolException("잘못된 형식의 스코어 입니다.");
		//데이터 타입이 유효한지 체크
		//ASCII, SHORT, HEX
		if(!checkDataType(dataType)) {
			throw new MCProtocolException("지원하지 않는 DataType입니다.");
		}
		
		int iScore = Integer.parseInt(score);
		//HEX라면 0000의 4자리가 들어오고 ASCII라면 2자리가 들어고기 때문에 정확한 자릿수가 들어왔는지 예외처리
		if ((dataType.equals("HEX")&&iScore != data.length() / 4)||(dataType.equals("ASCII") && iScore != data.length() / 2))
			throw new MCProtocolException("스코어에 해당하는 쓰기 데이터 길이가 불일치 합니다.");

		_score = Integer.parseInt(score);
		
		if (_score >= MAX_SCORE) {
			throw new MCProtocolException("최대스코어를 초과 하였습니다.");
		}


		_requestWriteData = new RequestWriteDataObj(code, num, score, dataType, data);
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
	 */
	private byte[] getDataBytes() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		if (getCommand() == Command.BATCH_READ) {
			baos.write(_requestReadData.getBytes(getTransMode()));
		} else if(getCommand() == Command.BATCH_WRITE){ // Write 명령
			baos.write(_requestWriteData.getBytes(getTransMode()));
		} else {
			throw new MCProtocolException("올바르지 않은 커맨드 입니다. 커맨드를 한번더 확인해주세요");
		}
		return baos.toByteArray();
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

		public byte[] getBytes(TransMode mode) throws Exception {

			byte[] code = null;
			byte[] deviceNum = null;
			byte[] score = null;

			DeviceCodeDefine def = getDeviceCode(this.sDeviceCode);
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

package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.InvalidSearchControlsException;
import javax.xml.bind.DatatypeConverter;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;

public class MultipleBlockBatchReadWriteProtocol extends AbstractBlocksFrame {

	private static final int HEADER_END_POINT = 23;

	private final int MAX_BLOCK_NUM = 120;
	private final int MAX_SCORE = 960;

	/** 커맨드 **/
	private byte[] bCommand = null;
	/** 서브 커맨드 **/
	private byte[] bSubCommand = null;
	/** 워드 디바이스 블록수 **/
	private byte bWordDeviceBlockNum = 0;
	/** 비트 디바이스 블록수 **/
	private byte bBitDeviceBlockNum = 0;

	private int totalScore;
	private int totalBlock;

	private QHeader header = null;
	private List<RequestWriteDataObj> requestWriteData = null;
	private List<RequestReadDataObj> requestReadData = null;

	private String packet;

	public MultipleBlockBatchReadWriteProtocol(TransMode pMode, Command pCmd, SubCommand pSubCmd) {
		super(pMode, pCmd, pSubCmd);

		this.header = new QHeader();
		this.bCommand = pCmd.getBytes();
		this.bSubCommand = pSubCmd.getBytes();
		this.requestWriteData = new ArrayList<>();
		this.requestReadData = new ArrayList<>();
	}

	/**
	 * 요청 패킷 가져오기
	 */
	@Override
	public ByteBuffer getRequestPacket() throws Exception {

		// 헤더 + 커멘드 + 서브 커멘드 + WORD BLOCK 사이즈 + BIT BLOCK 사이즈 + READ or WRITE
		byte[] cmd = getCommandBytes();
		byte[] subCmd = getSubCommandBytes();
		byte[] wordBlock = getWordBlockBytes();
		byte[] bitBlock = getBitBlockBytes();
		byte[] data = getDataBytes();
		byte[] head = this.header.getByte(getTransMode(), getBodySize(cmd, subCmd, wordBlock, bitBlock, data));

		byte[] result = EditUtil.byteCopy(head, cmd, subCmd, wordBlock, bitBlock, data);
		return ByteBuffer.wrap(result);
	}

	/**
	 * 수신 패킷 설정하기
	 */
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

		this.packet = sPacket;
	}

	/**
	 * 수신 패킷 가져오기
	 */
	@Override
	public String getResponsePacket() throws Exception {

		return packet;
	}

	/**
	 * 결과 코드 반환
	 * @throws Exception 
	 */
	@Override
	public String getResponseCode() throws Exception {
		return EditUtil.bytesToHexStr(EditUtil.hexStr4ToBigEndianBytes(this.packet.substring(18, 22)));
	}

	/**
	 * 데이터 부분 반환
	 */
	@Override
	public String getResponseData() {
		return packet.substring(HEADER_END_POINT - 1, packet.length());
	}

	/**
	 * 읽기요청
	 */
	@Override
	public void addReadRequest(String code, String num, String score) throws Exception {
		if (getCommand() != Command.MULTIPLE_BLCOK_READ)
			throw new Exception("읽기 요구를 할수 없는 명령어 입니다.");
		if (num.length() > 6)
			throw new Exception("잘못된 형식의 디바이스 번호입니다.");
		if (score.length() > 4)
			throw new Exception("잘못된 형식의 스코어 입니다.");

		totalScore += Integer.parseInt(score);
		totalBlock++;

		if (totalBlock >= MAX_BLOCK_NUM) {
			throw new Exception("최대블록수를 초과 하였습니다.");
		}
		if (totalScore >= MAX_SCORE) {
			throw new Exception("최대스코어를 초과 하였습니다.");
		}

		this.requestReadData.add(new RequestReadDataObj(code, num, score));
		setRequestCount(code);
	}

	@Override
	public void addWriteRequest(String code, String num, String score, String dataType, String data) throws Exception {
		if (getCommand() != Command.MULTIPLE_BLCOK_WRITE)
			throw new Exception("쓰기 요구를 할수 없는 명령어 입니다.");
		if (num.length() > 6)
			throw new Exception("잘못된 형식의 디바이스 번호입니다.");
		if (score.length() > 4)
			throw new Exception("잘못된 형식의 스코어 입니다.");

		int iScore = Integer.parseInt(score);
		if (iScore != data.length() / 4)
			throw new Exception("스코어에 해당하는 쓰기 데이터 길이가 불일치 합니다.");

		totalScore = totalScore + iScore;
		totalBlock++;
		
		if ((totalBlock*4)+totalScore>=960) {
			throw new Exception("쓸 수 있는 최대 수를 초과 하였습니다.");
		}

		this.requestWriteData.add(new RequestWriteDataObj(code, num, score, data));
		setRequestCount(code);
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
	 * word블럭 데이터를 배열로 리턴하여 가지고 온다.
	 * 
	 * @return
	 * @throws Exception
	 */
	private byte[] getWordBlockBytes() throws Exception {
		byte[] result = null;
		if (getTransMode() == TransMode.ASCII) {
			result = EditUtil.bytesToHexStr(new byte[] { bWordDeviceBlockNum }).getBytes();
		} else {
			result = new byte[] { bWordDeviceBlockNum };
		}
		return result;
	}

	/**
	 * bit블럭 데이터를 배열로 리턴하여 가지고 온다.
	 * 
	 * @return
	 * @throws Exception
	 */
	private byte[] getBitBlockBytes() throws Exception {
		byte[] result = null;
		if (getTransMode() == TransMode.ASCII) {
			result = EditUtil.bytesToHexStr(new byte[] { bBitDeviceBlockNum }).getBytes();
		} else {
			result = new byte[] { bBitDeviceBlockNum };
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

		if (getCommand() == Command.MULTIPLE_BLCOK_READ) {
			for (int i = 0; i < requestReadData.size(); i++) {
				baos.write(requestReadData.get(i).getBytes(getTransMode()));
			}
		} else { // Write 명령
			for (int i = 0; i < requestWriteData.size(); i++) {
				// TODO 수정 해야함
				baos.write(requestWriteData.get(i).getBytes(getTransMode()));
			}
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
	 * 요청 데이터 카운터
	 * 
	 * @param code
	 */
	private void setRequestCount(String code) {
		if (getDeviceCode(code).getCodeType() == DeviceType.BIT) {
			this.bBitDeviceBlockNum++;
		} else {
			this.bWordDeviceBlockNum++;
		}
	}

	/**
	 * 읽기 요청 데이터
	 * 
	 * @author kang
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
	 * @author kang
	 *
	 */
	private class RequestWriteDataObj {
		private String sDeviceCode = null;
		private String sDeviceNum = null;
		private String sDeviceScore = null;

		private String sWriteData = null;

		public RequestWriteDataObj(String code, String num, String score, String data) {
			this.sDeviceCode = code;
			this.sDeviceNum = num;
			this.sDeviceScore = score;
			this.sWriteData = data;
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

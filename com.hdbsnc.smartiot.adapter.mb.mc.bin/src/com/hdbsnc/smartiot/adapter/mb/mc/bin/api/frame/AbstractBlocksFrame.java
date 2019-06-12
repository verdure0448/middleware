package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 복수 읽기/쓰기 프레임
 * 
 * @author kang
 *
 */
public abstract class AbstractBlocksFrame {

	/**
	 * 명령어
	 */
	private static final byte[] MC_COMMAND_MULTIPLE_BLCOK_READ = { 0x04, 0x06 };
	private static final byte[] MC_COMMAND_MULTIPLE_BLCOK_WRITE = { 0x14, 0x06 };
	private static final byte[] MC_COMMAND_BATCH_READ = { 0x04, 0x01 };
	private static final byte[] MC_COMMAND_BATCH_WRITE = { 0x14, 0x01 };
	
	private static final byte[] MC_SUB_COMMAND_WORD = { 0x00, 0x00 };

	private static final String DC_INPUT_RELAY = "X*";
	private static final String DC_OUTPUT_RELAY = "Y*";
	private static final String DC_INNER_RELAY = "M*";
	private static final String DC_LATCH_RELAY = "L*";
	private static final String DC_ANNUNCIATOR = "F*";
	private static final String DC_EDGE_RELAY = "V*";
	private static final String DC_LINK_RELAY = "B*";
	private static final String DC_DATA_REGISTER = "D*";
	private static final String DC_LINK_REGISTER = "W*";
	private static final String DC_FILE_REGISTER = "R*";
	private static final String TIMER_CURRENT_VALUE = "TN";

	private Map<String, DeviceCodeDefine> deviceCodeMap = null;
	private TransMode mode;
	private Command command;
	private SubCommand subCommand;

	public enum Command {
		MULTIPLE_BLCOK_READ(0), MULTIPLE_BLCOK_WRITE(1),BATCH_READ(2), BATCH_WRITE(3);

		private int idx;

		private Command(int index) {
			idx = index;
		}

		public byte[] getBytes() {
			byte[] rtn = null;
			switch (idx) {
			case 0:
				rtn = MC_COMMAND_MULTIPLE_BLCOK_READ;
				break;
			case 1:
				rtn = MC_COMMAND_MULTIPLE_BLCOK_WRITE;
				break;
			case 2:
				rtn = MC_COMMAND_BATCH_READ;
				break;
			case 3:
				rtn = MC_COMMAND_BATCH_WRITE;
				break;
			}
			return rtn;
		}
	};

	public enum SubCommand {
		WORD(0);
		private int idx;

		private SubCommand(int index) {
			idx = index;
		}

		public byte[] getBytes() {
			byte[] rtn = null;
			switch (idx) {
			case 0:
				rtn = MC_SUB_COMMAND_WORD;
				break;
			}
			return rtn;
		}
	};

	public enum TransMode {
		ASCII, BINARY
	};

	public enum DeviceType {
		BIT, WORD;
	};

	/**
	 * 생성자
	 * 
	 * @param pMode
	 * @param pCmd
	 * @param pSubCmd
	 */
	public AbstractBlocksFrame(TransMode pMode, Command pCmd, SubCommand pSubCmd) {
		this.mode = pMode;
		this.command = pCmd;
		this.subCommand = pSubCmd;

		deviceCodeMap = new HashMap<String, DeviceCodeDefine>();
		deviceCodeMap.put(DC_INPUT_RELAY, new DeviceCodeDefine(DC_INPUT_RELAY, (byte) 0x9C, DeviceType.BIT, "HEX"));
		deviceCodeMap.put(DC_OUTPUT_RELAY, new DeviceCodeDefine(DC_OUTPUT_RELAY, (byte) 0x9D, DeviceType.BIT, "HEX"));
		deviceCodeMap.put(DC_INNER_RELAY, new DeviceCodeDefine(DC_INNER_RELAY, (byte) 0x90, DeviceType.BIT, "DEC"));
		deviceCodeMap.put(DC_LATCH_RELAY, new DeviceCodeDefine(DC_LATCH_RELAY, (byte) 0x92, DeviceType.BIT, "DEC"));
		deviceCodeMap.put(DC_ANNUNCIATOR, new DeviceCodeDefine(DC_ANNUNCIATOR, (byte) 0x93, DeviceType.BIT, "DEC"));
		deviceCodeMap.put(DC_EDGE_RELAY, new DeviceCodeDefine(DC_EDGE_RELAY, (byte) 0x94, DeviceType.BIT, "DEC"));
		deviceCodeMap.put(DC_LINK_RELAY, new DeviceCodeDefine(DC_LINK_RELAY, (byte) 0xA0, DeviceType.BIT, "HEX"));
		deviceCodeMap.put(DC_DATA_REGISTER, new DeviceCodeDefine(DC_DATA_REGISTER, (byte) 0xA8, DeviceType.WORD, "DEC"));
		deviceCodeMap.put(DC_LINK_REGISTER, new DeviceCodeDefine(DC_LINK_REGISTER, (byte) 0xB4, DeviceType.WORD, "HEX"));
		deviceCodeMap.put(DC_FILE_REGISTER, new DeviceCodeDefine(DC_FILE_REGISTER, (byte) 0xAF, DeviceType.WORD, "DEC"));
		deviceCodeMap.put(TIMER_CURRENT_VALUE, new DeviceCodeDefine(TIMER_CURRENT_VALUE, (byte) 0xC2, DeviceType.WORD, "DEC"));
		
		
	}

	public TransMode getTransMode() {
		return this.mode;
	}

	public Command getCommand() {
		return this.command;
	}

	public SubCommand getSubCommand() {
		return this.subCommand;
	}

	public DeviceCodeDefine getDeviceCode(String code) {
		return deviceCodeMap.get(code);
	}

	/**
	 * 송신패킷을 구한다.
	 * 
	 * @param mode
	 *            ASCII or BINARY
	 * @return ByteBuffer
	 * @throws Exception
	 */
	public abstract ByteBuffer getRequestPacket() throws Exception;

	/**
	 * 수신패킷을 구한다.
	 * 
	 * @param mode
	 *            ASCII or BINARY
	 * @return ByteBuffer
	 * @throws Exception
	 */
	public abstract String getResponseCode() throws Exception;
	public abstract String getResponseData() throws Exception;
	public abstract String getResponsePacket() throws Exception;

	public abstract void setResponsePacket(byte[] val) throws Exception;

	public abstract void addReadRequest(String code, String num, String score) throws Exception;

	public abstract void addWriteRequest(String code, String num, String score, String dataType, String data) throws Exception;

	class DeviceCodeDefine {

		private String strCode;
		private byte byteCode;
		private DeviceType type;
		private String converter;

		public DeviceCodeDefine(String sCode, byte bCode, DeviceType type, String sConverter) {
			this.strCode = sCode;
			this.byteCode = bCode;
			this.type = type;
			this.converter=sConverter;
		}

		public String getConverter(){
			return this.converter;
		}
		
		public String getCodeString() {
			return this.strCode;
		}

		public byte getCodeByte() {
			return this.byteCode;
		}

		public DeviceType getCodeType() {
			return this.type;
		}

	}
}

package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;

/**
 * @author DBeom
 * 멜섹의 에러코드들이 실제적으로 들어갈 곳
 */
public class MitsubishiQSeriesMCCompleteException extends Exception{

	public final static String LIMIT = "";
	public final static String READ_STARTADDRESS = "C050";
	public final static String READ_RANGEOUT1 = "C051";
	public final static String READ_RANGEOUT2 = "C052";
	public final static String READ_RANGEOUT3 = "C053";
	public final static String READ_RANGEOUT4 = "C054";
	public final static String READ_REQ_EXCEED = "C056";
	public final static String CMD_SCMD_SYNTAXERR = "C059";
	
	
	private String resNetNo;
	private String resPlcNo;
	private String resIoNo;
	private String resModuleNo;
	private String resCmd;
	private String resSubCmd;
	private String errorCode;
	
	public MitsubishiQSeriesMCCompleteException(String errorCode, String responseData) throws Exception {
		super("ErrorCode : "+ errorCode);
		this.errorCode = errorCode;

		resNetNo = responseData.substring(0, 2);
		resPlcNo = responseData.substring(2, 4);
		resIoNo = EditUtil.bytesToHexStr(EditUtil.hexStr4ToBigEndianBytes(responseData.substring(4, 8)));
		resModuleNo = responseData.substring(8, 10);
		resCmd = EditUtil.bytesToHexStr(EditUtil.hexStr4ToBigEndianBytes(responseData.substring(10, 14)));
		resSubCmd = EditUtil.bytesToHexStr(EditUtil.hexStr4ToBigEndianBytes(responseData.substring(14, 18)));
	}

	public String getErrorCode(){
		return errorCode;
	}
	
	public String getErrMsg(){
		if(READ_STARTADDRESS.equals(errorCode)){
			return "시작번지가 초과되었습니다.";
		}else if(READ_RANGEOUT1.equals(errorCode)){
			return "읽기 쓰기 점수가 허용범위를 벗어납니다.";
		}else if(READ_RANGEOUT2.equals(errorCode)){
			return "읽기 쓰기 점수가 허용범위를 벗어납니다.";
		}else if(READ_RANGEOUT3.equals(errorCode)){
			return "읽기 쓰기 점수가 허용범위를 벗어납니다.";
		}else if(READ_RANGEOUT4.equals(errorCode)){
			return "읽기 쓰기 점수가 허용범위를 벗어납니다.";
		}else if(READ_REQ_EXCEED.equals(errorCode)){
			return "최대번지를 초과하는 읽기 쓰기를 요구했습니다.";
		}else if(CMD_SCMD_SYNTAXERR.equals(errorCode)){
			return "커멘드/서브커멘드가 잘못지정 되었습니다.";
		}
		return "등록되지 않은 에러코드입니다.";
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("ErrorMessage : " + getErrMsg());
		sb.append("  에러 종료 정보 : ");
		sb.append("ErrorCode : " + errorCode);
		sb.append(", ");
		sb.append("Network number : " + resNetNo);
		sb.append(", ");
		sb.append("PLC number : " + resPlcNo);
		sb.append(", ");
		sb.append("I/O req module : " + resIoNo);
		sb.append(", ");
		sb.append("Module number : " + resModuleNo);
		sb.append(", ");
		sb.append("Command : " + resCmd);
		sb.append(", ");
		sb.append("Sub command : " + resSubCmd);
		return sb.toString();
	}
	
	public String getResNetNo() {
		return resNetNo;
	}

	public String getResPlcNo() {
		return resPlcNo;
	}

	public String getResIoNo() {
		return resIoNo;
	}

	public String getResModuleNo() {
		return resModuleNo;
	}

	public String getResCmd() {
		return resCmd;
	}

	public String getResSubCmd() {
		return resSubCmd;
	}

}
	
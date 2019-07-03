package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception;

/**
 * @author DBeom
 * PLC수집 결과가 잘못된 경우를 처리하는 Exception
 */ 
public class MCProtocolResponseException extends Exception {

	private static final long serialVersionUID = -828468672742217338L;
	
	private String code = "null";
	private String msg = "null";

	public MCProtocolResponseException(){
		super();
	}
	
	public MCProtocolResponseException(Exception e){
		super(e);
	}
	
	public MCProtocolResponseException(String code){
		super(code);
		this.code = code;
	}
	
	public MCProtocolResponseException(String code, Exception e){
		super(code, e);
		this.code = code;
	}
	
	public MCProtocolResponseException(String code, String msg){
		super(msg);
		
		this.code = code;
		this.msg = msg;
	}
	
	public MCProtocolResponseException(String code, String msg, Exception e){
		super(msg, e);
		
		this.code = code;
		this.msg = msg;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
	
}

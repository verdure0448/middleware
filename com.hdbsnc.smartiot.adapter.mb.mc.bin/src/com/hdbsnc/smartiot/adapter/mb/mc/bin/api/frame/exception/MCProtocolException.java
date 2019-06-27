package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception;

/**
 * @author dbkim
 * 멜섹 프로토콜 개발 시 에러가 날 경우 에러를 정의 함.
 */
public class MCProtocolException extends Exception{
	
	private static final long serialVersionUID = -7407672838036793753L;

	private String code = "null";
	private String msg = "null";

	public MCProtocolException(){
		super();
	}
	
	public MCProtocolException(Exception e){
		super(e);
	}
	
	public MCProtocolException(String code){
		super(code);
	}
	
	public MCProtocolException(String code, Exception e){
		super(code, e);
	}
	
	public MCProtocolException(String code, String msg){
		super(code);
		this.code = code;
	}
	
	public MCProtocolException(String code, String msg, Exception e){
		super(code, e);
		
		this.code = code;
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		
		String result = code + ":" + msg;
		
		return super.getMessage();
	}
}
	
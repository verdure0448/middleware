package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception;

/**
 * @author DBeom
 * 사용자의 잘못된 요청의 경우를 처리하는 Exception
 */ 
public class ApplicationException extends Exception {

	private static final long serialVersionUID = -421619963378489677L;
	
	private String code = "null";
	private String msg = "null";

	public ApplicationException(){
		super();
	}
	
	public ApplicationException(Exception e){
		super(e);
	}
	
	public ApplicationException(String code){
		super(code);
		
		this.code = code;
	}
	
	public ApplicationException(String code, Exception e){
		super(code, e);
		
		this.code = code;
	}
	
	public ApplicationException(String code, String msg){
		super(msg);
		
		this.code = code;
		this.msg = msg;
	}
	
	public ApplicationException(String code, String msg, Exception e){
		super(code, e);
		
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

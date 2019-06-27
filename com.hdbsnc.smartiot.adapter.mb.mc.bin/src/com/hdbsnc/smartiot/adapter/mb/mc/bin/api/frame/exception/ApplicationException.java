package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception;

/**
 * @author DBeom
 * 공통적인 에러코드가 들어갈 곳
 */ 
public class ApplicationException extends Exception {

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
	}
	
	public ApplicationException(String code, Exception e){
		super(code, e);
	}
	
	public ApplicationException(String code, String msg){
		super(code);
		this.code = code;
	}
	
	public ApplicationException(String code, String msg, Exception e){
		super(code, e);
		
		this.code = code;
		this.msg = msg;
	}
	
}

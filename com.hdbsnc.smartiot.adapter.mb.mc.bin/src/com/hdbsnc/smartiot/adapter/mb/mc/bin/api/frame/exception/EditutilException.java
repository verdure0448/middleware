package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.exception;

/**
 * @author DBeom
 * 공통적인 에러코드가 들어갈 곳
 */ 
public class EditutilException extends Exception {

	private String code = "null";
	private String msg = "null";

	public EditutilException(){
		super();
	}
	
	public EditutilException(Exception e){
		super(e);
	}
	
	public EditutilException(String code){
		super(code);
	}
	
	public EditutilException(String code, Exception e){
		super(code, e);
	}
	
	public EditutilException(String code, String msg){
		super(code);
		this.code = code;
	}
	
	public EditutilException(String code, String msg, Exception e){
		super(code, e);
		
		this.code = code;
		this.msg = msg;
	}
	
}

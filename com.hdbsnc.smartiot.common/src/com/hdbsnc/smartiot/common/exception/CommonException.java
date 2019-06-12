package com.hdbsnc.smartiot.common.exception;

public class CommonException extends Exception{
	
	public static final String TYPE_ERROR = "error";
	public static final String TYPE_WARNNING = "warnning";
	public static final String TYPE_INFO = "info";
	
	private String code;
	private String type;
	private String msg;
	
	protected CommonException(String code, String type, String msg){
		super(code);
		this.code = code;
		this.type = type;
		this.msg = msg;
	}
	
	protected CommonException(String code, String type, String msg, Throwable e){
		super(code, e);
		this.code = code;
		this.type = type;
		this.msg = msg;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getMessage(){
		return this.msg;
	}

}

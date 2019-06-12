package com.hdbsnc.smartiot.common.exception;

public class ApplicationException extends CommonException {

	public  ApplicationException(String code, String type, String msg) {
		super(code, type, msg);
	}
	
	public  ApplicationException(String code, String type, String msg, Throwable e) {
		super(code, type, msg, e);
	}
}

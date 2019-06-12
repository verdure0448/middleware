package com.hdbsnc.smartiot.common.exception;

public class SystemException extends CommonException {

	public  SystemException(String code, String type, String msg) {
		super(code, type, msg);
	}
	
	public  SystemException(String code, String type, String msg, Throwable e) {
		super(code, type, msg, e);
	}
}

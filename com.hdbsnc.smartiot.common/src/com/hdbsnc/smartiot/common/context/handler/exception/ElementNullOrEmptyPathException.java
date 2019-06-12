package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.exception.CommonException;

public class ElementNullOrEmptyPathException extends CommonException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5972955417846028524L;

	public ElementNullOrEmptyPathException() {
		super("995", CommonException.TYPE_ERROR, "");
	}

	@Override
	public String getMessage() {
		return "장치에 Null 및 Empty 기능 및 속성은 지원하지 않습니다.";
	}
}

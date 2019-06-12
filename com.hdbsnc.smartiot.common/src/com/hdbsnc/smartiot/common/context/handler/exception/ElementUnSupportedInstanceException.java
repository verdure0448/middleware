package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.exception.CommonException;

public class ElementUnSupportedInstanceException extends CommonException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6769582361176966304L;

	public ElementUnSupportedInstanceException() {
		super("994", TYPE_ERROR, "");
	}

	@Override
	public String getMessage() {
		return "장치에서 지원하지 않는 기능 및 속성 타입입니다.";
	}

}

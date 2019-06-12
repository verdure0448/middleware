package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.exception.CommonException;

public class ElementNotFoundException extends CommonException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2929522718396853583L;
	private String path;
	public ElementNotFoundException(String path) {
		super("996", TYPE_ERROR, "");
		this.path = path;
	}
	
	@Override
	public String getMessage() {
		return "장치에 해당 기능 혹은 속성이 존재하지 않습니다.("+path+")";
	}

}

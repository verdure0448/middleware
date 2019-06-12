package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.exception.CommonException;

public class ContextHandlerNotFoundException extends CommonException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8323765514527580627L;
	private IContext inCtx;
	
	public ContextHandlerNotFoundException(IContext inCtx){
		super("990", TYPE_ERROR, "");
		this.inCtx = inCtx;
	}

	@Override
	public String getMessage() {
		return "장치에서 해당 기능 및 속성 처리를 지원하지 않습니다.(" + inCtx.getFullPath() + ")";
	}
	
	public IContext getContext(){
		return this.inCtx;
	}
	
}

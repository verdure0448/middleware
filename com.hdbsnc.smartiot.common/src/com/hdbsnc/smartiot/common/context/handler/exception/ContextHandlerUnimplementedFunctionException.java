package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.exception.CommonException;

public class ContextHandlerUnimplementedFunctionException extends CommonException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8046036560921202037L;
	private IContext inCtx;
	public ContextHandlerUnimplementedFunctionException(IContext inCtx){
		super("993", TYPE_ERROR, "");
		this.inCtx = inCtx;
	}
	
	@Override
	public String getMessage() {
		return "장치에서 제공하지 않는 기능 및 속성 입니다.(" + inCtx.getFullPath() + ")";
	}
	
	public IContext getContext(){
		return this.inCtx;
	}
}

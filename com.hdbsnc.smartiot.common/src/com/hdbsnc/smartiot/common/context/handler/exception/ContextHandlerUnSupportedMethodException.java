package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.exception.CommonException;

public class ContextHandlerUnSupportedMethodException extends CommonException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2659336628372862872L;
	private IContext inCtx;
	private String methodName;
	
	public ContextHandlerUnSupportedMethodException(IContext inCtx, String methodName){
		super("994", CommonException.TYPE_ERROR, "");
		this.inCtx = inCtx;
		this.methodName = methodName;
	}
	@Override
	public String getMessage() {
		return "장치의 "+inCtx.getFullPath()+" 에서 지원하지 않는 속성 메소드입니다.("+methodName+")";
	}
	
	public IContext getContext(){
		return this.inCtx;
	}
	
	public String getMethodName(){
		return this.methodName;
	}
	
}

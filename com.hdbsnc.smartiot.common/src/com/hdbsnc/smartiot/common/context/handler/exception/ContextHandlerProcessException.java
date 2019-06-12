package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.exception.CommonException;

public class ContextHandlerProcessException extends CommonException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4062521465599570378L;
	private IContext inCtx;
	
	public ContextHandlerProcessException(IContext inCtx, Exception e){
		super("992", TYPE_ERROR, "", e);
		this.inCtx = inCtx;
	}
	
	public IContext getContext(){
		return this.inCtx;
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		if(inCtx.getPaths()!=null) sb.append(inCtx.getFullPath()).append(" : ");
		sb.append(this.getCause().getMessage());
		return sb.toString();
	}
}

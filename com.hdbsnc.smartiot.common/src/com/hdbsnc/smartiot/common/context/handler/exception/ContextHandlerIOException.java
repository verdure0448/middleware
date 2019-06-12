package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.exception.CommonException;

public class ContextHandlerIOException extends CommonException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1385931478598172219L;
	private IContext inCtx;
	
	public ContextHandlerIOException(IContext inCtx, Exception e){
		super("991", TYPE_ERROR, "", e);
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

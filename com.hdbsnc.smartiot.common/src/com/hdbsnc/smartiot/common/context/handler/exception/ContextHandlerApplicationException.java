package com.hdbsnc.smartiot.common.context.handler.exception;

import com.hdbsnc.smartiot.common.exception.CommonException;

public class ContextHandlerApplicationException extends CommonException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4309112975762754511L;

//	private String msg = "";
//	private Exception e = null;

	public ContextHandlerApplicationException(String code, String type, String msg) {
		super(code, type, msg);

	}

	public ContextHandlerApplicationException(String code, String type, String msg, Throwable e) {
		super(code, type, msg, e);
	}

//	public ContextHandlerApplicationException(IProfileManager pm, String innerCode, String[] param) {
//		super(Integer.parseInt(pm.getMessageObj(innerCode).getOuterCode()), pm.getMessageObj(innerCode).getType());
//		this.msg = createMsgContext(pm.getMessageObj(innerCode).getContext(), param);
//	}

//	@Override
//	public String getMessage() {
//		if (e != null) {
//			return msg + " [" + e.getMessage() + "]";
//		}
//		return msg;
//	}

}

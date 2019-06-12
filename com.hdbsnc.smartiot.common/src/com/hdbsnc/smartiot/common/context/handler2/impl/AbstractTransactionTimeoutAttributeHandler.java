package com.hdbsnc.smartiot.common.context.handler2.impl;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnSupportedMethodException;
import com.hdbsnc.smartiot.common.context.handler2.IAttributeHandler;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;

public abstract class AbstractTransactionTimeoutAttributeHandler extends AbstractTransactionTimeoutFunctionHandler implements IAttributeHandler{
	
	protected AbstractTransactionTimeoutAttributeHandler(String name){
		super(null, name, 3000);
	}

	protected AbstractTransactionTimeoutAttributeHandler(IDirectoryHandler parent, String name){
		super(parent, name, 3000);
	}
	
	protected AbstractTransactionTimeoutAttributeHandler(String name, long timeout){
		super(null, name, timeout);
	}

	@Override
	public void transactionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws ContextHandlerUnSupportedMethodException, Exception {
		Map<String, String> params = inboundCtx.getParams();
		String value = null, method = null;
		if(params==null){
			method = "read";
		}else if(params.containsKey("update")){
			method = "update";
		}else if(params.containsKey("read")){
			method = "read";	
		}else if(params.containsKey("create")){
			method = "create";
		}else if(params.containsKey("delete")){
			method = "delete";
		}else{
			method = "read";
		}
		String tid = inboundCtx.getTID();
		ISession session = null;
		ISessionManager sm = getSessionManager();
		if(tid==null || tid.equals("this") || tid.equals("")){
			session = sm.getSessionBySessionKey(inboundCtx.getSID());
		}else{
			session = sm.getSessionByDeviceId(tid);
		}
		if(!method.equals("read")){
			value = params.get(method);
		}else{
			value = null;
		}
		String key = inboundCtx.getFullPath();
		switch(method){
		case "read":
			read(inboundCtx, outboundCtx, session, value);
			if(outboundCtx.getPaths().contains("ack")){
				String updateValue = outboundCtx.getParams().get(method);
				if(updateValue==null) updateValue = "";
				session.setAttributeValue(key, updateValue);
			}
			break;
		case "update":
			update(inboundCtx, outboundCtx, session, value);
			if(outboundCtx.getPaths().contains("ack")){
				String updateValue = outboundCtx.getParams().get(method);
				if(updateValue==null) updateValue = "";
				session.setAttributeValue(key, updateValue);
			}
			break;
		case "create":
			create(inboundCtx, outboundCtx, session, value);
			if(outboundCtx.getPaths().contains("ack")){
				String updateValue = outboundCtx.getParams().get(method);
				if(updateValue==null) updateValue = "";
				session.setAttributeValue(key, updateValue);
			}
			break;
		case "delete":
			delete(inboundCtx, outboundCtx, session, value);
			if(outboundCtx.getPaths().contains("ack")){
				session.getAttributeKeys().remove(key);
			}
			break;
			default:
				throw new ContextHandlerUnSupportedMethodException(inboundCtx, method);
		}
	}
	
	@Override
	public void rejectionProcess(IContext inboundCtx, OutboundContext outboundCtx) throws ContextHandlerUnSupportedMethodException, Exception {
		
		outboundCtx.getPaths().add("nack");
		outboundCtx.getParams().put("code", "W9001");
		outboundCtx.getParams().put("type", "warn");
		outboundCtx.getParams().put("msg", "트랜젝션이 잠겨 있습니다.(다른 request가 선행 호출되어 있을 수 있습니다.)");
		outboundCtx.setTransmission("res");
		
	}
	
}

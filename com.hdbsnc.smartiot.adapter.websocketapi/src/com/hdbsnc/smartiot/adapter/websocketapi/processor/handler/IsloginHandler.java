package com.hdbsnc.smartiot.adapter.websocketapi.processor.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;

public class IsloginHandler extends AbstractFunctionHandler{

	public IsloginHandler() {
		super("islogin");
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String sid = inboundCtx.getSID();
		if(sid!=null && sid.startsWith("sid-")){
			if(getSessionManager().isValidSession(sid)){
				outboundCtx.addPath("ack");
				outboundCtx.setTransmission("res");
				return;
			}
		}else{
			if(getSessionManager().containsDeviceId(sid)){
				outboundCtx.addPath("ack");
				outboundCtx.setTransmission("res");
				return;
			}
		}
		outboundCtx.addPath("nack");
		outboundCtx.setTransmission("res");
	}

}
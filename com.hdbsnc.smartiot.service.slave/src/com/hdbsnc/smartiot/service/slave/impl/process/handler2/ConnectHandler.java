package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;

public class ConnectHandler extends AbstractFunctionHandler{
	Sss2 sss;
	
	public ConnectHandler(Sss2 sss){
		super("connect");
		this.sss = sss;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		outboundCtx.setSID(sss.getSlaveServerSessionId());
		outboundCtx.setTID("this");
		//경로는 현재경로 그대로 마스터로 전송. 
		
	}

}

package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;

public class DefaultAckHandler extends AbstractFunctionHandler{

	private Sss2 sss;
	public DefaultAckHandler(Sss2 sss){
		super("ack");
		this.sss = sss;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		sss.pollAndCallContextTracer(inboundCtx.getSPort(), inboundCtx);
		
		outboundCtx.dispose();
	}
}

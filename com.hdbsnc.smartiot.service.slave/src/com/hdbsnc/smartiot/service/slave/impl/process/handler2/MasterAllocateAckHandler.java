package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;

public class MasterAllocateAckHandler extends AbstractFunctionHandler{

	private Sss2 sss;
	public MasterAllocateAckHandler(Sss2 sss){
		super("ack");
		this.sss = sss;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		IContextTracer tracer = sss.pollAndCallContextTracer(inboundCtx.getSPort(), inboundCtx);
		
		outboundCtx.dispose();
		//마스터서버에게 세션키 전달이 성공적으로 이루어졌음. 
		//추가로 할 작업은?
	}
}

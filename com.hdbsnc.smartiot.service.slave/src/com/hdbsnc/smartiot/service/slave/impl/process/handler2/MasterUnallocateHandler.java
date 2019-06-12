package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;

public class MasterUnallocateHandler extends AbstractFunctionHandler implements IContextCallback{

	private Sss2 sss;
	
	public MasterUnallocateHandler(Sss2 sss){
		super("unalloc");
		this.sss = sss;
	}
	
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		outboundCtx.setSID(sss.getSlaveServerSessionId());
		outboundCtx.setSPort(sss.offerContextTracer(inboundCtx, this).getSeq());
		outboundCtx.setTID("this");
	}
	
	public void responseSuccess(IContextTracer ctxTracer){
		IContext inboundCtx = ctxTracer.getRequestContext();
		IContext outboundCtx = ctxTracer.getResponseContext();
		if(outboundCtx.getPaths().contains("ack")){
			this.getCommonService().getLogger().logger("SSS").info(inboundCtx.getFullPath()+" : ACK");
		}else{
			this.getCommonService().getLogger().logger("SSS").warn(inboundCtx.getFullPath()+" : NACK");
		}
	}
	
	public void responseFail(IContextTracer ctxTracer){
		this.getCommonService().getLogger().err(getCommonService().getExceptionfactory().getMsgInfo("604").getMsg());
	}
}

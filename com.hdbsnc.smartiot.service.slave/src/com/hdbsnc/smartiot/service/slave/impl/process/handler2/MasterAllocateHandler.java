package com.hdbsnc.smartiot.service.slave.impl.process.handler2;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.slave.impl.Sss2;

public class MasterAllocateHandler extends AbstractFunctionHandler implements IContextCallback{
	public static final String KEY_INSTANCE_ID = "iid";
	public static final String KEY_DEVICE_ID = "did";
	public static final String KEY_USER_ID = "uid";
	public static final String KEY_SESSION_ID = "sid";
	private Sss2 sss;
	
	public MasterAllocateHandler(Sss2 sss){
		super("alloc");
		this.sss = sss;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		Map<String, String> params = inboundCtx.getParams();
		if(!params.containsKey(KEY_INSTANCE_ID) || !params.containsKey(KEY_DEVICE_ID) || !params.containsKey(KEY_USER_ID) || !params.containsKey(KEY_SESSION_ID)){
			throw this.getCommonService().getExceptionfactory().createSysException("204");
		}
		outboundCtx.setSID(sss.getSlaveServerSessionId());
		outboundCtx.setSPort(sss.offerContextTracer(inboundCtx, this).getSeq());
		outboundCtx.setTID("this");
		
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		IContext inboundCtx = ctxTracer.getRequestContext();
		IContext outboundCtx = ctxTracer.getResponseContext();
		if(outboundCtx.getPaths().contains("ack")){
			this.getCommonService().getLogger().logger("SSS").info(inboundCtx.getFullPath()+" : ACK");
		}else{
			this.getCommonService().getLogger().logger("SSS").warn(inboundCtx.getFullPath()+" : NACK");
		}
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {

		this.getCommonService().getLogger().err(getCommonService().getExceptionfactory().getMsgInfo("604").getMsg());
	}

}

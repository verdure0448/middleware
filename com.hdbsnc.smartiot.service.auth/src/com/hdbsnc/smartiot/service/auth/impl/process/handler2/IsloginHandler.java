package com.hdbsnc.smartiot.service.auth.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.master.IMasterService;

public class IsloginHandler extends AbstractFunctionHandler{

	private IMasterService mss;
	
	public IsloginHandler(IMasterService mss){
		super("islogin");
		this.mss = mss;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String sid = inboundCtx.getSID();
		outboundCtx.setTID("this");
		outboundCtx.setTPort(null);
		if(sid==null || sid.equals("")){
			outboundCtx.addPath("nack");
		}else if(sid.startsWith("sid-")){
			if(mss.getSlaveServerManager().containsDeviceSessionKey(sid)){
				outboundCtx.addPath("ack");
			}else{
				outboundCtx.addPath("nack");
			}
		}else{
			if(mss.getSlaveServerManager().containsDevicesId(sid)){
				outboundCtx.addPath("ack");
			}else{
				outboundCtx.addPath("nack");
			}
		}
		outboundCtx.setTransmission("res");
	}

}

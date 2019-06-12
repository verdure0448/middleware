package com.hdbsnc.smartiot.service.master.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

/**
 *DID가 존재하는 슬래이브 서버를 찾아서 해당 DID를 제거한다.   
 * @author hjs0317
 *
 */
public class MasterUnallocateHandler extends AbstractFunctionHandler{

	private SlaveServerManager ssm;
	public static final String KEY_SESSION_ID = "sid";
	
	public MasterUnallocateHandler(SlaveServerManager ssm){
		super("unalloc");
		this.ssm = ssm;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		String deviceSid = inboundCtx.getParams().get(KEY_SESSION_ID);
		if(deviceSid==null) throw getCommonService().getExceptionfactory().createSysException("204");
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		server.removeDevice(deviceSid);
		
		outboundCtx.setTID("this");
		outboundCtx.setSID(slaveSid);
		outboundCtx.getPaths().add("ack");
		outboundCtx.setTransmission("res");
	}

}

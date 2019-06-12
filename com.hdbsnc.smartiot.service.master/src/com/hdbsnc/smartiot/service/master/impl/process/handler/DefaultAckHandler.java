package com.hdbsnc.smartiot.service.master.impl.process.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

@Deprecated
public class DefaultAckHandler extends AbstractFunctionHandler{

	private SlaveServerManager ssm;
	/**
	 * port번호를 체크해서 ContextTracer를 호출해주는 핸들러 
	 * @param ssm
	 */
	public DefaultAckHandler(SlaveServerManager ssm){
		super("ack");
		this.ssm = ssm;
	}

	@Override
	public void process(IContext inboundCtx) throws Exception {
		String sid = inboundCtx.getSID();
		String sPort = inboundCtx.getSPort();
		
		Server server = ssm.getServerByServerSessionKey(sid);
		server.pollAndCallContextTracer(sPort, inboundCtx);
		
	}

}

package com.hdbsnc.smartiot.service.master.impl.process.handler2;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;
import com.hdbsnc.smartiot.util.logger.Log;

public class ConnectHandler extends AbstractFunctionHandler{

	private SlaveServerManager ssm;
	private IConnectionManager cm;

	public ConnectHandler(SlaveServerManager ssm, IConnectionManager cm) {
		super("connect");
		this.ssm = ssm;
		this.cm = cm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String slaveId = inboundCtx.getSID();
		IConnection con = cm.getConnection(slaveId);
		
		Server server = ssm.addServer(con, slaveId);
		
		outboundCtx.setSID(server.getSessionKey());
		outboundCtx.setTID("this");
		outboundCtx.getPaths().add("ack");
		outboundCtx.setTransmission("res");
		
		getCommonService().getLogger().info("슬래이브 서버에 응답 보냄: "+outboundCtx.getFullPath());
	}

}

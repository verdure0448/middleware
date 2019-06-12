package com.hdbsnc.smartiot.service.master.impl.process.handler;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFuncAndDirHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

@Deprecated
public class ConnectHandler extends AbstractFuncAndDirHandler{

	private SlaveServerManager ssm;
	private IConnectionManager cm;
	private UrlParser parser;
	
	public ConnectHandler(SlaveServerManager ssm, IConnectionManager cm){
		super("connect");
		this.ssm = ssm;
		this.cm = cm;
		this.parser = UrlParser.getInstance();
	}
	
	@Override
	public void process(IContext inboundCtx) throws Exception {
		
		String slaveId = inboundCtx.getSID();
		
		IConnection con = cm.getConnection(slaveId);
		
		Server server = ssm.addServer(con, slaveId);
		
		Url url = Url.createOtp();
		url.setUserInfo(server.getSessionKey(), null);
		url.setHostInfo("this", null);
		url.setPaths(currentPaths()).addPath("ack");
		url.addFrag("trans", "res");
		
		String packet = parser.parse(url);
		System.out.println("슬래이브 서버에 응답 보냄: "+packet);
		con.write(packet.getBytes());
		
	}



}

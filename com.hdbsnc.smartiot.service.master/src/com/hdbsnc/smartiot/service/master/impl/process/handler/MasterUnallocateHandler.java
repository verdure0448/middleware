package com.hdbsnc.smartiot.service.master.impl.process.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFuncAndDirHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

@Deprecated
public class MasterUnallocateHandler extends AbstractFuncAndDirHandler{

	private SlaveServerManager ssm;
	public static final String KEY_SESSION_ID = "sid";
	private UrlParser parser;
	
	public MasterUnallocateHandler(SlaveServerManager ssm){
		super("unalloc");
		this.ssm = ssm;
		this.parser = UrlParser.getInstance();
	}
	

	@Override
	public void process(IContext inboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		String deviceSid = inboundCtx.getParams().get(KEY_SESSION_ID);
		if(deviceSid==null) throw new Exception("필수 파라미터가 누락되었습니다.");
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		server.removeDevice(deviceSid);
		
		Url url = Url.createOtp();
		url.setHostInfo("this", null);
		url.setUserInfo(slaveSid, null);
		url.setPaths(currentPaths()).addPath("ack");
		url.addFrag("trans", "res");
		
		server.getConnection().write(parser.parse(url).getBytes());
		
	}

}

package com.hdbsnc.smartiot.service.master.impl.process.handler;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFuncAndDirHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

@Deprecated
public class MasterStartInstanceHandler extends AbstractFuncAndDirHandler{

	public static final String KEY_INSTANCE_ID = "iid";
	public static final String KEY_INSTANCE_IP = "ip";
	public static final String KEY_INSTANCE_PORT = "port";
	public static final String KEY_INSTANCE_URL = "url";
	
	private SlaveServerManager ssm;
	private UrlParser parser;
	
	public MasterStartInstanceHandler(SlaveServerManager ssm){
		super("start");
		this.ssm = ssm;
		this.parser = UrlParser.getInstance();
	}
	
	@Override
	public void process(IContext inboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		Map<String, String> params = inboundCtx.getParams();
		String iid = params.get(KEY_INSTANCE_ID);
		String ip = params.get(KEY_INSTANCE_IP);
		String port = params.get(KEY_INSTANCE_PORT);
		String urlString = params.get(KEY_INSTANCE_URL);
		if(iid==null || ip==null || port==null) throw new Exception("필수 파라미터가 누락되었습니다.");
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		Instance instance = server.getInstance(iid);
		if(instance==null){
			instance = server.addInstance(iid, ip, port, urlString);
		}
		instance.putIpPort(ip, port);
		instance.putUrl(urlString);
		instance.changeState(Instance.STATE_START);
		
		Url url = Url.createOtp();
		url.setHostInfo("this", inboundCtx.getTPort());
		url.setUserInfo(slaveSid, inboundCtx.getSPort());
		url.setPaths(currentPaths()).addPath("ack");
		url.addFrag("trans", "res");
		
		server.getConnection().write(parser.parse(url).getBytes());
	}



}

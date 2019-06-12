package com.hdbsnc.smartiot.service.master.impl.process.handler2;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance;

public class MasterStartInstanceHandler extends AbstractFunctionHandler{
	public static final String KEY_INSTANCE_ID = "iid";
	public static final String KEY_INSTANCE_IP = "ip";
	public static final String KEY_INSTANCE_PORT = "port";
	public static final String KEY_INSTANCE_URL = "url";
	private SlaveServerManager ssm;
	
	public MasterStartInstanceHandler(SlaveServerManager ssm) {
		super("start");
		this.ssm = ssm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		Map<String, String> params = inboundCtx.getParams();
		String iid = params.get(KEY_INSTANCE_ID);
		String ip = params.get(KEY_INSTANCE_IP);
		String port = params.get(KEY_INSTANCE_PORT);
		String urlString = params.get(KEY_INSTANCE_URL);
		if(iid==null || ip==null || port==null) throw getCommonService().getExceptionfactory().createSysException("204");
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		Instance instance = server.getInstance(iid);
		if(instance==null){
			instance = server.addInstance(iid, ip, port, urlString);
		}
		instance.putIpPort(ip, port);
		instance.putUrl(urlString);
		instance.changeState(Instance.STATE_START);
		
		outboundCtx.setSID(slaveSid);
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.getPaths().add("ack");
		outboundCtx.setTransmission("res");
		
	}

}

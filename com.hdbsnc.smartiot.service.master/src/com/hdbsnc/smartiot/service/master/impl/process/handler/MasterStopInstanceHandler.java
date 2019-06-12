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
public class MasterStopInstanceHandler extends AbstractFuncAndDirHandler{
	public static final String KEY_INSTANCE_ID = "iid";
	
	private SlaveServerManager ssm;
	private UrlParser parser;
	
	public MasterStopInstanceHandler(SlaveServerManager ssm){
		super("stop");
		this.ssm = ssm;
		this.parser = UrlParser.getInstance();
	}
	
	@Override
	public void process(IContext inboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		Map<String, String> params = inboundCtx.getParams();
		String iid = params.get(KEY_INSTANCE_ID);
		if(iid==null) throw new Exception("필수 파라미터가 누락되었습니다.");
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		Instance instance = server.getInstance(iid);
		
		Url url = Url.createOtp();
		url.setHostInfo("this", inboundCtx.getTPort());
		url.setUserInfo(slaveSid, inboundCtx.getSPort());
		url.setPaths(currentPaths());
		url.addFrag("trans", "res");
		
		if(instance==null){
			//nack
			url.addPath("nack");
			//errcd, msg는 추후 정의하여 전송.
		}else{
			//ack
			instance.changeState(Instance.STATE_STOP);
			url.addPath("ack");
			
		}
		
		server.getConnection().write(parser.parse(url).getBytes());
		
	}


}

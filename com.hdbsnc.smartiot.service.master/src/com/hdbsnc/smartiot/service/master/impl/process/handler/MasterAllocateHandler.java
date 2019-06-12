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
public class MasterAllocateHandler extends AbstractFuncAndDirHandler{

	public static final String KEY_INSTANCE_ID = "iid";
	public static final String KEY_SESSION_ID = "sid";
	public static final String KEY_DEVICE_ID = "did";
	public static final String KEY_USER_ID = "uid";
	
	private SlaveServerManager ssm;
	private UrlParser parser;
	
	public MasterAllocateHandler(SlaveServerManager ssm){
		super("alloc");
		this.ssm = ssm;
		this.parser = UrlParser.getInstance();
	}
	
	/**
	 * 예제 패
	 * otp://sid-sdlfkjsdlkfjlsdkjflksdjflskdjlfhgh@this/master/alloc?iid=com.pulmuone.instance.adtg.10&did=com.pulmuone.adtg.123&sid=[ device session key ]
	 * 
	 * @param inboundCtx
	 * @throws Exception
	 */
	@Override
	public void process(IContext inboundCtx) throws Exception {
		Map<String, String> params = inboundCtx.getParams();
		if(!params.containsKey(KEY_DEVICE_ID) || !params.containsKey(KEY_INSTANCE_ID) || !params.containsKey(KEY_SESSION_ID) || !params.containsKey(KEY_USER_ID)){
			throw new Exception("필수 파라미터가 누락되었습니다.");
		}
		
		String sid = inboundCtx.getSID();
		
		String iid = params.get(KEY_INSTANCE_ID);
		String did = params.get(KEY_DEVICE_ID);
		String deviceSid = params.get(KEY_SESSION_ID);
		String uid = params.get(KEY_USER_ID);
		Server server = ssm.getServerByServerSessionKey(sid);
		
		Instance instance = server.getInstance(iid);
		if(instance==null){
			instance = server.addInstance(iid);
			//마스터서버에서 관리되지 않던 인스턴스의 자식으로 들어온 케이스. 이러한 케이스가 생기지 않도록 해야 한다.
		}
		instance.putDevice(did, deviceSid, uid);
		
		Url url = Url.createOtp();
		url.setHostInfo("this", inboundCtx.getTPort());
		url.setUserInfo(sid, inboundCtx.getSPort());
		url.setPaths(currentPaths()).addPath("ack");
		url.addFrag("trans", "res");
		
		server.getConnection().write(parser.parse(url).getBytes());
	}


}

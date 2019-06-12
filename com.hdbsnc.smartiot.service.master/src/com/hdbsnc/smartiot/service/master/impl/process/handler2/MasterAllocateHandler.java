package com.hdbsnc.smartiot.service.master.impl.process.handler2;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

public class MasterAllocateHandler extends AbstractFunctionHandler{
	public static final String KEY_INSTANCE_ID = "iid";
	public static final String KEY_SESSION_ID = "sid";
	public static final String KEY_DEVICE_ID = "did";
	public static final String KEY_USER_ID = "uid";
	
	private SlaveServerManager ssm;
	
	public MasterAllocateHandler(SlaveServerManager ssm) {
		super("alloc");
		this.ssm = ssm;
	}

	/**
	 * 예제 패
	 * otp://sid-sdlfkjsdlkfjlsdkjflksdjflskdjlfhgh@this/master/alloc?iid=com.pulmuone.instance.adtg.10&did=com.pulmuone.adtg.123&sid=[ device session key ]
	 * 
	 * @param inboundCtx
	 * @throws Exception
	 */
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		Map<String, String> params = inboundCtx.getParams();
		if(!params.containsKey(KEY_DEVICE_ID) || !params.containsKey(KEY_INSTANCE_ID) || !params.containsKey(KEY_SESSION_ID) || !params.containsKey(KEY_USER_ID)){
			throw getCommonService().getExceptionfactory().createSysException("204");
		}
		String sid = inboundCtx.getSID();
		String iid = params.get(KEY_INSTANCE_ID);
		String did = params.get(KEY_DEVICE_ID);
		String deviceSid = params.get(KEY_SESSION_ID);
		String uid = params.get(KEY_USER_ID);
		Server server = ssm.getServerByServerSessionKey(sid);
		
		Instance instance = server.getInstance(iid);
		if(instance==null){
			//싱글 아답터(클라이언트 모드)의 경우 자기자신의 디폴트 장치식별자로 셀프인증을 하기에 이메소드를 타는 케이스가 발생한다. 
			instance = server.addInstance(iid);
//			throw getCommonService().getExceptionfactory().createSysException("507", new String[]{iid});
		}
		instance.putDevice(did, deviceSid, uid);
		
		outboundCtx.setSID(sid);
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.getPaths().add("ack");
		outboundCtx.setTransmission("res");
	}

}

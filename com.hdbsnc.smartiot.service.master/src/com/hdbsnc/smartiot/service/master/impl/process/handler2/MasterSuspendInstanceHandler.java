package com.hdbsnc.smartiot.service.master.impl.process.handler2;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

public class MasterSuspendInstanceHandler extends AbstractFunctionHandler{

	public static final String KEY_INSTANCE_ID ="iid";
	private SlaveServerManager ssm;
	
	public MasterSuspendInstanceHandler(SlaveServerManager ssm){
		super("suspend");
		this.ssm = ssm;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		Map<String, String> params = inboundCtx.getParams();
		String iid = params.get(KEY_INSTANCE_ID);
		if(iid==null) {
//			throw new Exception("필수 파라미터가 누락되었습니다.");
			throw getCommonService().getExceptionfactory().createSysException("204");
		}
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		Instance instance = server.getInstance(iid);
		
		outboundCtx.setTID("this");
		outboundCtx.setTPort(inboundCtx.getTPort());
		outboundCtx.setSID(slaveSid);
		outboundCtx.setSPort(inboundCtx.getSPort());
		outboundCtx.setTransmission("res");
		
		if(instance==null){
			//nack
			outboundCtx.getPaths().add("nack");
			//errcd, msg는 추후 정의하여 전송.
		}else{
			//ack
			instance.changeState(Instance.STATE_SUSPEND);
			outboundCtx.getPaths().add("ack");
		}
		
	}

}

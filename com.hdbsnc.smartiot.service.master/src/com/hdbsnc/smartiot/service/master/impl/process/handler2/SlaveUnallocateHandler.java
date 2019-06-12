package com.hdbsnc.smartiot.service.master.impl.process.handler2;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance.Device;

public class SlaveUnallocateHandler extends AbstractFunctionHandler implements IContextCallback{
	public static final String KEY_SESSION_ID = "sid";
	private SlaveServerManager ssm;
	
	public SlaveUnallocateHandler(SlaveServerManager ssm){
		super("unalloc");
		this.ssm = ssm;
	}
	
	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		String deviceSid = inboundCtx.getParams().get(KEY_SESSION_ID);
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		
		if(server==null) {
//			throw new Exception("해당세션키의 서버가 존재하지 않습니다.");
			throw getCommonService().getExceptionfactory().createSysException("503", new String[]{slaveSid});
		}
		
		outboundCtx.setTID("this");
		outboundCtx.getParams().put(KEY_SESSION_ID, deviceSid);
		outboundCtx.setSID(slaveSid);
		outboundCtx.setSPort(server.offerContextTracer(inboundCtx, this).getSeq());
		
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		IContext request = ctxTracer.getRequestContext();
		IContext response = ctxTracer.getResponseContext();
		
		if(response.getPaths().contains("ack")){
			String slaveSid = request.getSID();
			String deviceSid = request.getParams().get(KEY_SESSION_ID);
			
			Server server = ssm.getServerByServerSessionKey(slaveSid);
			Device removeDevice = server.removeDevice(deviceSid);
		}else{
			//nack로 온 케이스. 그냥 바이패스. 에러코드, 메시지등이 그대로 전송 될 것이다.
		}
		
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		// 처리할 부분 없음. 바이패스. 
		
	}

}

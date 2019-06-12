package com.hdbsnc.smartiot.service.master.impl.process.handler;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFuncAndDirHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance.Device;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

/**
 * 내부에서 사용하는 핸들러. 외부요청용은 별도로 만들어야 함. 
 * @author hjs0317
 *
 */
@Deprecated
public class SlaveUnallocateHandler extends AbstractFuncAndDirHandler implements IContextCallback{

	public static final String KEY_SESSION_ID = "sid";
	private SlaveServerManager ssm;
	private UrlParser parser;
	public SlaveUnallocateHandler(SlaveServerManager ssm){
		super("unalloc");
		this.ssm = ssm;
		this.parser = UrlParser.getInstance();
	}
	
	@Override
	public void process(IContext inboundCtx) throws Exception {
		String slaveSid = inboundCtx.getSID();
		String deviceSid = inboundCtx.getParams().get(KEY_SESSION_ID);
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		
		if(server==null) throw new Exception("해당세션키의 서버가 존재하지 않습니다.");
		
		Url requestUrl = Url.createOtp();
		requestUrl.setHostInfo("this", null);
		requestUrl.setPaths(this.currentPaths());
		requestUrl.addQuery(KEY_SESSION_ID, deviceSid);
		requestUrl.setUserInfo(slaveSid, server.offerContextTracer(inboundCtx, this).getSeq());
		
		server.getConnection().write(parser.parse(requestUrl).getBytes());
		
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		IContext request = ctxTracer.getRequestContext();
		IContext response = ctxTracer.getResponseContext();
		
		String slaveSid = request.getSID();
		String deviceSid = request.getParams().get(KEY_SESSION_ID);
		
		Server server = ssm.getServerByServerSessionKey(slaveSid);
		Device removeDevice = server.removeDevice(deviceSid);
		
		
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		// TODO Auto-generated method stub
		
	}

}

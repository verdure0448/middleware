package com.hdbsnc.smartiot.service.master.impl.process.handler;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler.impl.AbstractFuncAndDirHandler;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

/**
 * 마스터에서 특정 슬래이브 서버에게 세션을 할당하는 커맨드
 * 
 * 전송 방향: 마스터 -> 슬래이브
 * 
 * @author hjs0317
 *
 */
@Deprecated
public class SlaveAllocateHandler extends AbstractFuncAndDirHandler implements IContextCallback{

	
	public static final String KEY_INSTANCE_ID = "iid";
	public static final String KEY_DEVICE_ID = "did";
	public static final String KEY_SESSION_ID = "sid";
	public static final String KEY_USER_ID = "uid";
	public static final String KEY_CONNECT_DOMAIN = "comsvr";
	public static final String KEY_URL = "url";
	
	private SlaveServerManager ssm;
	private UrlParser parser;
	
	public SlaveAllocateHandler(SlaveServerManager ssm){
		super("alloc");
		this.ssm = ssm;
		this.parser = UrlParser.getInstance();
	}
	
	/**
	 * 비동기로 slave/alloc 커맨드에 대한 ack를 받는 곳.
	 */
	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		IContext request = ctxTracer.getRequestContext();
		IContext response = ctxTracer.getResponseContext();
		String iid = request.getParams().get(KEY_INSTANCE_ID);
		String did = request.getParams().get(KEY_DEVICE_ID);
		String uid = request.getParams().get(KEY_USER_ID);
		String sid = request.getParams().get(KEY_SESSION_ID);
		String serverSid = response.getSID();
		Server server = ssm.getServerByServerSessionKey(serverSid);
		Instance instance = server.getInstance(iid);
		String url = instance.getConnectUrl();
		instance.putDevice(did, sid, uid);
		
		response.getParams().put(KEY_CONNECT_DOMAIN, instance.getConnectIp()+":"+instance.getConnectPort());
		response.getParams().put(KEY_URL, url);
		
		//이후 Ass의 auth/open의 응답으로 전송 된다.
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		IContext nack = ctxTracer.getResponseContext();
		if(nack==null) {
			//메시지가 없는 경우는 ContextTracer에서 자동으로 제거된 경우 
			return;
		}
	}

	/**
	 * inboundCtx에는 슬래이서버를 모른체 오도록 할것인지? 아니면 할당된 슬래이서버를 지정해서 오게 할 것인지.
	 */
	@Override
	public void process(IContext inboundCtx) throws Exception {
		
		Map<String, String> params = inboundCtx.getParams();
		if(!params.containsKey(KEY_DEVICE_ID) || !params.containsKey(KEY_INSTANCE_ID) || !params.containsKey(KEY_SESSION_ID) || !params.containsKey(KEY_USER_ID)){
			throw new Exception("필수 파라미터가 누락되었습니다.");
		}
		String did = params.get(KEY_DEVICE_ID);
		String iid = params.get(KEY_INSTANCE_ID);
		Server selectedServer = ssm.getServerByDeviceId(did);
		
		if(selectedServer!=null){
			System.out.println("이미 할당된 서버가 있으므로 그쪽으로 보낸다. ");
		}else{
			System.out.println("할당된 서버가 없으므로 라운드로빈셀렉트를 해서 서버를 할당 받는다. ");
			try{
				selectedServer = ssm.roundRobinSelectSlaveServer(iid);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
		}
		
		
		
		Url requestUrl = Url.createOtp(inboundCtx.getPaths(), inboundCtx.getParams());
		requestUrl.setHostInfo("this", null);
		requestUrl.setUserInfo(selectedServer.getSessionKey(), selectedServer.offerContextTracer(inboundCtx, this).getSeq());
		
		
		
		selectedServer.getConnection().write(parser.parse(requestUrl).getBytes());
		
	
	}
	

}

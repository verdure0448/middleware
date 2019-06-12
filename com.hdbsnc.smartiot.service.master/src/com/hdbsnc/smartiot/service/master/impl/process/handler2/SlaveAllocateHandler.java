package com.hdbsnc.smartiot.service.master.impl.process.handler2;

import java.util.Map;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.AbstractFunctionHandler;
import com.hdbsnc.smartiot.common.exception.SystemException;
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
public class SlaveAllocateHandler extends AbstractFunctionHandler implements IContextCallback{
	public static final String KEY_INSTANCE_ID = "iid";
	public static final String KEY_DEVICE_ID = "did";
	public static final String KEY_SESSION_ID = "sid";
	public static final String KEY_USER_ID = "uid";
	public static final String KEY_CONNECT_DOMAIN = "comsvr";
	public static final String KEY_URL = "url";
	private SlaveServerManager ssm;
	
	public SlaveAllocateHandler(SlaveServerManager ssm) {
		super("alloc");
		this.ssm = ssm;
	}

	@Override
	public void process(IContext inboundCtx, OutboundContext outboundCtx) throws Exception {
		Map<String, String> params = inboundCtx.getParams();
		if(!params.containsKey(KEY_DEVICE_ID) || !params.containsKey(KEY_INSTANCE_ID) || !params.containsKey(KEY_SESSION_ID) || !params.containsKey(KEY_USER_ID)){
			throw getCommonService().getExceptionfactory().createSysException("204");
		}
		String did = params.get(KEY_DEVICE_ID);
		String iid = params.get(KEY_INSTANCE_ID);
		Server selectedServer = ssm.getServerByDeviceId(did);
		
		if(selectedServer!=null){
			getCommonService().getLogger().logger("MSS").info("이미 할당된 서버가 있으므로 그쪽으로 보낸다. ");
		}else{
			getCommonService().getLogger().logger("MSS").info("할당된 서버가 없으므로 라운드로빈셀렉트를 해서 서버를 할당 받는다. ");
			try{
				selectedServer = ssm.roundRobinSelectSlaveServer(iid);
			}catch(SystemException e){
				throw e;//509 예외를 그대로 던진다. 
			}catch(Exception e){
				throw getCommonService().getExceptionfactory().createSysException("508", new String[]{did}, e);
			}
		}
		
		outboundCtx.setTID("this");
		outboundCtx.setSID(selectedServer.getSessionKey());
		outboundCtx.setSPort(selectedServer.offerContextTracer(inboundCtx, this).getSeq());
		
	}
	
	/**
	 * 비동기로 slave/alloc 커맨드에 대한 ack를 받는 곳.
	 */
	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		IContext request = ctxTracer.getRequestContext();
		IContext response = ctxTracer.getResponseContext();
		
		if(response.getPaths().contains("ack")){
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
		}else{
			//nack로 온경우 
			//바이패스 한다. 에러메시지 및 에러코드 등이 그대로 전달. 
			
		}
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		IContext nack = ctxTracer.getResponseContext();
		if(nack==null) {
			//메시지가 없는 경우는 ContextTracer에서 자동으로 제거된 경우 
			return;
		}
		
	}

}

package com.hdbsnc.smartiot.service.master.impl.process;

import java.io.IOException;
import java.util.List;

import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler.impl.RootHandler;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParseException;
import com.hdbsnc.smartiot.common.otp.url.parser.UrlParser;
import com.hdbsnc.smartiot.common.otp.url.parser.vo.Url;
import com.hdbsnc.smartiot.service.master.slavemanager.Server;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

@Deprecated
public class MasterContextProcessor2 implements IContextProcessor{

	private RootHandler root;
	private ServicePool pool;
	private SlaveServerManager ssm;
	private IIntegratedSessionManager ism;
	private IAdapterInstanceManager aim;
	private UrlParser parser;
	
	public MasterContextProcessor2(ServicePool pool, SlaveServerManager ssm, IIntegratedSessionManager ism, IAdapterInstanceManager aim){
		root = new RootHandler();
		this.pool = pool;
		this.ssm = ssm;
		parser = UrlParser.getInstance();
		this.ism = ism;
		this.aim = aim;
	}
	
	public RootHandler getRootHandler(){
		return root;
	}

	@Override
	public void parallelProcess(IContext inboundCtx) throws Exception {
		this.pool.execute(new Worker(inboundCtx));
		
	}
	
	@Override
	public void process(IContext inboundCtx) throws Exception {
		new Worker(inboundCtx).run();
		
	}

	private class Worker implements Runnable{

		private IContext ctx;
		
		Worker(IContext ctx){
			this.ctx = ctx;
		}
		
		@Override
		public void run() {
			String transType = ctx.getTransmission();
			if(transType==null || transType.equals("")) transType = IContext.TRANSMISSION_REQUEST;
			switch(transType){
			case IContext.TRANSMISSION_REQUEST:
			case IContext.TRANSMISSION_REQUEST1:
				try {
					request(ctx);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case IContext.TRANSMISSION_RESPONSE:
			case IContext.TRANSMISSION_RESPONSE1:
				try {
					response(ctx);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case IContext.TRANSMISSION_EVENT:
			case IContext.TRANSMISSION_EVENT1:
				try {
					event(ctx);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
				default:
					//프로토콜에 정의되지 않은 TRANSMISSION타입으로 왔으므로 에러메시지. 
					break;
			}
		}
		
		/**
		 * 마스터서버로 올 수 있는 request 유형들
		 * 1. 내부에서 요청. TID를 확인해서 해당 슬래이브서버로 보내야한다.
		 * 2. 슬래이브서버에서 요청. TID가 내부에서 처리할 수 없으므로 바이패스. 다른 슬래이브서버로 보내야한다.
		 * 3. 슬래이브서버에서 요청. TID가 내부에 존재하므로 AIM으로 보낸다.
		 * 4. 마스터서버의 특정 기능 수행을 요청. process를 타면 된다.
		 * @param ctx
		 * @throws Exception
		 */
		private void request(IContext ctx) throws Exception{
			String sid = ctx.getSID();
			String tid = ctx.getTID();
			
			Server requestServer = ssm.getServer(sid);
			if(requestServer==null){
				if(tid==null || tid.equals("this") || tid.equals(ssm.getMasterServerId())){
					process(ctx);
				}else{
					//내부의 요청. 
					//TID가 존재하는 슬래이브 서버를 찾아서 전달. 
					requestToSlaveServer(ctx);
				}
			}else{
				//슬래이브에서 온 요청. 
				if(tid!=null && (tid.equals("this") || tid.equals(ssm.getMasterServerId())) ){
					//마스터 서버의 프로세스를 탄다.
					process(ctx);
				}else{
					if(ism.containsDeviceId(tid)){
						//내부에서 처리해야 한다.
						requestToAim(ctx);
					}else{
						//다른 슬래이브 서버로 전달해야 한다.
						requestToSlaveServer(ctx);
					}
				}
			}
		}
		
		/**
		 * 마스터서버로 올 수 있는 response 유형들 
		 * 1. 내부에서 온 응답. 슬래이브서버로 보내야 한다.
		 * 2. 슬래이브서버에서 온 응답. TID가 내부에 존재하면 내부에서 처리.
		 * 3. 슬래이브서버에서 온 응답. TID가 존재하는 슬래이브 서버를 찾아서 전달.
		 * 4. 마스터서버의 요청에 의한 응답. 내부 contextTracer 를 호출. 
		 * @param ctx
		 * @throws Exception
		 */
		private void response(IContext ctx) throws Exception{
			String sid = ctx.getSID();
			String tid = ctx.getTID();
			Server server = ssm.getServerByServerSessionKey(sid);
			if(server==null){
				//내부에서 온 응답. 슬래이브서버로 보내자.
				responseToSlaveServer(ctx);
			}else{
				//슬래이브에서 온 응답. 
				String seq = ctx.getSPort();
				if(tid!=null && (tid.equals("this") || tid.equals(ssm.getMasterServerId())) ){
					//마스터에서 보낸 요청에 대항 응답이 왔다.
					if(seq==null || seq.equals("")){
						process(ctx);//시퀀스가 없으므로 그대로 핸들러에서 처리되도록 한다.
					}else{
						server.pollAndCallContextTracer(seq, ctx);
					}
				}else{
					//TID가 별도로 존재한다.
					if(ism.containsDeviceId(tid)){
						//내부로 전달한다.
						server.pollAndCallContextTracer(seq, ctx);
						//ism에 parent가 존재하면 자동으로 해당 contextTracer가 호출이된다.
					}else{
						//다른 슬래이브 서버로 전달한다.
						responseToSlaveServer(ctx);
					}
				}
			}
		}
		
		/**
		 * 마스터서버로 올 수 있는 event 유형들 
		 * 1. 내부에서 슬래이브로 보내는 이벤트.
		 * 2. 슬래이브서버에서 마스터로 보내는 이벤트.
		 * 3. 마스터서버에서 슬래이브로 보내는 이벤트.
		 * 4. 슬래이브서버에서 슬래이브서버로 전달되는 이벤트.
		 * @param ctx
		 * @throws Exception
		 */
		private void event(IContext ctx) throws Exception{
			String sid = ctx.getSID();
			String tid = ctx.getTID();
			Server server = ssm.getServerByServerSessionKey(sid);
			if(server==null){
				//내부에서 온 이벤트. 
				eventToSlaveServer(ctx);
			}else{ 
				//슬래이브에서 온 이벤트. 
				if(tid!=null && (tid.equals("this") || tid.equals(ssm.getMasterServerId())) ){
					//마스터 서버에 보내온 이벤트.
					eventToMasterServer(ctx);
				}else{
					if(ism.containsDeviceId(tid)){
						eventToAim(ctx);
					}else{
						//다른 슬래이브서버로 전달해야 한다. 
						eventToSlaveServer(ctx);
					}
				}
			}
		}
		
		/**
		 * 마스터서버의 핸들러를 호출한다. 
		 * @param ctx
		 * @throws Exception
		 */
		private void process(IContext ctx) throws Exception{
			List<String> pathList = ctx.getPaths();
			IElementHandler currHandler = root;			
			int type;
			for(String path: pathList){
				type = currHandler.type();
				if( type==IElementHandler.ROOT || ((type & IElementHandler.DIRECTORY)==IElementHandler.DIRECTORY) ){
					currHandler = ((IDirectoryHandler) currHandler).getHandler(path);
					if(currHandler!=null) continue;
				}
				throw new Exception("핸들러가 없습니다.(path:"+ctx.getFullPath()+")");
			}
			if( (currHandler.type() & IElementHandler.FUNCTION)==IElementHandler.FUNCTION){
				((IFunctionHandler) currHandler).process(ctx);
			}else{
				//경로는 일치했으나 처리할 function이 구현되어있지 않는 케이스.
				throw new Exception("핸들러 경로는 일치하나 Function이 없습니다.(path:"+ctx.getFullPath()+")");
			}
		}
		
		
		private void responseToSlaveServer(IContext ctx) throws Exception{
			String slaveSid = ctx.getSID();
			Server selfServer = ssm.getServerByServerSessionKey(slaveSid);
			selfServer.pollAndCallContextTracer(ctx.getSPort(), ctx);
		}
		
		private void requestToAim(IContext ctx) throws Exception{
			aim.handOverContext(ctx, new IContextCallback(){
				@Override
				public void responseSuccess(IContextTracer ctxTracer) {
					IContext inboundCtx = ctxTracer.getRequestContext();
					IContext outboundCtx = ctxTracer.getResponseContext();
					String inSid = inboundCtx.getSID();
					// inbound SID가 슬래이브 서버인지만 체크하면 될듯.
					Server inboundServer = ssm.getServerByServerSessionKey(inSid);
					if(inboundServer==null){
						//외부에서 온 요청이 아니므로 처리할 필요 없음.
						return;
					}
					Url responseUrl = Url.createOtp(outboundCtx.getPaths(), outboundCtx.getParams());
					responseUrl.setHostInfo(inboundCtx.getTID(), inboundCtx.getTPort());
					responseUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
					responseUrl.addFrag("trans", "res");
					if(outboundCtx.containsContent()){
						responseUrl.addFrag("cont",outboundCtx.getContentType());
					}
					try {
						inboundServer.getConnection().write(parser.parse(responseUrl).getBytes());
						if(outboundCtx.containsContent()){
							inboundServer.getConnection().write(outboundCtx.getContent().array());
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (UrlParseException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void responseFail(IContextTracer ctxTracer) {
					
				}
				});
		}
		
		private void requestToSlaveServer(IContext ctx) throws Exception{
			String tid = ctx.getTID();
			Server targetServer = ssm.getServerByDeviceId(tid);
			if(targetServer==null){
				throw new Exception("tid("+tid+")를 처리할 서버가 없음.");
			}
			Url url = Url.createOtp(ctx.getPaths(), ctx.getParams());
			url.setHostInfo(tid, null);
			if(ctx.containsContent()){
				url.addFrag("cont", ctx.getContentType());
			}
			url.setUserInfo(targetServer.getSessionKey(), targetServer.offerContextTracer(ctx, new IContextCallback(){
				@Override
				public void responseSuccess(IContextTracer ctxTracer) {
					IContext inboundCtx = ctxTracer.getRequestContext();
					IContext outboundCtx = ctxTracer.getResponseContext();
					String inSid = inboundCtx.getSID();
					
					//내부요청은 tracer의 parent로 ism에서 자동으로 해결. 외부 슬래이브서버의 요청인지만 체크하면 된다.
					// inbound SID가 슬래이브 서버인지만 체크하면 될듯.
					Server inboundServer = ssm.getServerByServerSessionKey(inSid);
					if(inboundServer==null){
						//외부에서 온 요청이 아니므로 처리할 필요 없음.
						return;
					}
					Url responseUrl = Url.createOtp(outboundCtx.getPaths(), outboundCtx.getParams());
					responseUrl.setHostInfo(inboundCtx.getTID(), inboundCtx.getTPort());
					responseUrl.setUserInfo(inboundCtx.getSID(), inboundCtx.getSPort());
					responseUrl.addFrag("trans", "res");
					if(outboundCtx.containsContent()){
						responseUrl.addFrag("cont",outboundCtx.getContentType());
					}
					try {
						inboundServer.getConnection().write(parser.parse(responseUrl).getBytes());
						if(outboundCtx.containsContent()){
							inboundServer.getConnection().write(outboundCtx.getContent().array());
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (UrlParseException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void responseFail(IContextTracer ctxTracer) {
					
				}
				}).getSeq());

			targetServer.getConnection().write(parser.parse(url).getBytes());
			if(ctx.containsContent()){
				targetServer.getConnection().write(ctx.getContent().array());
			}

		}
		
		private void eventToSlaveServer(IContext ctx) throws Exception{
			String tid = ctx.getTID();
			Server targetServer = ssm.getServerByDeviceId(tid);
			Url url = Url.createOtp(ctx.getPaths(), ctx.getParams());
			url.setHostInfo(tid, null);
			url.setUserInfo(targetServer.getSessionKey(), null);
			url.addFrag("trans", "evt");
			if(ctx.containsContent()){
				url.addFrag("cont", ctx.getContentType());
			}
			try {
				targetServer.getConnection().write(parser.parse(url).getBytes());
				if(ctx.containsContent()){
					targetServer.getConnection().write(ctx.getContent().array());
				}
			} catch (IOException | UrlParseException e) {
				e.printStackTrace();
			}
		}
		
		private void eventToAim(IContext ctx) throws Exception{
			aim.handOverContext(ctx, null);//이벤트는 응답이 없다. 
		}
		
		private void eventToMasterServer(IContext ctx) throws Exception{
			//마스터 서버에서 수신 받을 이벤트는 현재 정의되지 않았다. 
			process(ctx);//이벤트가 정의되어 있다면 여기서 처리될 것이다. 
		}
	}


	
	
}

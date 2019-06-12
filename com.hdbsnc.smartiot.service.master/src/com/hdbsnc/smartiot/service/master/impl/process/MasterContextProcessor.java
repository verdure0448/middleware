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
public class MasterContextProcessor implements IContextProcessor{

	private RootHandler root;
	private ServicePool pool;
	private SlaveServerManager ssm;
	private IIntegratedSessionManager ism;
	private IAdapterInstanceManager aim;
	private UrlParser parser;
	
	public MasterContextProcessor(ServicePool pool, SlaveServerManager ssm, IIntegratedSessionManager ism, IAdapterInstanceManager aim){
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
			List<String> pathList = ctx.getPaths();
			IElementHandler currHandler = root;
			IElementHandler tempHandler = null;
			IDirectoryHandler currDir;
			for(String path: pathList){
				if((currHandler.type() & IElementHandler.DIRECTORY) == IElementHandler.DIRECTORY){
					currDir = (IDirectoryHandler) currHandler;
					tempHandler = currDir.getHandler(path);
					if(tempHandler!=null){
						currHandler = tempHandler;
						continue;
					}else{
						/** 
						 * 핸들러가 존재하지 않는다.
						 * 1. 여기는 MSS의 ContextProcessor이므로 결국 외부로 가야할 것들이므로 TID를 가지고 있는 서버를 찾아서 바이패스한다.
						 * 2. TID를 가지고 있는 슬래이브 서버가 존재하지 않으면? 해당 ContextTracer를 찾아서 캔슬해준다. 자동으로 fail메소드 호출.
						 * 3. 외부에서 들어온것들은 어떻게 구분하는가? 외부에서 들어온것들은 처리를 어떤 방식으로...
						 */
						// request sample: 	otp://sid-xxxxxxxxxxxxxxxxxxxxxxxxxxxx:100@com.hdbsnc.adtg.101/actuator?update=on    => 생략했으므로 #trans:request
						
						// response sample: otp://sid-xxxxxxxxxxxxxxxxxxxxxxxxxxxx:100@this/acutuator/ack#trans:response
						String sid = ctx.getSID();
						String tid = ctx.getTID(); //tid가 "this"이면 안된다.
						String trans = ctx.getTransmission();
						if(trans==null||trans.equals("")){
							trans = IContext.TRANSMISSION_REQUEST;
						}
						Server requestServer = ssm.getServerByServerSessionKey(sid);
						if(requestServer==null){
							//아답터로 부터 온 Context
							//내부적으로 처리가 되지 않는 건이라 여기까지 왔음. 
							switch(trans){
							case IContext.TRANSMISSION_REQUEST:
							case IContext.TRANSMISSION_REQUEST1:
								//TID를 보고 해당 서버로 보내야 한다. 해당 서버에서 SEQ 생성.
								reqeustToSlaveServer(tid);
								break;
							case IContext.TRANSMISSION_RESPONSE:
							case IContext.TRANSMISSION_RESPONSE1:
								//해당 서버의 ContextTracer를 찾아서 콜해줘야 한다.
								responseToSlaveServer();
								break;
							case IContext.TRANSMISSION_EVENT:
							case IContext.TRANSMISSION_EVENT1:
								//TID에게 이벤트를 전송한다.
								//시퀀스 없이 그냥 목적지로 전송하면 된다.
								eventToSlaveServer(tid);
								break;
							default:
								//다른 TRANSMISSION 타입으로 왔으므로 잘못된 포멧이거나 구버젼일 수 있음. 	
								break;
							}
							
						}else{
							//슬래이브서버로 부터 온 Context
							
							switch(trans){
							case IContext.TRANSMISSION_REQUEST:
							case IContext.TRANSMISSION_REQUEST1:
								//TID를 보고 해당 서버로 보내야 한다. SEQ번호가 있으므로 ContextTracer를 등록 후 응답시 유의할 것! 
								//TID가 다른 슬래이브 서버의 자식 장치ID이면 정상적이나, 현재 마스터서버의 자식이라면 어떻게?
								//TID가 마스터서버 ISM에서 처리해야 하는 것인지? 아니면 슬래이브 서버에게 전달해야 할 것인지 판단해야 한다.
								if(ism.containsDeviceId(tid)){
									// 마스터서버 내부 아답터가 처리해야할 건.
									requestToAim();
								}else{
									// 슬래이브서버를 찾아서 전달해야 하는 건. 
									reqeustToSlaveServer(tid);
								}
								break;
							case IContext.TRANSMISSION_RESPONSE:
							case IContext.TRANSMISSION_RESPONSE1:
								//마스터 내부 요청이든 슬래이브 요청이든 request자체는 해당 슬래이브서버로 나갔으므로 일반케이스와 동일.
								//요청한 Context를 찾아 ContextTracer를 콜 해줘야 한다.
								responseToSlaveServer();
								break;
							case IContext.TRANSMISSION_EVENT:
							case IContext.TRANSMISSION_EVENT1:
								//TID에게 이벤트를 전송한다.
								//tid가 내부에 있으면 AIM으로, 그렇지 않으면 슬래이브로 전송
								if(ism.containsDeviceId(tid)){
									//내부에 있으므로 aim에서 처리.
									try {
										//이벤트이므로 따로 응답 받을 것이 없다. 
										aim.handOverContext(ctx, null);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									//외부에 있으므로 슬래이브로 전달.
									eventToSlaveServer(tid);
								}
								break;
							default:
								//다른 TRANSMISSION 타입으로 왔으므로 잘못된 포멧이거나 구버젼일 수 있음.
								break;
							}
						}
						return;
					}
				}
			}
			if( (currHandler.type() & IElementHandler.FUNCTION) == IElementHandler.FUNCTION){
				try {
					((IFunctionHandler) currHandler).process(ctx);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}else{
				//경로는 일치했으나 처리한 function이 구현되어 있지 않아 아무것도 하지 않는 케이스.
				//에러 메시지를 보내주자.
			}
			
		}
		
		private void responseToSlaveServer(){
			String slaveSid = ctx.getSID();
			Server selfServer = ssm.getServerByServerSessionKey(slaveSid);
			selfServer.pollAndCallContextTracer(ctx.getSPort(), ctx);
		}
		
		private void requestToAim(){
			try {
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void reqeustToSlaveServer(String tid){
			Server targetServer = ssm.getServerByDeviceId(tid);
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
			try {
				targetServer.getConnection().write(parser.parse(url).getBytes());
				if(ctx.containsContent()){
					targetServer.getConnection().write(ctx.getContent().array());
				}
			} catch (IOException | UrlParseException e) {
				e.printStackTrace();
			}
		}
		
		private void eventToSlaveServer(String tid){
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
	}


	
	
}

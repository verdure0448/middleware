package com.hdbsnc.smartiot.service.auth.impl.process;

import java.util.List;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.context.handler.IDirectoryHandler;
import com.hdbsnc.smartiot.common.context.handler.IElementHandler;
import com.hdbsnc.smartiot.common.context.handler.IFunctionHandler;
import com.hdbsnc.smartiot.common.context.handler.impl.RootHandler;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class AuthContextProcessor implements IContextProcessor{

	private ServicePool pool;
	private RootHandler root;
	
	public AuthContextProcessor(ServicePool pool){
		this.pool = pool;
		this.root = new RootHandler();
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
		
		public void run(){
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
		 * 인증서버로 오는 request는 
		 * 1. auth/open
		 * 2. 추가 할 것. 
		 * @param ctx
		 * @throws Exception
		 */
		private void request(IContext ctx) throws Exception{
			process(ctx);
		}
		
		private void response(IContext ctx) throws Exception{
			process(ctx);
		}
		
		private void event(IContext ctx) throws Exception{
			process(ctx);
		}
		
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
	}

}

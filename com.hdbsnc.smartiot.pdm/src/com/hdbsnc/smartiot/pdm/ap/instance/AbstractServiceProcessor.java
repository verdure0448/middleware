package com.hdbsnc.smartiot.pdm.ap.instance;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEventListener;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerApplicationException;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerIOException;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerNotFoundException;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerProcessException;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnSupportedMethodException;
import com.hdbsnc.smartiot.common.context.handler.exception.ContextHandlerUnimplementedFunctionException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNotFoundException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementNullOrEmptyPathException;
import com.hdbsnc.smartiot.common.context.handler.exception.ElementUnSupportedInstanceException;
import com.hdbsnc.smartiot.common.context.handler2.OutboundContext;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.exception.ApplicationException;
import com.hdbsnc.smartiot.common.exception.SystemException;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.AuthCertificationException;
import com.hdbsnc.smartiot.util.logger.Log;

public abstract class AbstractServiceProcessor implements IContextProcessor{
	protected ICommonService service;
	private RootHandler root;
	private List<IAdapterProcessorEventListener> eventUpdateList;
	private String iid;
	private Log log;
	
	public static final int INBOUND_TRANSFERTYPE_REQUEST = 1;
	public static final int INBOUND_TRANSFERTYPE_RESPONSE = 2;
	public static final int INBOUND_TRANSFERTYPE_EVENT = 3;
	
//	public static final int TRANSFERTYPE_REQUEST_INBOUND = 1;	//여기서 처리해야할 request
//	public static final int TRANSFERTYPE_REQUEST_OUTBOUND = 2; 	//다른곳으로 전달해야할 request
//	public static final int TRANSFERTYPE_RESPONSE_INBOUND = 3;
//	public static final int TRANSFERTYPE_RESPONSE_OUTBOUND = 4;
//	public static final int TRANSFERTYPE_EVENT_INBOUND = 5;
//	public static final int TRANSFERTYPE_EVENT_OUTBOUND = 6;
	
	AbstractServiceProcessor(ICommonService service, String iid){
		this.service = service;
		this.iid = iid;
		this.log = service.getLogger().logger(iid);
		this.root = new RootHandler(service, null);
		this.eventUpdateList = new ArrayList<IAdapterProcessorEventListener>();
	}
	
	private IIntegratedSessionManager ism;
	public AbstractServiceProcessor(ICommonService service, String iid, IIntegratedSessionManager ism){
		this(service, iid);
		this.ism = ism;
	}
	
	public IIntegratedSessionManager getISM(){
		return this.ism;
	}
	
	public RootHandler getRootHandler(){
		return root;
	}
	
	@Override
	public void parallelProcess(IContext inboundCtx) throws Exception {
		this.service.getServicePool().execute(new Worker(inboundCtx));
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
					log.err(e1);
					break;
				}
				break;
			case IContext.TRANSMISSION_RESPONSE:
			case IContext.TRANSMISSION_RESPONSE1:
				try {
					response(ctx);
				} catch (Exception e1) {
					log.err(e1);
					break;
				}
				break;
			case IContext.TRANSMISSION_EVENT:
			case IContext.TRANSMISSION_EVENT1:
				try {
					event(ctx);
				} catch (Exception e1) {
					log.err(e1);
					break;
				}
				
				default:
					
					break;
			}
		}
	}
	
//	protected abstract void request(IContext ctx) throws Exception;
//	
//	protected abstract void response(IContext ctx) throws Exception;
//	
//	protected abstract void event(IContext ctx) throws Exception;
	
	private void request(IContext inCtx) throws Exception{		
		OutboundContext outCtx = new OutboundContext(inCtx);
		try{
			request(inCtx, outCtx);	
			if(outCtx.isDisposed()) {
				outCtx = null;
				return;
			}
			
		}catch(ContextHandlerNotFoundException | 
				ContextHandlerUnSupportedMethodException | 
				ContextHandlerUnimplementedFunctionException | 
				ContextHandlerProcessException | 
				ContextHandlerIOException |
				ElementNullOrEmptyPathException |
				ElementNotFoundException |
				ElementUnSupportedInstanceException |
				ContextHandlerApplicationException |
				ApplicationException |
				SystemException |
				AuthCertificationException e){
			outCtx.setTID("this");
			outCtx.getPaths().add("nack");
			outCtx.getParams().put("code", e.getCode());
			outCtx.getParams().put("type", e.getType());
			outCtx.getParams().put("msg", e.getMessage());
			outCtx.setTransmission("res");
		}
		transferContext(INBOUND_TRANSFERTYPE_REQUEST, inCtx, outCtx);
	}
	
	private void response(IContext inCtx) throws Exception{
		OutboundContext outCtx = new OutboundContext(inCtx);
		try{
			response(inCtx, outCtx);
			if(outCtx.isDisposed()) {
				outCtx = null;
				return;
			}
			
		}catch(ContextHandlerNotFoundException | 
				ContextHandlerUnSupportedMethodException | 
				ContextHandlerUnimplementedFunctionException | 
				ContextHandlerProcessException | 
				ContextHandlerIOException |
				ElementNullOrEmptyPathException |
				ElementNotFoundException |
				ElementUnSupportedInstanceException |
				ContextHandlerApplicationException |
				ApplicationException |
				SystemException |
				AuthCertificationException e){
			outCtx.getPaths().add("nack");
			outCtx.getParams().put("code", e.getCode());
			outCtx.getParams().put("type", e.getType());
			outCtx.getParams().put("msg", e.getMessage());
			outCtx.setTransmission("res");
		}
		transferContext(INBOUND_TRANSFERTYPE_RESPONSE, inCtx, outCtx);
	}
	
	private void event(IContext inCtx) throws Exception{
		OutboundContext outCtx = new OutboundContext(inCtx);
		try{
			event(inCtx, outCtx);	
			if(outCtx.isDisposed()) {
				outCtx = null;
				return;
			}
			
		}catch(ContextHandlerNotFoundException | 
				ContextHandlerUnSupportedMethodException | 
				ContextHandlerUnimplementedFunctionException | 
				ContextHandlerProcessException | 
				ContextHandlerIOException |
				ElementNullOrEmptyPathException |
				ElementNotFoundException |
				ElementUnSupportedInstanceException |
				ContextHandlerApplicationException |
				ApplicationException |
				SystemException |
				AuthCertificationException e){
			outCtx.getPaths().add("nack");
			outCtx.getParams().put("code", e.getCode());
			outCtx.getParams().put("type", e.getType());
			outCtx.getParams().put("msg", e.getMessage());
			outCtx.setTransmission("res");
		}
		transferContext(INBOUND_TRANSFERTYPE_EVENT, inCtx, outCtx);
	}
	
	private void transferContext(int inboundCtxTransferType, IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		if(outboundCtx.isDisposed()) return;
		String sid = outboundCtx.getSID();
		String tid = outboundCtx.getTID();
		switch(inboundCtxTransferType){
		case INBOUND_TRANSFERTYPE_REQUEST:
			
//			if((tid!=null && tid.equals("this")) && (sid==null || !sid.startsWith("sid-"))){
//				ism.pollAndCallContextTracer(inboundCtx, outboundCtx);
//				break;
//			}
			try{
				transfer(outboundCtx);
			}catch(SystemException e){
				log.err(e);
				ism.pollAndCallContextTracer(inboundCtx, outboundCtx);
			}
			break;
		case INBOUND_TRANSFERTYPE_RESPONSE:
			outboundCtx.setTransmission("res");
			transfer(outboundCtx);
			break;
		case INBOUND_TRANSFERTYPE_EVENT:
			outboundCtx.setTransmission("evt");
			transfer(outboundCtx);
			break;
		}
		
	}
	
	protected abstract void transfer(OutboundContext outboundCtx) throws Exception;
	
	protected abstract void request(IContext inboundCtx, OutboundContext outboundCtx) throws Exception;
	
	protected abstract void response(IContext inboundCtx, OutboundContext outboundCtx) throws Exception;
	
	protected abstract void event(IContext inboundCtx, OutboundContext outboundCtx) throws Exception;

	

	
	
}

package com.hdbsnc.smartiot.pdm.ap.instance;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEventListener;
import com.hdbsnc.smartiot.common.context.IContext;
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
import com.hdbsnc.smartiot.common.em.EventProducerDisposedException;
import com.hdbsnc.smartiot.common.em.IEventProducer;
import com.hdbsnc.smartiot.common.exception.ApplicationException;
import com.hdbsnc.smartiot.common.exception.SystemException;
import com.hdbsnc.smartiot.common.ism.sm.AuthCertificationException;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;

@Deprecated
public abstract class AbstractAdapterProcessor implements IAdapterProcessor{
	protected ICommonService service;
	private RootHandler root;
	private List<IAdapterProcessorEventListener> eventUpdateList;
	protected IAdapterInstanceManager aim;
	private String iid;
	
	public AbstractAdapterProcessor(ICommonService service, IAdapterContext ctx){
		this.iid = ctx.getAdapterInstanceInfo().getInsId();
		this.service = service;
		this.aim = ctx.getAdapterInstanceManager();
		this.root = new RootHandler(service, ctx.getSessionManager());
		this.eventUpdateList = new ArrayList<IAdapterProcessorEventListener>();
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
				updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_REQUEST, IAdapterProcessorEvent.STATE_BEGIN, ctx));
				try {
					request(ctx);
				} catch (Exception e1) {
					updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_REQUEST, IAdapterProcessorEvent.STATE_ERROR, ctx, e1));
					break;
				}
				updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_REQUEST, IAdapterProcessorEvent.STATE_SUCCESS, ctx));
				break;
			case IContext.TRANSMISSION_RESPONSE:
			case IContext.TRANSMISSION_RESPONSE1:
				updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_RESPONSE, IAdapterProcessorEvent.STATE_BEGIN, ctx));
				try {
					response(ctx);
				} catch (Exception e1) {
					updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_RESPONSE, IAdapterProcessorEvent.STATE_ERROR,ctx, e1));
					break;
				}
				updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_RESPONSE, IAdapterProcessorEvent.STATE_SUCCESS, ctx));
				break;
			case IContext.TRANSMISSION_EVENT:
			case IContext.TRANSMISSION_EVENT1:
				updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_EVENT, IAdapterProcessorEvent.STATE_BEGIN, ctx));
				try {
					event(ctx);
				} catch (Exception e1) {
					updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_EVENT, IAdapterProcessorEvent.STATE_ERROR,ctx, e1));
					break;
				}
				updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_EVENT, IAdapterProcessorEvent.STATE_SUCCESS, ctx));
				break;
				default:
					//프로토콜에 정의되지 않은 TRANSMISSION타입으로 왔으므로 에러메시지. 
					updateEvent(new AdapterProcessorEvent(iid, 0, IAdapterProcessorEvent.STATE_ERROR));
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
		if(outCtx.isDisposed()) outCtx = null;
		transferContext(1, inCtx, outCtx);
	}
	
	private void response(IContext inCtx) throws Exception{
		OutboundContext outCtx = new OutboundContext(inCtx);
		try{
			response(inCtx, outCtx);	
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
		if(outCtx.isDisposed()) outCtx = null;
		transferContext(2, inCtx, outCtx);
	}
	
	private void event(IContext inCtx) throws Exception{
		OutboundContext outCtx = new OutboundContext(inCtx);
		try{
			event(inCtx, outCtx);	
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
		if(outCtx.isDisposed()) outCtx = null;
		transferContext(3, inCtx, outCtx);
	}
	
	private void transferContext(int transmissionType, IContext inboundCtx, IContext outboundCtx) throws Exception{
		transfer(inboundCtx, outboundCtx);
		switch(transmissionType){
		case 1:
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_REQUEST, IAdapterProcessorEvent.STATE_INBOUND_TRANSFER, outboundCtx));
			break;
		case 2:
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_RESPONSE, IAdapterProcessorEvent.STATE_INBOUND_TRANSFER, outboundCtx));
			break;
		case 3:
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_EVENT, IAdapterProcessorEvent.STATE_INBOUND_TRANSFER, outboundCtx));
			break;
		}
	}
	
	protected abstract void transfer(IContext inboundCtx, IContext outboundCtx) throws Exception;
	
	protected abstract void request(IContext inboundCtx, OutboundContext outboundCtx) throws Exception;
	
	protected abstract void response(IContext inboundCtx, OutboundContext outboundCtx) throws Exception;
	
	protected abstract void event(IContext inboundCtx, OutboundContext outboundCtx) throws Exception;
	
	protected List<IAdapterProcessorEventListener> getEventUpdateList(){
		return this.eventUpdateList;
	}
	
	@Override
	public void addEventListener(IAdapterProcessorEventListener listener) {
		this.eventUpdateList.add(listener);
		
	}

	@Override
	public void removeEventListener(IAdapterProcessorEventListener listener) {
		this.eventUpdateList.remove(listener);
	}

	@Override
	public void removeAllEventListener() {
		this.eventUpdateList.clear();
	}

	@Override
	public void updateEvent(final IAdapterProcessorEvent e) {
		try{
			if(e instanceof AdapterProcessorEvent){
				((IEventProducer)aim).pushEvent((AdapterProcessorEvent) e);
			}else{
				((IEventProducer)aim).pushEvent(new AdapterProcessorEvent(e));
			}
		}catch(EventProducerDisposedException e1){
			service.getLogger().err(e1);
		}
		
		try {
			service.getServicePool().execute(new Runnable(){

				@Override
				public void run() {
					IAdapterProcessorEventListener listener;
					for(int i=0;i<eventUpdateList.size();i++){
						listener = eventUpdateList.get(i);
						listener.onEvent(e);
					}
				}
				
			});
		} catch (AlreadyClosedException e1) {
			e1.printStackTrace();
		}
		
	}
	
	
}

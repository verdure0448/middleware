package com.hdbsnc.smartiot.pdm.ap.instance;

import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEventListener;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
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
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.AuthCertificationException;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.ism.sm.IUserProfile;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IMsgMastObj;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;

public abstract class AbstractGscpInstanceProcessor implements IAdapterProcessor, IContextCallback{
	private ICommonService service;
	private RootHandler root;
	private List<IAdapterProcessorEventListener> eventUpdateList;
	private IAdapterInstanceManager aim;
	protected ISessionManager sm;
	private String iid;
	private IInstanceObj instanceInfo;
	protected Log log;
	protected IAdapterInstanceContainer aic;
	private IAdapterProcessorEvent lastEvent = null;
	
	
	public AbstractGscpInstanceProcessor(ICommonService service, IAdapterContext ctx){
		this.service = service;
		this.instanceInfo = ctx.getAdapterInstanceInfo();
		this.log = service.getLogger().logger(instanceInfo.getInsId());
		this.iid = instanceInfo.getInsId();
		this.aim = ctx.getAdapterInstanceManager();
		this.sm = ctx.getSessionManager();
		this.aic = ctx.getAdapterInstanceContainer();
		this.root = new RootHandler(service, sm);
		this.eventUpdateList = new ArrayList<IAdapterProcessorEventListener>();
	}
	
	public IAdapterProcessorEvent getLastEvent(){
		return lastEvent;
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
					log.err(e1);
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
					log.err(e1);
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
					log.err(e1);
					break;
				}
				updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_EVENT, IAdapterProcessorEvent.STATE_SUCCESS, ctx));
				break;
				default:
					//프로토콜에 정의되지 않은 TRANSMISSION타입으로 왔으므로 에러메시지. 
					updateEvent(new AdapterProcessorEvent(iid, 0, IAdapterProcessorEvent.STATE_ERROR));
					log.info("프로토콜에 정의되지 않은 TRANSMISSION 타입이므로 처리 불가.");
					break;
			}
		}
	}
	private static final int REQUEST_INNERTRANSFER = 1;
	private static final int REQUEST_OUTTERTRANSFER = 4;
	private void request(IContext inCtx) throws Exception{
		OutboundContext outCtx = new OutboundContext(inCtx);
		String tid = inCtx.getTID();
		String fullPath = outCtx.getFullPath();
		if (tid==null || tid.equals("") || "this".equals(tid) || sm.containsDeviceId(tid) || outCtx.getFullPath().equals(fullPath)) {
			try{
				if(!checkUserFilter(inCtx)) throw service.getExceptionfactory().createSysException("106");
				IAdapterInstanceEvent evt = aic.getLastEvent();
				if(evt==null) throw service.getExceptionfactory().createSysException("107", new String[]{"none", "none"});
				int evtType = evt.getEventType();
				int evtState = evt.getStateType();
				if(!(evtType==IAdapterInstanceEvent.START_EVENT && (evtState==IAdapterInstanceEvent.COMPLETED_STATE || evtState==IAdapterInstanceEvent.END_STATE))){
					throw service.getExceptionfactory().createSysException("107", new String[]{String.valueOf(evtType), String.valueOf(evtState)});
				}
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
				outCtx.getPaths().add("nack");
				outCtx.getParams().put("code", e.getCode());
				outCtx.getParams().put("type", e.getType());
				outCtx.getParams().put("msg", e.getMessage());
				outCtx.setTransmission("res");
			}
			transferContext(REQUEST_INNERTRANSFER, inCtx, outCtx);
		}else{
			try{
				if(!checkUserFilter(inCtx)) throw service.getExceptionfactory().createSysException("106");
			}catch(SystemException e){
				outCtx.getPaths().add("nack");
				outCtx.getParams().put("code", e.getCode());
				outCtx.getParams().put("type", e.getType());
				outCtx.getParams().put("msg", e.getMessage());
				outCtx.setTransmission("res");
				transferContext(REQUEST_INNERTRANSFER, inCtx, outCtx);
				return;
			}
			transferContext(REQUEST_OUTTERTRANSFER, inCtx, null);
		}
		
	}
	
	private static final int RESPONSE_INNERTRANSFER = 2;
	private static final int RESPONSE_OUTTERTRANSFER = 5;
	private void response(IContext inCtx) throws Exception{
		OutboundContext outCtx = new OutboundContext(inCtx);
		String tid = inCtx.getTID();
		String sid = inCtx.getSID();
		if (tid==null || tid.equals("") || "this".equals(tid)) {
			try{
				if(!checkUserFilter(inCtx)) {
					throw service.getExceptionfactory().createSysException("106");
				}
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
			transferContext(RESPONSE_INNERTRANSFER, inCtx, outCtx);
		}else if(sm.containsSessionKey(sid)){
			outCtx.setTransmission("res");
			transferContext(RESPONSE_INNERTRANSFER, inCtx, outCtx);
		}else{
			transferContext(RESPONSE_OUTTERTRANSFER, inCtx, null);
		}
	}
	
	private static final int EVENT_INNERTRANSFER = 3;
	private static final int EVENT_OUTTERTRANSFER = 6;
	private void event(IContext inCtx) throws Exception{
		OutboundContext outCtx = new OutboundContext(inCtx);
		String tid = inCtx.getTID();
		if (tid==null || tid.equals("") || "this".equals(tid) || sm.containsDeviceId(tid) ) {
			try{
				if(!checkUserFilter(inCtx)) throw service.getExceptionfactory().createSysException("106");
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
			transferContext(EVENT_INNERTRANSFER, inCtx, outCtx);
		}else{
			transferContext(EVENT_OUTTERTRANSFER, inCtx, null);
		}
	}
	
	private void transferContext(int transmissionType, IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		switch(transmissionType){
		case REQUEST_INNERTRANSFER: // 현재 아답터에서 처리해야할 정상정인 요청들...
			String sid = outboundCtx.getSID();
			if(sm.containsSessionKey(sid)){
				responseInnerTransfer(inboundCtx, outboundCtx);
			}else{
				responseOutterTransfer(inboundCtx, outboundCtx);
			}
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_REQUEST, IAdapterProcessorEvent.STATE_INBOUND_TRANSFER, outboundCtx));
			break;
		case REQUEST_OUTTERTRANSFER: // 현재 아답터에서 처리할 수 없는 단순히 거쳐가는 장치제어 명령들 ...
			requestOutterTransfer(inboundCtx);
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_REQUEST, IAdapterProcessorEvent.STATE_OUTBOUND_TRANSFER, inboundCtx));
			break;
		case RESPONSE_INNERTRANSFER:
			String sid2 = outboundCtx.getSID();
			if(sm.containsSessionKey(sid2)){
				responseInnerTransfer(inboundCtx, outboundCtx);
			}else{
				responseOutterTransfer(inboundCtx, outboundCtx);
			}
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_RESPONSE, IAdapterProcessorEvent.STATE_INBOUND_TRANSFER, outboundCtx));
			break;
		case RESPONSE_OUTTERTRANSFER:
			responseOutterTransfer(inboundCtx, outboundCtx);
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_RESPONSE, IAdapterProcessorEvent.STATE_OUTBOUND_TRANSFER, inboundCtx));
			break;
		case EVENT_INNERTRANSFER:
			eventInnerTransfer(outboundCtx);
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_EVENT, IAdapterProcessorEvent.STATE_INBOUND_TRANSFER, outboundCtx));
			break;
		case EVENT_OUTTERTRANSFER:
			eventOutterTransfer(inboundCtx);
			updateEvent(new AdapterProcessorEvent(iid, IAdapterProcessorEvent.TYPE_EVENT, IAdapterProcessorEvent.STATE_OUTBOUND_TRANSFER, inboundCtx));
			break;
		}
	}
	
	private void requestOutterTransfer(IContext inboundCtx) throws Exception{
		// event인경우 callback --> null
		//TODO reponse의 경우 코어(마스터,슬레이브,aim)에서 요청이 들어올 경우 이벤트핸들러 작성 필요
		aim.handOverContext(inboundCtx, this);
	}
	
//	private void requestInnerTransfer(IContext inboundCtx, IContext outboundCtx) throws Exception{
//		transfer(inboundCtx, outboundCtx);
//	}
	
	private void eventOutterTransfer(IContext inboundCtx) throws Exception{
		aim.handOverContext(inboundCtx, null);
	}
	
	private void eventInnerTransfer(IContext outboundCtx) throws Exception{
		transfer(outboundCtx);
	}
	
	private void responseOutterTransfer(IContext inboundCtx, IContext outboundCtx){
		sm.getIntegratedSessionManager().pollAndCallContextTracer(inboundCtx, outboundCtx);
	}
	
	protected void responseInnerTransfer(IContext inboundCtx, OutboundContext outboundCtx) throws Exception{
		try{
			transfer(outboundCtx);
		}catch(ClosedChannelException e){
			outboundCtx.addPath("nack");
			outboundCtx.putParam("code", "105");
			outboundCtx.putParam("msg", service.getExceptionfactory().getMsgInfo("105").getMsg());
			outboundCtx.putParam("type", service.getExceptionfactory().getMsgInfo("105").getOuterCode());
			sm.getIntegratedSessionManager().pollAndCallContextTracer(inboundCtx, outboundCtx);
			throw service.getExceptionfactory().createSysException("105");
		}catch(SystemException e){
			outboundCtx.addPath("nack");
			outboundCtx.putParam("code", e.getCode());
			outboundCtx.putParam("msg", e.getMessage());
			outboundCtx.putParam("type", e.getType());
			sm.getIntegratedSessionManager().pollAndCallContextTracer(inboundCtx, outboundCtx);
			throw e;
		}catch(Exception e){
			outboundCtx.addPath("nack");
			outboundCtx.putParam("code", "105");
			outboundCtx.putParam("msg", e.getMessage());
			outboundCtx.putParam("type", "error");
			sm.getIntegratedSessionManager().pollAndCallContextTracer(inboundCtx, outboundCtx);
			throw e;
		}
	}
	
	@Override
	public void responseSuccess(IContextTracer ctxTracer){
		IContext requestCtx = ctxTracer.getRequestContext(); //요청
		IContext responseCtx = ctxTracer.getResponseContext(); //응답
		OutboundContext tempCtx = new OutboundContext(responseCtx);
		tempCtx.setSID(requestCtx.getSID());
		tempCtx.setSPort(requestCtx.getSPort());
		tempCtx.setTID(requestCtx.getTID());
		tempCtx.setTPort(requestCtx.getTPort());
		tempCtx.setTransmission("res");
		try {
			process(tempCtx);
		} catch (Exception e) {
			e.printStackTrace();
			log.err(e);
		}
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		IMsgMastObj obj = service.getExceptionfactory().getMsgInfo("102");
		IContext requestCtx = ctxTracer.getRequestContext(); //요청
		OutboundContext tempCtx = new OutboundContext(requestCtx);
		tempCtx.getPaths().add("nack");
		tempCtx.getParams().put("code", obj.getOuterCode());
		tempCtx.getParams().put("msg", obj.getMsg());
		tempCtx.getParams().put("type", obj.getType());
		tempCtx.setTransmission("res");
		try {
			process(tempCtx);
		} catch (Exception e) {
			e.printStackTrace();
			log.err(e);
		}
	}
	
	protected abstract void transfer(IContext outboundCtx) throws Exception;
	
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
		this.lastEvent = e;
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
	
	private boolean checkUserFilter(IContext ctx){
		String sid = ctx.getSID();
		String tid = ctx.getTID();
		
		if(tid==null){
			//tid가 없는 케이스는 없다.
			return false;
		}else if(tid.equals("this")){
			//1:1 통신 구조이므로 현재서버의 아답터인스턴스 아이디가 된다.
			return checkDevicePool(sid, iid);
		}
		//장치 식별자가 있으므로 현재 세션에서 권한체크.
		
		if(sid==null){
			//sid는 무조건 있어야 한다.2016.3.7현재까지는...
			return false;
		}else if(sid.startsWith("hid-")){//host id 줄임말.
			//마스터, 슬래이브의 세션키가 들어가 있으면 해당 서버의 세션키가 맞는지 체크해야 한다.
			//서버의 세션키는 모든장치 접근이 가능한 것으로 처리.
			//단, 세션키 자체는 동일해야 한다. 일치 여부만 체크.
			return true;
		}else if(sid.startsWith("sid-")){
			//세션키가 있으므로 해당 세션을 찾아서 권한체크.
			return checkUserFilter(sid, tid, ctx.getFullPath());
		}else{
			//세션키가 아닌 장치 식별자가 들어 있는 케이스.
			//장치아답터가 무인증 모드인지 체크해야 함.
			return checkAdapteInstanceAnonymity();
		}
	}
	
	private boolean checkUserFilter(String sessionId, String tid, String fullPath){
		IIntegratedSessionManager ism = sm.getIntegratedSessionManager();
		ISession session = ism.getSessionBySessionId(sessionId);
		if(session==null) return false;
		IUserProfile userProfile = session.getUserProfile();
		List<String> filterList = userProfile.getUserFilterList();
		if(filterList.contains("*")) return true;
		List<Pattern> patternList = userProfile.getUserFilterPatternList();
		if(patternList!=null && patternList.size()!=0) return patternMatch(patternList, tid, fullPath);
		return false;
	}
	
	private boolean checkDevicePool(String sessionId, String tid){
//		ISession session = sm.getSessionBySessionKey(sessionId);
//		if(session==null){
//			return false;
//		}
//		String devPoolId = this.instanceInfo.getDevPoolId();
		return true;
	}
	
	private boolean checkAdapteInstanceAnonymity(){
		//인스턴스의 인증이 무인증(자체 인증포함) 인지 체크해서 리턴.
		//구현 요망.
		
		return true;
	}
	
	private boolean patternMatch(List<Pattern> filterList, String tid, String fullPath){
		if(!(fullPath==null || fullPath.equals("") || fullPath.equals("/"))) tid = tid + "/" + fullPath;
		Pattern p;
		for(int i=0,s=filterList.size();i<s;i++){
			p = filterList.get(i);
			if(p.matcher(tid).matches()) return true;
		}
		return false;
	}
	
	
}

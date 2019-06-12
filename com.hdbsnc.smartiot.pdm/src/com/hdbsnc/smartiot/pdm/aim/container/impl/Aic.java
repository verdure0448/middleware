package com.hdbsnc.smartiot.pdm.aim.container.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEventListener;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceHandler;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.em.EventProducerDisposedException;
import com.hdbsnc.smartiot.common.em.IEventProducer;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.pdm.aim.container.handler.impl.PdmLogAih;
import com.hdbsnc.smartiot.pdm.aim.impl.AimEvent;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Aic implements IAdapterInstanceContainer{
	private class ListenerCondition{
		private ListenerCondition(){};
		private int eventTypes;
		private int stateTypes;
		private boolean isOnce;
		private IAdapterInstanceEventListener listener;
	}
	private List<ListenerCondition> listenerList;
	public static final List<IAdapterInstanceHandler> EMPTY_HANDLERS = new ArrayList<IAdapterInstanceHandler>(0);
	
	private InnerAdapterContext ctx;
	private IAdapterInstance instance;
	private List<IAdapterInstanceHandler> beforeHandlerList = null;
	private List<IAdapterInstanceHandler> afterHandlerList = null;
	private ICommonService service;
	
	private AimEvent lastEvent;
	private IAdapterInstanceManager aim;
	private ISessionManager sm;
	private IInstanceObj info;
	private Log log;
	public Aic(IAdapterInstanceManager aim, IAdapter adapter, IInstanceObj info, Map<String, String> attInfo, ISessionManager sm, ICommonService service){
		this.aim = aim;
		this.sm = sm;
		this.info = info;
		this.ctx = this.createContext(aim, adapter, info, attInfo, sm, service);
		this.instance = adapter.getFactory(service).createInstance();
		this.beforeHandlerList = null;
		this.afterHandlerList = null;
		this.service = service;
		this.log = service.getLogger().logger(info.getInsId());
		listenerList = new ArrayList<ListenerCondition>();
		lastEvent = createEvent(IAdapterInstanceEvent.CREATE_EVENT, IAdapterInstanceEvent.CREATED_STATE, null);
	}
	
	@Override
	public IAdapterContext getContext(){
		return this.ctx;
	}
	
	@Override
	public IAdapterInstanceEvent getLastEvent() {
		return lastEvent;
	}
	
	private AimEvent createEvent(int eventType, int stateType, Exception e){
		AimEvent event = new AimEvent(eventType, stateType, ctx.getAdapterManifest(), ctx.getAdapterInstanceInfo(), e);
		return event;
	}
	
	@Override
	public void addEventListener(IAdapterInstanceEventListener listener, int eventTypes, int stateTypes, boolean isOnce) {
		synchronized(listenerList){
			ListenerCondition con = new ListenerCondition();
			con.listener = listener;
			con.eventTypes = eventTypes;
			con.stateTypes = stateTypes;
			con.isOnce = isOnce;
			listenerList.add(con);
		}
	}
	
	@Override
	public void addEventListener(IAdapterInstanceEventListener listener, int eventTypes, int stateTypes){
		addEventListener(listener, eventTypes, stateTypes, false);//isOnces는 기본은 계속해서 이벤트를 수신한다.
	}
	
	@Override
	public void addEventListener(IAdapterInstanceEventListener listener, int eventTypes) {
		addEventListener(listener, eventTypes, 0);
	}
	
	@Override
	public void addEventListener(IAdapterInstanceEventListener listener) {
		addEventListener(listener, 0, 0); //all
	}

	@Override
	public void removeEventListener(IAdapterInstanceEventListener listener) {
		synchronized(listenerList){
			ListenerCondition con;
			for(int i=0, s=listenerList.size();i<s;i++){
				con = listenerList.get(i);
				if(con.listener.equals(listener)){
					listenerList.remove(i);
					return;
				}
			}
		}
	}

	@Override
	public void removeAllEventListener() {
		synchronized(listenerList){
			listenerList.clear();
		}
	}

	@Override
	public void updateAdapterInstanceEvent(int updateEvt, int updateSvt, Exception e) {
		synchronized(listenerList){
			AimEvent newEvt = createEvent(updateEvt, updateSvt, e);
			lastEvent = newEvt;
			try {
				((IEventProducer) aim).pushEvent(newEvt);
			} catch (EventProducerDisposedException e1) {
				service.getLogger().err(e1);
			}
			int evts, svts;
			ListenerCondition con;
			Iterator<ListenerCondition> iter = listenerList.iterator();
			while(iter.hasNext()){
				con = iter.next();
				evts = con.eventTypes;
				svts = con.stateTypes;
				if((evts & updateEvt) == updateEvt && (svts & updateSvt) == updateSvt){
					if(con.isOnce || con.listener.isRemoveable()) {
						iter.remove();
					}
					con.listener.onChangeAdapterInstance(newEvt); //이벤트내부에서 삭제시 데드락이 걸림. 주의 할 것!!!!!
				}
			}
		}
	}
	
	@Override
	public IAdapterProcessor getProcessor(){
		return instance.getProcessor();
	}
	
	protected void addBeforeHandler(IAdapterInstanceHandler handler){
		if(beforeHandlerList==null || beforeHandlerList.equals(EMPTY_HANDLERS)){
			this.beforeHandlerList = new ArrayList<IAdapterInstanceHandler>();
		}
		this.beforeHandlerList.add(handler);
	}
	
	protected void removeBeforeHandler(IAdapterInstanceHandler handler){
		if(beforeHandlerList==null || beforeHandlerList.equals(EMPTY_HANDLERS)){
			this.beforeHandlerList.remove(handler);
		}
	}
	
	protected void clearBeforeHandlers(){
		if(afterHandlerList==null || afterHandlerList.equals(EMPTY_HANDLERS)){
			this.beforeHandlerList.clear();
		}
	}

	@Override
	public List<IAdapterInstanceHandler> getBeforeHandlerList() {
		return this.beforeHandlerList;
	}
	
	protected void addAfterHandler(IAdapterInstanceHandler handler){
		if(afterHandlerList==null || afterHandlerList.equals(EMPTY_HANDLERS)){
			this.afterHandlerList = new ArrayList<IAdapterInstanceHandler>();
		}
		this.afterHandlerList.add(handler);
	}
	
	protected void removeAfterHandler(IAdapterInstanceHandler handler){
		if(afterHandlerList==null || afterHandlerList.equals(EMPTY_HANDLERS)){
			this.afterHandlerList.remove(handler);
		}
	}
	
	protected void clearAfterHandlers(){
		if(afterHandlerList==null || afterHandlerList.equals(EMPTY_HANDLERS)){
			this.afterHandlerList.clear();
		}
	}

	@Override
	public List<IAdapterInstanceHandler> getAfterHandlerList() {
		return this.afterHandlerList;
	}
	
	
	private class Executor implements Runnable{
		private int innerEvt;
		private Executor(int eventType){
			this.innerEvt = eventType;
		}
		
		public void run(){
			synchronized(instance){
				try {
					//update할때 ctx에 lastEvent로 등록할 것.
					updateAdapterInstanceEvent(this.innerEvt, IAdapterInstanceEvent.BEGIN_STATE, null);
					
					int evt;
					IAdapterInstanceHandler handler;
					Iterator<IAdapterInstanceHandler> iter;
					if(beforeHandlerList!=null){
						//선행 핸들러들 호출
						iter = beforeHandlerList.iterator();
						while(iter.hasNext()){
							handler = iter.next();
							evt = handler.getEventTypes();
							if(evt==0 || (evt & innerEvt) == innerEvt){
								handler.process(ctx);
								if(handler.isOnce()) iter.remove();
							}
						}
					}
					updateAdapterInstanceEvent(this.innerEvt, IAdapterInstanceEvent.DOING_STATE, null);
					//메인 핸들러 호출
					switch(innerEvt){
					/**
					 * 최초 인스턴스 시작전에 호출됨. 
					 * dispose가 호출되지 않는한 다시 호출되는 케이스 없음.
					 * dispose가 호출되면 다시 start시에 initialize가 호출되어야 함.
					 */
					case IAdapterInstanceEvent.INITIALIZE_EVENT:
						instance.initialize(ctx);
						break;
					/**
					 * 클라이언트 아답터: 장치와 실제 연결되고, 디폴트세션 인증 정보를 가지고와서 ism인증  세션객체를 만든다.
					 * 서버 아답터: 서버 소켓을 가동하거나 장치에서 제공하는 API를 초기화시켜서 장치 연결을 기다리는 상태로 만든다.
					 */
					case IAdapterInstanceEvent.START_EVENT:
						instance.start(ctx);
						break;
					/**
					 * 세션정보 및 컨넥션 정보들은(연결을 끊지도 않는다.) 손대지 않고 외부에서 오는 inbound/oubound 만 처리하지 않는 상태. 
					 * 정확히는 inbound를 차단한 상태.
					 */
					case IAdapterInstanceEvent.SUSPEND_EVENT:
						instance.suspend(ctx);
						break;
					/**
					 * SM 에 존재하는 session을 dispose하고, CM 에 존재하는 connection을 disconnect 한 후 clear한다.
					 * Connection이 존재하면 해당 연결을 모두 끊는다. 
					 * 이 후 start시에 SM과 CM이 처음부터 다 시작할 수 있도록 한다.
					 * SM과 CM객체 자체가 사라지는 것은 아님.
					 */
					case IAdapterInstanceEvent.STOP_EVENT:
						instance.stop(ctx);
						sm.stop();
						break;
					/**
					 * 현재 인스턴스와 관련한 모든 리소스를 dispose 시켜서 재활용할 수 없도록 완전히 정리한다.
					 * 이 후 start시에는 initialize를 먼저해주어야 한다.
					 */
					case IAdapterInstanceEvent.DISPOSE_EVENT:
						instance.dispose(ctx);
						sm.dispose();
						break;
					default:
						//할게 있나???
						break;
					}
					updateAdapterInstanceEvent(this.innerEvt, IAdapterInstanceEvent.COMPLETED_STATE, null);
					if(afterHandlerList!=null){
						//후행 핸들러들 호출 
						iter = afterHandlerList.iterator();
						while(iter.hasNext()){
							handler = iter.next();
							evt = handler.getEventTypes();
							if(evt==0 || (evt & innerEvt) == innerEvt){
								handler.process(ctx);
								if(handler.isOnce()) iter.remove();
							}
						}
					}
					
					updateAdapterInstanceEvent(this.innerEvt, IAdapterInstanceEvent.END_STATE, null);
				} catch (Exception e) {
					log.err(e);
					updateAdapterInstanceEvent(this.innerEvt, IAdapterInstanceEvent.ERROR_STATE, e);
					//System.out.println(e.getMessage());
				}
			}
		}
	}
	

	@Override
	public void init() throws Exception {
		this.clearBeforeHandlers();
		this.clearAfterHandlers();
		//선행, 후행 핸들러들 초기화. 재활용할 경우 필요 할듯.
		this.addHandler(new PdmLogAih(IAdapterInstanceHandler.ALL_EVENT, IAdapterInstanceHandler.BEFORE_HANDLER)); //all
		this.addHandler(new PdmLogAih(IAdapterInstanceEvent.INITIALIZE_EVENT | IAdapterInstanceEvent.START_EVENT, IAdapterInstanceHandler.AFTER_HANDLER));
	}

	@Override
	public void asyncProcess(int eventType) {
		try {
			service.getServicePool().execute(new Executor(eventType));
		} catch (AlreadyClosedException e) {
			e.printStackTrace();
		}
	}

//	@Override
//	public void syncProcess(int eventType) {
//		new Executor(ctx, eventType).run(); //현재 쓰레드에서 그냥 실행
//	}
	
	@Override
	public void addHandler(IAdapterInstanceHandler handler){
		switch(handler.getHandlerTypes()){
		case IAdapterInstanceHandler.BEFORE_HANDLER:
			this.addBeforeHandler(handler);
			break;
		case IAdapterInstanceHandler.AFTER_HANDLER:
			this.addAfterHandler(handler);
			break;
		case IAdapterInstanceHandler.ALL_HANDLER:
			this.addBeforeHandler(handler);
			this.addAfterHandler(handler);
			break;
		default:
			
			break;
		}
	}

	@Override
	public void removeHandler(IAdapterInstanceHandler handler) {
		int handlerType = handler.getHandlerTypes();
		
		int check = handlerType & IAdapterInstanceHandler.AFTER_HANDLER;
		
		if(check == IAdapterInstanceHandler.AFTER_HANDLER){
			if(this.afterHandlerList!=null && !this.afterHandlerList.equals(EMPTY_HANDLERS)){
				this.afterHandlerList.remove(handler);
			}
		}
		
		check = handlerType & IAdapterInstanceHandler.BEFORE_HANDLER;
		if(check == IAdapterInstanceHandler.BEFORE_HANDLER){
			if(this.beforeHandlerList!=null && !this.beforeHandlerList.equals(EMPTY_HANDLERS)){
				this.beforeHandlerList.remove(handler);
			}
		}
	}

	@Override
	public void clearHandlers() {
		if(this.afterHandlerList!=null && !this.afterHandlerList.equals(EMPTY_HANDLERS)){
			this.afterHandlerList.clear();
		}
		if(this.beforeHandlerList!=null && !this.beforeHandlerList.equals(EMPTY_HANDLERS)){
			this.beforeHandlerList.clear();
		}
	}

//	private class InnerEvent implements IAdapterInstanceEvent{
//		private long updateTime;
//		private int eventType;
//		private int stateType;
//		private Exception e;
//		private IAdapterManifest manifest;
//		private IInstanceObj instanceInfo;			
//		
//		private InnerEvent(){
//			this.updateTime = System.currentTimeMillis();
//		}
//		
//		@Override
//		public long getCreatedTime() {
//			return updateTime;
//		}
//
//		@Override
//		public int getEventType() {
//			return this.eventType;
//		}
//
//		@Override
//		public int getStateType() {
//			return this.stateType;
//		}
//
//		@Override
//		public Exception getException() {
//			return this.e;
//		}
//
//		@Override
//		public IAdapterManifest getManifest() {
//			return this.manifest;
//		}
//
//		@Override
//		public IInstanceObj getInstanceInfo() {
//			return this.instanceInfo;
//		}	
//	}
	
	private InnerAdapterContext createContext(IAdapterInstanceManager aim, IAdapter adapter, IInstanceObj info, Map<String, String> attInfo, ISessionManager sm, ICommonService service){
		InnerAdapterContext ctx = new InnerAdapterContext();
		ctx.servicePool = service.getServicePool();
		ctx.logger = service.getLogger();
		ctx.instanceInfo = info;
		ctx.instanceAttInfo = attInfo;
		ctx.profileInfo = adapter.getManifest();
		ctx.aim = aim;
		ctx.sm = sm;
		ctx.container = this;
		return ctx;
	}
	
	public class InnerAdapterContext implements IAdapterContext {
		private long createdTime;
		private IInstanceObj instanceInfo;
		private Map<String, String> instanceAttInfo;
		private IAdapterManifest profileInfo;
		private IAdapterInstanceContainer container;
		private ServicePool servicePool;
		private Log logger;
		private ISessionManager sm;	
		private IAdapterInstanceManager aim;
		
		private InnerAdapterContext(){
			createdTime = System.currentTimeMillis();
		}
		
		@Override
		public long getCreatedTime() {
			return createdTime;
		}

		@Override
		public IInstanceObj getAdapterInstanceInfo() {
			return instanceInfo;
		}

		@Override
		public IAdapterManifest getAdapterManifest() {
			return profileInfo;
		}

		@Override
		public IAdapterInstanceContainer getAdapterInstanceContainer(){
			return container;
		}
		
		@Override
		public ISessionManager getSessionManager() {
			return sm;
		}

		@Override
		public IAdapterInstanceManager getAdapterInstanceManager() {
			return aim;
		}

		@Override
		public String getAttributeValue(String attributeName) {
			return instanceAttInfo.get(attributeName);
		}
		
		@Override
		public Set<String> getAttributeKeyList(){
			return instanceAttInfo.keySet();
		}

	}
}

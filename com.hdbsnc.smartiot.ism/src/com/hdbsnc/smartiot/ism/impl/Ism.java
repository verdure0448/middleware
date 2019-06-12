package com.hdbsnc.smartiot.ism.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.ism.IConnectionInfo;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.ISessionAllocater;
import com.hdbsnc.smartiot.common.ism.ISessionAllocaterCallback;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Ism implements IIntegratedSessionManager{

	private ServicePool pool;
	private Log logger;
	private Map<String, ISessionManager> smMap;	//instanceId:sessionManager
	private IProfileManager pm;
	
	private Map<IContext, InnerContextTracer> ctxTracerMap;
	
	private static final int CONTEXTTRACER_TIMEOUT = 1000*60*2; //2분 안에 응답이 없으면 응답시간 초과 에러보낸다.
	
	private String serverId;
	
	public Ism(IProfileManager pm, ServicePool pool, Log logger, String serverId){
		this.pool = pool;
		this.logger = logger.logger("ISM");
		this.smMap = new Hashtable<String, ISessionManager>();
		this.pm = pm;
		this.ctxTracerMap = new Hashtable<IContext, InnerContextTracer>();
		this.serverId = serverId;
	}
	
	@Override
	public ISessionManager getSessionManager(String instanceId) {
		return smMap.get(instanceId);
	}

	@Override
	public Map<String, ISessionManager> getSessionManagerMap() {
		return smMap;
	}

	@Override
	public ISessionManager createSessionManager(String instanceId) {
		IInstanceObj info;
		try {
			info = pm.getInstanceObj(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
			info = null;
		}
		if(info==null) return null;
		Sm sm = new Sm(this, info, pm, pool, logger);
		synchronized(smMap){
			smMap.put(instanceId, sm);
		}
		return sm;
	}

	/**
	 * only AIM(아답터인스턴스메니져)에서 이메소드를 사용함. 다른곳에서는 사용하지 말것. 
	 * 왜냐면, 해깔리니깐.... 쓰는 방향을 단순화 함. 
	 */
	@Override
	public IContextTracer offerContextTracer(IContext request, IContextCallback callback) {
		InnerContextTracer tracer = new InnerContextTracer(seq());
		tracer.inboundCtx = request;
		tracer.outboundCtx = null;
		tracer.callback = callback;
		ctxTracerMap.put(request, tracer);
		pool.addSchedule(tracer, new Date(System.currentTimeMillis()+CONTEXTTRACER_TIMEOUT));
		return tracer;
	}
	
	/**
	 * only ContextTracer에서 parent가 있는지 체크해서 있으면 아래의 메소드를 사용함. 
	 * ContextTracer의 체인을 지원하기 위해서...
	 */
	@Override
	public IContextTracer pollAndCallContextTracer(IContext request, IContext response) {
		InnerContextTracer tracer = ctxTracerMap.get(request);
		if(tracer!=null){
			ctxTracerMap.remove(request);
			tracer.outboundCtx = response;
			tracer.cancel();
			if(tracer.callback!=null){
				tracer.callback.responseSuccess(tracer);
			}
			//부모는 없다.
		}
		return tracer;
	}
	
	/**
	 * only ContextTracer에서 parent가 있는지 체크하기 위한 메소드. 
	 * ContextTracer의 체인을 지원하기 위해서...
	 */
	@Override
	public boolean isExistContextTracer(IContext request) {
		return this.ctxTracerMap.containsKey(request);
	}
	
	private long currentSeq = 0;
	private long lastSeqInitTime = 0;
	private long initTime = 1000*60*60; //ContextTracer 대기시간은 1시
	private synchronized String seq(){
		long currentTime = System.currentTimeMillis();
		if( (this.currentSeq>=999999) || ((currentTime-lastSeqInitTime)> initTime) ){
			this.currentSeq = 0;
			this.lastSeqInitTime = currentTime;
		}
		this.currentSeq++;
		return Long.toString(currentSeq);
	}
	
	private class InnerContextTracer extends TimerTask implements IContextTracer{

		private String seq;
		private IContext inboundCtx;
		private IContext outboundCtx;
		private IContextCallback callback;
		//ism 의 ContextTracer들은 부모가 없다.
		//private IContextTracer parentTracer; 
		
		private InnerContextTracer(String seq){
			super();
			this.seq = seq;
		}
		
		//무조건 null
		@Override
		public IContextTracer pollAndCallParent() {
			return null;
		}

		//무조건 false
		@Override
		public boolean hasParent() {
			return false;
		}

		@Override
		public String getSeq() {
			return seq;
		}

		@Override
		public IContext getRequestContext() {
			return this.inboundCtx;
		}

		@Override
		public IContext getResponseContext() {
			return this.outboundCtx;
		}

		@Override
		public IContextCallback getCallback() {
			return this.callback;
		}

		@Override
		public void run() {
			ctxTracerMap.remove(inboundCtx);
			if(this.callback!=null){
				this.callback.responseFail(this);
			}
			
		}
		
		@Override
		public void update() {
			synchronized(this){
				this.notifyAll();
			}
			
		}
		private long timeoutMs = 60000;
		@Override
		public IContext waitResponseContext() throws Exception {
			synchronized(this){
				long currentTimeoutMs = timeoutMs;
				long currentMs;
				long startMs = System.currentTimeMillis();
				while(true){
					if(outboundCtx!=null) break;
					try{this.wait(currentTimeoutMs);}catch(InterruptedException e){}
					currentMs = System.currentTimeMillis();currentTimeoutMs = currentMs - startMs;startMs = currentMs;
					if(currentTimeoutMs>=timeoutMs) {
						this.cancel();
						ctxTracerMap.remove(inboundCtx);
						throw new Exception("타임 아웃 예외.");
					}else{
						currentTimeoutMs = timeoutMs - currentTimeoutMs;
					}
					if(outboundCtx!=null) break;
				}
				this.cancel();
				ctxTracerMap.remove(inboundCtx);
			}
			return outboundCtx;
		}

		@Override
		public IContext waitResponseContext(long timeoutMs) throws Exception {
			synchronized(this){
				long currentTimeoutMs = timeoutMs;
				long currentMs;
				long startMs = System.currentTimeMillis();
				while(true){
					if(outboundCtx!=null) break;
					try{this.wait(currentTimeoutMs);}catch(InterruptedException e){}
					currentMs = System.currentTimeMillis();currentTimeoutMs = currentMs - startMs;startMs = currentMs;
					if(currentTimeoutMs>=timeoutMs) {
						this.cancel();
						ctxTracerMap.remove(inboundCtx);
						throw new Exception("타임 아웃 예외.");
					}else{
						currentTimeoutMs = timeoutMs - currentTimeoutMs;
					}
					if(outboundCtx!=null) break;
				}
				this.cancel();
				ctxTracerMap.remove(inboundCtx);
			}
			return outboundCtx;
		}
		
	}

	@Override
	/**
	 * IContextTracer 관련으로 등록된 것들을 취소시키고 제거한다. 
	 */
	public void cancelAll() {
		Set<Entry<IContext, InnerContextTracer>> entrySet2 = ctxTracerMap.entrySet();
		for(Entry<IContext, InnerContextTracer> entry : entrySet2){
			entry.getValue().cancel();
		}
		ctxTracerMap.clear();
	}
	
	@Override
	public void dispose(){
		cancelAll();
		synchronized(smMap){
			Set<Entry<String, ISessionManager>> entrySet = smMap.entrySet();
			for(Entry<String, ISessionManager> entry :entrySet){
				((Sm)entry.getValue()).ismCallDispose();
			}
			smMap.clear();
		}
	}
	
	@Override
	public void disposeSessionManager(String instanceId){
		Sm sm = null;
		synchronized(smMap){
			sm = (Sm) smMap.remove(instanceId);
		}
		if(sm!=null){
			sm.ismCallDispose();
		}
	}

	@Override
	public ISession getSession(String deviceId) {
		synchronized(smMap){
			Set<Entry<String, ISessionManager>> entrySet =  smMap.entrySet();
			for(Entry<String, ISessionManager> entry : entrySet){
				if(entry.getValue().containsDeviceId(deviceId)){
					return entry.getValue().getSessionByDeviceId(deviceId);
				}
			}
		}
		return null;
	}
	
	@Override
	public ISession getSessionBySessionId(String sessionId){
		synchronized(smMap){
			Set<Entry<String, ISessionManager>> entrySet =  smMap.entrySet();
			for(Entry<String, ISessionManager> entry : entrySet){
				if(entry.getValue().containsSessionKey(sessionId)){
					return entry.getValue().getSessionBySessionKey(sessionId);
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean containsDeviceId(String deviceId){
		synchronized(smMap){
			Set<Entry<String, ISessionManager>> entrySet =  smMap.entrySet();
			for(Entry<String, ISessionManager> entry : entrySet){
				if(entry.getValue().containsDeviceId(deviceId)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getServerId() {
		return this.serverId;
	}

	@Override
	public void innerNewSession(ISession session) throws Exception{
		if(allocater!=null) {
			try {
				allocater.allocateOrder(session, new ISessionAllocaterCallback(){

					@Override
					public void sessionCallbackEvent(int eventType, IConnectionInfo conInfo) {
						// 성공/실패에 따라 처리를 해준다. 슬래이브인경우에는 외부에 갔다가 ack/nack등을 받아온다.
						switch(eventType){
						case ISessionAllocaterCallback.EVENT_ALLOC_RESPONSE_SUCCESS_ACK:
							logger.info("ISessionAllocater 할당 요청에 대한 응답 ACK.");
							break;
						case ISessionAllocaterCallback.EVENT_ALLOC_RESPONSE_SUCCESS_NACK:
							logger.info("ISessionAllocater 할당 요청에 대한 응답 NACK.");
							break;
						case ISessionAllocaterCallback.EVENT_ALLOC_RESPONSE_FAIL:
							logger.info("ISessionAllocater 할당 요청 실패.");
							break;
							default:
								
						}
					}
					
				});
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("ISessionAllocater 할당 예외: "+e.getMessage());
			}
		}else{
			logger.info("ISessionAllocater 가 존재하지 않음. (스탠드 얼론 모드)");
		}
	}

	private ISessionAllocater allocater = null;
	@Override
	public void setSessionAllocater(ISessionAllocater allocater) {
		this.allocater = allocater;
	}

	/**
	 * 외부에서 ism을 통해 세션객체를 넣어줄경우 사용. 주로 slaveserver에서 사용된다.
	 */
	@Override
	public void outterNewSession(String deviceId, String userId, String sessionKey) throws Exception {
		IInstanceObj instanceObj = pm.searchInstanceByDevId(deviceId);
		if(instanceObj==null) throw new Exception("ProfileManager에 해당 deviceId("+deviceId+")에 대한 instance정보가 없음.");
		String insId = instanceObj.getInsId();
		ISessionManager sm = this.smMap.get(insId);
		if(sm==null) throw new Exception("동작중이 인스턴스가 없음.");
		sm.allocateSession(deviceId, userId, sessionKey);
		
	}

	/**
	 * 내부 인스턴스에서 세션을 종료 시킬 경우.
	 * ISession 의 dispose() 에서 호출 된다. 
	 */
	@Override
	public void innerDisposeSession(String sid) throws Exception {
		if(allocater!=null) {
			allocater.unallocateOrder(sid, new ISessionAllocaterCallback(){

				@Override
				public void sessionCallbackEvent(int eventType, IConnectionInfo conInfo) {
					// 성공/실패에 따라 처리를 해준다. 슬래이브인경우에는 외부에 갔다가 ack/nack등을 받아온다.
					switch(eventType){
					case ISessionAllocaterCallback.EVENT_UNALLOC_RESPONSE_SUCCESS_ACK:
						logger.info("ISessionAllocater 할당 요청에 대한 응답 ACK.");
						break;
					case ISessionAllocaterCallback.EVENT_UNALLOC_RESPONSE_SUCCESS_NACK:
						logger.info("ISessionAllocater 할당 요청에 대한 응답 NACK.");
						break;
					case ISessionAllocaterCallback.EVENT_UNALLOC_RESPONSE_FAIL:
						logger.info("ISessionAllocater 할당 요청 실패.");
						break;
						default:
					}
				}
				
			});
			
		}else{
			logger.info("ISessionAllocater 가 존재하지 않음. (스탠드 얼론 모드)");
		}
	}

	/**
	 * 서버 밖에서 세션을 종료시킬 경우 호출 됨. 
	 */
	@Override
	public void outterDisposeSession(String sessionKey) throws Exception {
		ISessionManager sm = getSessionManagerBySessionKey(sessionKey);
		if(sm==null) throw new Exception("동작중이 인스턴스가 없음.");
		sm.unallocateSession(sessionKey);
		
	}
	
	public ISession getSessionBySessionKey(String sessionKey){
		ISession sess = null;
		synchronized(smMap){
			Collection<ISessionManager> coll = smMap.values();
			for(ISessionManager sm: coll){
				sess = sm.getSessionBySessionKey(sessionKey);
				if(sess!=null) return sess;
			}
		}
		return null;
	}
	
	
	public ISessionManager getSessionManagerBySessionKey(String sessionKey){
		synchronized(smMap){
			Collection<ISessionManager> coll = smMap.values();
			for(ISessionManager sm: coll){
				if(sm.containsSessionKey(sessionKey)) return sm;
			}
		}
		return null;
	}
	
	@Override
	public int count() {
		return this.ctxTracerMap.size();
	}

	@Override
	public void updateSession(String deviceId, String userId, String sessionKey) throws Exception{
		IInstanceObj instanceObj = pm.searchInstanceByDevId(deviceId);
		if(instanceObj==null) throw new Exception("ProfileManager에 해당 deviceId("+deviceId+")에 대한 instance정보가 없음.");
		String insId = instanceObj.getInsId();
		ISessionManager sm = this.smMap.get(insId);
		if(sm==null) throw new Exception("동작중이 인스턴스가 없음.");
		sm.updateSession(deviceId, userId, sessionKey);
		
	}

}

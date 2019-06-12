package com.hdbsnc.smartiot.ism.impl;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.connection.impl.DefaultConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.AuthCertificationException;
import com.hdbsnc.smartiot.common.ism.sm.IAttributeMetaData;
import com.hdbsnc.smartiot.common.ism.sm.IDeviceProfile;
import com.hdbsnc.smartiot.common.ism.sm.IFunctionMetaData;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionState;
import com.hdbsnc.smartiot.common.ism.sm.ISessionStateListener;
import com.hdbsnc.smartiot.common.ism.sm.IUserProfile;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IDeviceObj;
import com.hdbsnc.smartiot.common.pm.vo.IDevicePoolObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceFunctionObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserFilterObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserObj;
import com.hdbsnc.smartiot.common.pm.vo.IUserPoolObj;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Sm implements ISessionManager, ISessionStateListener{
	private static final long DEFAULT_SESSION_TIMEOUT = 1000*60*60; // 한시
	
	private IIntegratedSessionManager ism;
	private IProfileManager pm;
	private ServicePool pool;
	private Log logger;
	private String instanceId;
	private IInstanceObj instanceObj;
	private DoubleKeySessionHashtable sessionTable;
	private HashKeyGenerator keyGen;
	private IConnectionManager cm = null;
	
	
	public Sm(IIntegratedSessionManager ism, IInstanceObj info, IProfileManager pm, ServicePool pool, Log logger){
		this.ism = ism;
		this.pm = pm;
		this.pool = pool;
		this.logger = logger;
		this.sessionTable = new DoubleKeySessionHashtable();
		this.instanceId = info.getInsId();
		this.instanceObj = info;
		try {
			this.keyGen = new HashKeyGenerator("MD5", ism.getServerId());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getAdapterInstanceId() {
		return this.instanceId;
	}

	@Override
	public int getSessionCount() {
		return this.sessionTable.size();
	}

	@Override
	public ISession getSessionByDeviceId(String deviceId) {
		InnerSession innerSession = (InnerSession) sessionTable.getByDeviceId(deviceId);
		if(innerSession!=null) innerSession.lastAccessedTime = System.currentTimeMillis();
		return innerSession;
	}
	
	@Override
	public ISession getSessionBySessionKey(String sessionKey){
		InnerSession innerSession = (InnerSession) sessionTable.getBySessionKey(sessionKey);
		if(innerSession!=null) innerSession.lastAccessedTime = System.currentTimeMillis();
		return innerSession;
	}

	@Override
	public List<ISession> getSessionList() {
		return new ArrayList<ISession>(sessionTable.values());
	}	
	
	@Override
	public synchronized ISession certificate(String deviceId, String userId, String password) throws AuthCertificationException, Exception{
		try {
			IInstanceObj instanceObj = pm.integrationAuth(userId, password, deviceId);
			if(instanceObj==null) throw new AuthCertificationException();
		} catch (Exception e1) {
			throw new AuthCertificationException();
		}
	
		ISession newSession = createSession(deviceId, userId, keyGen.generateKey(deviceId));
		newSession.addSessionStateListener(this);
		newSession.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_ACTIVATE);
		return newSession;
	}
	
	private ISession createSession(String deviceId, String userId, String sessionKey) throws Exception{
		InnerSession newSession = new InnerSession();
		newSession.deviceId = deviceId;
		logger.info("create session: "+deviceId+", "+userId+", "+sessionKey);
		fillSession(newSession, userId, sessionKey);
//		newSession.sessionKey = sessionKey;
//		newSession.instanceId = this.instanceId;
//		newSession.userId = userId;
//		String sessionTimeout = this.instanceObj.getSessionTimeout();
//		if(sessionTimeout==null){
//			newSession.sessionTimeout = DEFAULT_SESSION_TIMEOUT;
//		}else{
//			newSession.sessionTimeout = Long.parseLong(sessionTimeout);
//		}
//		setUpDeviceProfile(newSession.deviceProfile, pm.getDevicePoolObj(instanceObj.getDevPoolId()), pm.getDeviceObj(deviceId));
//		IUserObj userObj = pm.getUserObj(userId);
//		List<IUserFilterObj> filterList = pm.searchUserFilterByUserId(userId);
//		
//		setUpUserProfile(newSession.userProfile, pm.getUserPoolObj(userObj.getUserPoolId()), userObj, filterList);
//		setUpAttribute(newSession, pm.getInstanceAttributeList(instanceId));
//		sessionTable.put(newSession);
//		if(newSession.sessionTimeout!=0){
//			newSession.sessionTimeoutTask = newSession.new SessionTimeoutTask();
//			pool.addSchedule(newSession.sessionTimeoutTask, new Date(System.currentTimeMillis()+newSession.sessionTimeout));
//			logger.info(deviceId+" 세션 유효성 검증 작업 추가.(SessionTimeout:"+sessionTimeout+")");
//		}else{
//			newSession.sessionTimeoutTask = null;
//			logger.info(deviceId+" 세션 유효성 검증 하지 않음.(SessionTimeout: 0)");
//		}
		return newSession;
	}
	
	private void fillSession(InnerSession session, String userId, String sessionKey) throws Exception{
		session.instanceId = this.instanceId;
		session.sessionKey = sessionKey;
		session.userId = userId;
		String sessionTimeout = this.instanceObj.getSessionTimeout();
		if(sessionTimeout==null){
			session.sessionTimeout = DEFAULT_SESSION_TIMEOUT;
		}else{
			session.sessionTimeout = Long.parseLong(sessionTimeout);
		}
		setUpDeviceProfile(session.deviceProfile, pm.getDevicePoolObj(instanceObj.getDevPoolId()), pm.getDeviceObj(session.getDeviceId()));
		IUserObj userObj = pm.getUserObj(userId);
		List<IUserFilterObj> filterList = pm.searchUserFilterByUserId(userId);
		
		setUpUserProfile(session.userProfile, pm.getUserPoolObj(userObj.getUserPoolId()), userObj, filterList);
		setUpAttribute(session, pm.getInstanceAttributeList(instanceId));
		setUpFunction(session, pm.getInstanceFunctionList(instanceId));
		sessionTable.put(session);
		if(session.sessionTimeout!=0){
			session.sessionTimeoutTask = session.new SessionTimeoutTask();
			pool.addSchedule(session.sessionTimeoutTask, new Date(System.currentTimeMillis()+session.sessionTimeout));
			logger.info(session+" 세션 유효성 검증 작업 추가.(SessionTimeout:"+sessionTimeout+")");
		}else{
			session.sessionTimeoutTask = null;
			logger.info(session+" 세션 유효성 검증 하지 않음.(SessionTimeout: 0)");
		}
		session.addSessionStateListener(new ApiPushSessionStateListener(ism, pm, logger));
	}
	
	@Override
	public synchronized ISession allocateSession(String deviceId, String userId, String sessionKey) throws Exception{
		ISession newSession = createSession(deviceId, userId, sessionKey);
		newSession.addSessionStateListener(this);
		newSession.updateState(ISessionState.SESSION_CALL_OUTTER, ISessionState.SESSION_STATE_ACTIVATE);
		return newSession;
	}
	
	@Override
	public synchronized void unallocateSession(String sessionKey) throws Exception{
		ISession disposeSession = sessionTable.getBySessionKey(sessionKey);
		if(disposeSession!=null){
			sessionTable.removeBySessionKey(sessionKey);
			disposeSession.updateState(ISessionState.SESSION_CALL_OUTTER, ISessionState.SESSION_STATE_DISPOSE);
		}else{
			throw new Exception("세션이 존재하지 않아 해제 할 수 없습니다.");
		}
		
	}
	
	public void stop(){
		synchronized(sessionTable){
			ISession disposeSession;
			Iterator<ISession> iter = sessionTable.values().iterator();
			while(iter.hasNext()){
				disposeSession = iter.next();
				if(disposeSession.getSessionKey()!=null){
					disposeSession.updateState(ISessionState.SESSION_CALL_OUTTER, ISessionState.SESSION_STATE_DISPOSE);
				}
				iter.remove();
			}
			this.getConnectionManager().dispose();
		}
	}
	
	public void dispose(){
		ism.disposeSessionManager(instanceId);
	}
	
//	private synchronized InnerSession createSession(String deviceId, String userId) throws Exception{
//		InnerSession newSession = new InnerSession();
//		newSession.sessionKey = keyGen.generateKey(deviceId);
//		newSession.instanceId = this.instanceId;
//		newSession.deviceId = deviceId;
//		newSession.userId = userId;
//		newSession.lastAccessedTime = System.currentTimeMillis();
//		String sessionTimeout = this.instanceObj.getSessionTimeout();
//		if(sessionTimeout==null){
//			newSession.sessionTimeout = DEFAULT_SESSION_TIMEOUT;
//		}else{
//			newSession.sessionTimeout = Long.parseLong(sessionTimeout);
//		}
//		
//		setUpDeviceProfile(newSession.deviceProfile, pm.getDevicePoolObj(instanceObj.getDevPoolId()), pm.getDeviceObj(deviceId));
//		IUserObj userObj = pm.getUserObj(userId);
//		setUpUserProfile(newSession.userProfile, pm.getUserPoolObj(userObj.getUserPoolId()), userObj);
//		setUpAttribute(newSession, pm.getInstanceAttributeList(instanceId));
//		
//		sessionTable.put(newSession);
//		return newSession;
//	}
	
	private void setUpAttribute(InnerSession sess, List<IInstanceAttributeObj> attList){
		IInstanceAttributeObj att;
		if(attList!=null){
			for(int i=0,s=attList.size();i<s;i++){
				att = attList.get(i);
				sess.putAttribute(new DefaultAttributeMetaData(att.getKey(), att.getValue()));
			}
		}
	}
	
	private void setUpFunction(InnerSession sess, List<IInstanceFunctionObj> funcList){
		IInstanceFunctionObj func = null;
		if(funcList!=null){
			for(int i=0,s=funcList.size();i<s;i++){
				func = funcList.get(i);
				sess.putFunction(new DefaultFunctionMetaData(func.getKey(), func.getContType(), 
						new String[]{func.getParam1(), func.getParam2(), func.getParam3(), func.getParam4(), func.getParam5()}, 
						new String[]{func.getParamType1(), func.getParamType2(), func.getParamType3(), func.getParamType4(), func.getParamType5()}));
			}
		}
	}
	
	private void setUpDeviceProfile(InnerSession.DeviceProfile dp, IDevicePoolObj dpObj, IDeviceObj dObj){
		if(dpObj!=null){
			dp.devicePoolId = dpObj.getDevPoolId();
			dp.devicePoolNm = dpObj.getDevPoolNm();
			dp.devicePoolRemark = dpObj.getRemark();
		}
		if(dObj!=null){
			dp.deviceNm = dObj.getDevNm();
			dp.sessionTimeout = dObj.getSessionTimeout();
			dp.ip = dObj.getIp();
			dp.port = dObj.getPort();
			dp.isUse = dObj.getIsUse();
			dp.lat = dObj.getLat();
			dp.lon = dObj.getLon();
			dp.remark = dObj.getRemark();
		}
	}
	
	private void setUpUserProfile(InnerSession.UserProfile up, IUserPoolObj upObj, IUserObj uObj, List<IUserFilterObj> filterList ){
		if(upObj!=null){
			up.userPoolId = upObj.getUserPoolId();
			up.userPoolNm = upObj.getUserPoolNm();
			up.userPoolRemark = upObj.getRemark();
		}
		if(uObj!=null){
			up.userNm = uObj.getUserNm();
			up.userType = uObj.getUserType();
			up.titleNm = uObj.getTitleNm();
			up.deptNm = uObj.getDeptNm();
			up.compNm = uObj.getCompNm();
			up.remark = uObj.getRemark();
			
			if(filterList!=null && filterList.size()!=0){
				List<String> userFilterList = new ArrayList<String>();
				List<Pattern> userFilterPatternList = new ArrayList<Pattern>();
				String filter;
				for(IUserFilterObj obj : filterList){
					filter = obj.getAuthFilter();
					userFilterList.add(filter);
					if(filter!=null && filter.equals("*")) continue;
					try{
						userFilterPatternList.add(Pattern.compile(filter+".*"));
					}catch(PatternSyntaxException e){
						// 정규식 패턴에 일치하지 않는 것은 무시.
						System.out.println(e.getMessage());
					}
				}
				up.userFilterList = userFilterList;
				up.userFilterPatternList = userFilterPatternList;
			}
		}
	}
	
	public void deactivateSession(String sid){
		ISession timeoutSession;
		if(sid.startsWith("sid-")){
			timeoutSession = sessionTable.getBySessionKey(sid);
			if(timeoutSession!=null){
				sessionTable.removeBySessionKey(sid);
				timeoutSession.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_DEACTIVATE);
			}
		}else{
			timeoutSession = sessionTable.getByDeviceId(sid);
			if(timeoutSession!=null){
				sessionTable.removeByDeviceId(sid);
				timeoutSession.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_DEACTIVATE);
			}
		}
	}
	
	public synchronized void disposeSession(String sid){
		if(sid.startsWith("sid-")){
			ISession disposeSession = sessionTable.getBySessionKey(sid);
			if(disposeSession!=null){
				sessionTable.removeBySessionKey(sid);
				disposeSession.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_DISPOSE);
			}
		}else{
			ISession disposeSession =sessionTable.getByDeviceId(sid);
			if(disposeSession!=null){
				sessionTable.removeByDeviceId(sid);
				disposeSession.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_DISPOSE);
			}
		}
	}

	@Override
	public synchronized void disposeDeviceId(String deviceId) {
		ISession disposeSession =sessionTable.getByDeviceId(deviceId);
		if(disposeSession!=null){
			sessionTable.removeByDeviceId(deviceId);
			this.getConnectionManager().removeConnection(disposeSession.getSessionKey());
			disposeSession.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_DISPOSE);
		}
	}
	
	@Override
	public synchronized void disposeSessionKey(String sessionKey) {
		ISession disposeSession = sessionTable.getBySessionKey(sessionKey);
		if(disposeSession!=null){
			sessionTable.removeBySessionKey(sessionKey);
			this.getConnectionManager().removeConnection(sessionKey);
			disposeSession.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_DISPOSE);
		}
	}

	@Override
	public boolean isValidSession(String sessionKey) {
		ISession session = sessionTable.getBySessionKey(sessionKey);
		if(session!=null){
			switch(session.getState()){
			case ISession.SESSION_STATE_ACTIVATE:
				return true;
			}
		}
		return false;
	}

	/**
	 * ISM에서만 호출되는 메소드. 
	 */
	synchronized void ismCallDispose(){
		Iterator<ISession> iter = this.sessionTable.values().iterator();
		ISession temp;
		while(iter.hasNext()){
			temp = iter.next();
			temp.updateState(ISessionState.SESSION_CALL_INNER, ISessionState.SESSION_STATE_DISPOSE);
		}
		this.sessionTable.clear();
		if(cm!=null) cm.dispose();
		this.logger.info(this.instanceId+" 세션매니져가 제거 되었습니다.");
	}

	@Override
	public boolean containsDeviceId(String deviceId) {
		return this.sessionTable.containsDeviceId(deviceId);
	}

	@Override
	public boolean containsSessionKey(String sessionKey) {
		return this.sessionTable.containsSessionKey(sessionKey);
	}

	/**
	 * LastAccessedTime 업데이트 case
	 * 1) 세션이 생성되었을 때
	 * 2) SM에서 getSessionByDeviceId(String) 이 호출 되었을때. 
	 * 3) SM에서 getSessionBySessionKey(String) 이 호출 되었을떄. 
	 * 4) 세션의 seq() 가 호출 되었을때.
	 * @author hjs0317
	 *
	 */
	private class InnerSession implements ISession{
		private long initTime = 1000*60*60+1000; // 60분이 기다리는 최대시간이다.
		private long lastSeqInitTime;
		private long currentSeq;
		private long createdTime;
		private long lastAccessedTime;
		private long sessionTimeout; //0~몇 밀리세턴드의 세션타임아웃 시간 
		private int state;
		private String instanceId;
		private String sessionKey;
		private String deviceId;
		private String userId;
		private Map<String, DefaultAttributeMetaData> attMap;
		private Map<String, DefaultFunctionMetaData> funcMap;
		
		private Map<String, InnerContextTracer> ctxTracerMap;
		private DeviceProfile deviceProfile;
		private UserProfile userProfile;
		private List<ISessionStateListener> stateListenerList;
		private TimerTask sessionTimeoutTask;		
		private static final int CONTEXTTRACER_TIMEOUT = 1000*60*2; //2분안에 처리가 안되면 자동 fail처리된다. 
		private Map<String, Object> buffers = null;
		
		private InnerSession(){
			this.createdTime = System.currentTimeMillis();
			this.lastAccessedTime = createdTime;
			this.state = ISessionState.SESSION_STATE_CREATED;
			this.ctxTracerMap = new Hashtable<String, InnerContextTracer>();
			this.attMap = new Hashtable<String, DefaultAttributeMetaData>();
			this.funcMap = new Hashtable<String, DefaultFunctionMetaData>();
			this.lastSeqInitTime = 0;
			this.currentSeq = 0;
			this.deviceProfile = new DeviceProfile();
			this.userProfile = new UserProfile();
			this.stateListenerList = new ArrayList<ISessionStateListener>();
		}
		
		private synchronized String seq(){
			long currentTime = System.currentTimeMillis();
			if( (this.currentSeq>=999999) || ((currentTime-lastSeqInitTime)> initTime) ){
				this.currentSeq = 0;
				this.lastSeqInitTime = currentTime;
			}
			this.currentSeq++;
			this.lastAccessedTime = System.currentTimeMillis();
			return Long.toString(currentSeq);
		}
		
		@Override
		public long getCreatedTime() {
			return this.createdTime;
		}

		@Override
		public long getLastAccessedTime() {
			return this.lastAccessedTime;
		}

		@Override
		public long sessionTimeout() {
			return this.sessionTimeout;
		}

		@Override
		public int getState() {
			return this.state;
		}

		@Override
		public String getAdapterInstanceId() {
			return this.instanceId;
		}

		@Override
		public String getSessionKey() {
			return this.sessionKey;
		}

		@Override
		public String getDeviceId() {
			return this.deviceId;
		}
		
		@Override
		public String getUserId(){
			return this.userId;
		}
		
		@Override
		public synchronized void cancelAll(){
			Set<Entry<String, InnerContextTracer>> entrySet =this.ctxTracerMap.entrySet();
			for(Entry<String, InnerContextTracer> entry : entrySet){
				entry.getValue().cancel();// 외부에 요청한 건이 있다면 해당 요청에 대한 응답체크 타이머를 취소 한다.
			}
			this.ctxTracerMap.clear();
			if(sessionTimeoutTask!=null) sessionTimeoutTask.cancel(); // 세션 유효성 검증 타이머를 취소 한다. 
		}
		
		@Override
		public int count() {
			return ctxTracerMap.size();
		}
		
		@Override
		public synchronized IContextTracer pollAndCallContextTracer(String seq, IContext response){
			InnerContextTracer tracer = ctxTracerMap.get(seq);
			if(tracer!=null){
				ctxTracerMap.remove(seq);
				tracer.outboundCtx = response;
				tracer.cancel();
				if(tracer.callback!=null){
					tracer.callback.responseSuccess(tracer);
				}
				if(tracer.hasParent()){
					tracer.pollAndCallParent();
				}	
			}
			return tracer;
		}

		@Override
		public synchronized IContextTracer offerContextTracer(IContext request, IContextCallback callback) {
			InnerContextTracer tracer = new InnerContextTracer(seq());
			tracer.inboundCtx = request;
			tracer.outboundCtx = null;
			tracer.callback = callback;
			ctxTracerMap.put(tracer.getSeq(), tracer);
			pool.addSchedule(tracer, new Date(System.currentTimeMillis()+CONTEXTTRACER_TIMEOUT));
			return tracer;
		}

		@Override
		public synchronized boolean isExistContextTracer(String seq) {
			return ctxTracerMap.containsKey(seq);
		}
		
		@Override
		public IAttributeMetaData getAttribute(String key) {
			return this.attMap.get(key);
		}
		
		@Override
		public IFunctionMetaData getFunction(String key){
			return this.funcMap.get(key);
		}

		@Override
		public void setAttributeValue(String key, String value) {
			DefaultAttributeMetaData attMeta = this.attMap.get(key);
			if(attMeta!=null){
				attMeta.setValue(value);
			}else{
				attMeta = new DefaultAttributeMetaData(key, value);
				this.attMap.put(key, attMeta);
			}
		}
		
		public void putAttribute(DefaultAttributeMetaData attMeta){
			this.attMap.put(attMeta.getName(), attMeta);
		}
		
		public void putFunction(DefaultFunctionMetaData funcMeta){
			this.funcMap.put(funcMeta.getName(), funcMeta);
		}
		
		@Override
		public Set<String> getAttributeKeys(){
			return this.attMap.keySet();
		}
		@Override
		public Set<String> getFunctionKeys(){
			return this.funcMap.keySet();
		}
		
		@Override
		public boolean containsAttributeKey(String key){
			return this.attMap.containsKey(key);
		}
		@Override
		public boolean containsFunctionKey(String key){
			return this.funcMap.containsKey(key);
		}		
		
		@Override
		public IDeviceProfile getDeviceProfile() {
			return this.deviceProfile;
		}

		@Override
		public IUserProfile getUserProfile() {
			return this.userProfile;
		}
		
		private class InnerContextTracer extends TimerTask implements IContextTracer{
			private String seq;
			private IContext inboundCtx;
			private IContext outboundCtx;
			private IContextCallback callback;
			private IContextTracer parentTracer;
			
			private InnerContextTracer(String seq){
				super();
				this.seq = seq;
			}
			
			@Override
			public IContextTracer pollAndCallParent() {
				this.parentTracer = ism.pollAndCallContextTracer(inboundCtx, outboundCtx);
				return this.parentTracer;
			}

			@Override
			public boolean hasParent() {	
				return ism.isExistContextTracer(inboundCtx);
			}

			@Override
			public String getSeq() {
				return this.seq;
			}

			@Override
			public IContext getRequestContext() {
				return this.inboundCtx;
			}

			@Override
			public IContext getResponseContext() {
				return this.outboundCtx;
			}

			//이 타이머가 실행된다는 뜻은 제한시간음 넘어서도 응답혹은 처리가 안된다는 뜻이다.
			@Override
			public void run() {
				ctxTracerMap.remove(seq);
				if(this.callback!=null){
					this.callback.responseFail(this);
				}
			}

			@Override
			public IContextCallback getCallback() {
				return this.callback;
			}
			
			@Override
			public void update() {
				// 사용하지 않음: AIM 내부의 contextTracer만 구현해서 사용.
				
			}

			@Override
			public IContext waitResponseContext() throws Exception {
				// 사용하지 않음: AIM 내부의 contextTracer만 구현해서 사용.
				return null;
			}

			@Override
			public IContext waitResponseContext(long timeout) throws Exception {
				// 사용하지 않음: AIM 내부의 contextTracer만 구현해서 사용.
				return null;
			}
			
		}

		private class DeviceProfile implements IDeviceProfile{
			private String devicePoolId = null;
			private String devicePoolNm = null;
			private String devicePoolRemark = null;
			private String deviceNm = null;
			private String sessionTimeout = null;
			private String isUse = null;
			private String ip = null;
			private String port = null;
			private String lat = null;
			private String lon = null;
			private String remark = null;
			
			@Override
			public String getDevicePoolId() {
				return this.devicePoolId;
			}

			@Override
			public String getDevicePoolNm() {
				return this.devicePoolNm;
			}

			@Override
			public String getDevicePoolRemark() {
				return this.devicePoolRemark;
			}

			@Override
			public String getDeviceNm() {
				return this.deviceNm;
			}

			@Override
			public String getIsUse() {
				return this.isUse;
			}

			@Override
			public String getIp() {
				return this.ip;
			}

			@Override
			public String getPort() {
				return this.port;
			}

			@Override
			public String getLatitude() {
				return this.lat;
			}

			@Override
			public String getLongitude() {
				return this.lon;
			}

			@Override
			public String getRemark() {
				return this.remark;
			}

			@Override
			public String getSessionTimeout() {
				return sessionTimeout;
			}			
		}
		
		private class UserProfile implements IUserProfile{
			private String userPoolId = null;
			private String userPoolNm = null;
			private String userPoolRemark = null;
			private String userType = null;
			private String userNm = null;
			private String compNm = null;
			private String deptNm = null;
			private String titleNm = null;
			private String remark = null;
			private List<String> userFilterList = null;
			private List<Pattern> userFilterPatternList = null;
			
			@Override
			public String getUserPoolId() {
				return this.userPoolId;
			}

			@Override
			public String getUserPoolNm() {
				return this.userPoolNm;
			}

			@Override
			public String getUserPoolRemark() {
				return this.userPoolRemark;
			}

			@Override
			public String getUserType() {
				return this.userType;
			}

			@Override
			public String getUserNm() {
				return this.userNm;
			}

			@Override
			public String getCompNm() {
				return this.compNm;
			}

			@Override
			public String getDeptNm() {
				return this.deptNm;
			}

			@Override
			public String getTitleNm() {
				return this.titleNm;
			}

			@Override
			public String getRemark() {
				return this.remark;
			}
			
			@Override
			public List<String> getUserFilterList(){
				return this.userFilterList;
			}
			
			@Override
			public List<Pattern> getUserFilterPatternList(){
				return this.userFilterPatternList;
			}
			
		}

		@Override
		public void updateState(int caller, int sessionState) {
			this.state = sessionState;
			for(ISessionStateListener listener: this.stateListenerList){
				listener.changedState(caller, sessionState, this);
			}
		}

		@Override
		public void addSessionStateListener(ISessionStateListener listener) {
			this.stateListenerList.add(listener);
		}

		@Override
		public void removeSessionStateListener(ISessionStateListener listener) {
			this.stateListenerList.remove(listener);
		}

		@Override
		public void removeAllSessionStateListener() {
			this.stateListenerList.clear();
		}
		
		

		/**
		 * 세션타임아웃 시간이 도래하면 이 루틴을 타게 됨.
		 * 마지막 사용시간을 보고 유효하다면 연장. 아니라면 SM에게 dispose 요청.
		 */
		
		class SessionTimeoutTask extends TimerTask{
			
			SessionTimeoutTask(){
				super();
			}
			
			@Override
			public void run() {
				// 미리 정의된 세션타임아웃 시간이 도달했으면 타이머에 의해서 이쪽 코드로 옴.
				long currentTime = System.currentTimeMillis();
				long tempValue = currentTime - lastAccessedTime;
				if(tempValue>sessionTimeout){
					logger.info(deviceId+" SessionTimeout("+sessionTimeout+")");
					if(deviceId!=null && !deviceId.equals("")){
						deactivateSession(deviceId);
					}else if(sessionKey!=null && !sessionKey.equals("")){
						deactivateSession(sessionKey);
					}else{
						//둘중에 하나도 존재하지 않는 케이스는 절대 없다. 
					}
				}else{
					sessionTimeoutTask = new SessionTimeoutTask();
					pool.addSchedule(sessionTimeoutTask, new Date(System.currentTimeMillis()+sessionTimeout));
				}
				
			}
			
		}

		@Override
		public synchronized Object getBuffers(String key) {
			if(buffers==null) return null;
			return buffers.get(key);
		}

		@Override
		public synchronized Object removeBuffers(String key) {
			if(buffers==null) return null;
			return buffers.remove(key);
		}
		
		public synchronized void putBuffers(String key, Object obj){
			if(buffers==null) buffers = new HashMap<String, Object>();
			buffers.put(key, obj);
		}

		@Override
		public synchronized boolean containsKey(String key) {
			if(buffers==null) return false;
			return buffers.containsKey(key);
		}

		@Override
		public synchronized int buffersSize() {
			if(buffers==null) return 0;
			return buffers.size();
		}

		@Override
		public synchronized void clearBuffers() {
			if(buffers==null)  return;
			buffers.clear();
		}
		

	}

	@Override
	public IIntegratedSessionManager getIntegratedSessionManager() {
		return this.ism;
	}

	@Override
	public void changedState(int caller, int sessionState, ISession session) {
		switch(caller){
		case ISessionState.SESSION_CALL_INNER:
			switch(sessionState){
			case ISessionState.SESSION_STATE_ACTIVATE:
				try {
					ism.innerNewSession(session);
				} catch (Exception e1) {
					logger.err(e1);
				}
				break;
			case ISessionState.SESSION_STATE_DEACTIVATE:
				//내부 세션타임아웃시 발생되는 케이스.
				//세션타임아웃시 자동으로 뭔가를 해야 한다면 여기에 코드추가.
				
				//2016.4.19 GS인증 버젼까지는 DEACTIVATE == DISPOSE 동일하게 처리.
			case ISessionState.SESSION_STATE_DISPOSE:
				session.cancelAll();
				try {
					ism.innerDisposeSession(session.getSessionKey());
				} catch (Exception e) {
					logger.err(e);
				}
				break;
			}
			break;
		case ISessionState.SESSION_CALL_OUTTER:
			switch(sessionState){
			case ISessionState.SESSION_STATE_ACTIVATE:
				
				break;
			case ISessionState.SESSION_STATE_DEACTIVATE:
				
				break;
			case ISessionState.SESSION_STATE_DISPOSE:
				session.cancelAll();
				break;
			}
			break;
		}
	}

	@Override
	public void updateSession(String deviceId, String userId, String sessionKey) throws Exception {
		InnerSession updateSession = (InnerSession) this.sessionTable.getByDeviceId(deviceId);
		synchronized(updateSession){
			logger.info("update session: "+deviceId+", "+userId+", "+sessionKey);
			String oldSid = updateSession.getSessionKey();
			if(oldSid!=null && !oldSid.equals("")){
				this.sessionTable.removeBySessionKey(oldSid);
			}
			if(updateSession.sessionTimeout!=0){
				updateSession.sessionTimeoutTask.cancel();
				logger.info("update session: "+deviceId+" 세션 유효성 검증 작업 제거.");
			}
			fillSession(updateSession, userId, sessionKey);
		}
	}

	@Override
	public synchronized IConnectionManager getConnectionManager() {
		if(cm==null){
			cm = new DefaultConnectionManager();
		}
		return cm;
	}



	
	
}

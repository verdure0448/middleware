package com.hdbsnc.smartiot.service.master.slavemanager;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.IContextTracerSupportBySeq;
import com.hdbsnc.smartiot.common.ism.IConnectionInfo;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance.Device;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Server implements IContextTracerSupportBySeq{
	private static final int CONTEXTTRACER_TIMEOUT = 1000*60; //1시간안에 처리가 안되면 자동 fail처리 함. 
	Map<String, InnerContextTracer> ctxTracerMap;
	long initTime = 1000*60*60+1000; // 60분이 기다리는 최대시간이다.
	long lastSeqInitTime;
	long currentSeq;
	
	String serverSessionKey;
	String serverId;
	IConnection con;
	Map<String, Instance> instanceMap; //instanceId:instance
	Map<String, Device> deviceMap; //sessionKey:session
	IIntegratedSessionManager ism;
	ServicePool pool;
	
	Server(ServicePool pool, IIntegratedSessionManager ism, IConnection con, String serverId, String sessionKey){
		this.con = con;
		this.serverId = serverId;
		this.serverSessionKey = sessionKey;
		this.instanceMap = new Hashtable<String, Instance>();
		this.deviceMap = new Hashtable<String, Device>();
		
		this.lastSeqInitTime = 0;
		this.currentSeq = 0;
		this.ctxTracerMap = new Hashtable<String, InnerContextTracer>();
		this.ism = ism;
		this.pool = pool;
	}
	
	public synchronized String seq(){
		long currentTime = System.currentTimeMillis();
		if( (this.currentSeq>=999999) || ((currentTime-lastSeqInitTime)> initTime) ){
			this.currentSeq = 0;
			this.lastSeqInitTime = currentTime;
		}
		this.currentSeq++;
		return Long.toString(currentSeq);
	}
	
	public String getSessionKey(){
		return this.serverSessionKey;
	}

	public String getServerId() {
		return serverId;
	}
	
	public IConnection getConnection(){
		return con;
	}
	
	@Deprecated
	public Instance addInstance(String instanceId, String ip, String port){
		Instance newInstance = this.new Instance(instanceId, ip, port);
		instanceMap.put(instanceId, newInstance);
		return newInstance;
	}
	
	public Instance addInstance(String instanceId, String ip, String port, String url){
		Instance newInstance = this.new Instance(instanceId, ip, port, url);
		instanceMap.put(instanceId, newInstance);
		return newInstance;
	}
	
	public Instance addInstance(String instanceId){
		Instance newInstance = this.new Instance(instanceId);
		instanceMap.put(instanceId, newInstance);
		return newInstance;
	}
	
	public void removeInstance(String instanceId){
		synchronized(this){
			Instance instance = instanceMap.remove(instanceId);
			if(instance!=null){
				Set<String> sessionKeyList = instance.getSessionKeyList();
				for(String sessionKey: sessionKeyList){
					deviceMap.remove(sessionKey);
				}
				instanceMap.clear();
			}
		}
	}
	
	public Instance getInstance(String instanceId){
		return instanceMap.get(instanceId);
	}
	
	public Device getDevice(String sessionKey){
		return this.deviceMap.get(sessionKey);
	}
	
	public boolean containsInstance(String iid){
		return this.instanceMap.containsKey(iid);
	}
	
	public boolean containsDevice(String sessionKey){
		return this.deviceMap.containsKey(sessionKey);
	}
	
	public boolean containsDeviceByDeviceId(String deviceId){
		Collection<Device> devices = this.deviceMap.values();
		for(Device device: devices){
			if(device.getDeviceId().equals(deviceId)){
				return true;
			}
		}
		return false;
	}
	
	public void cancelAll(){
		synchronized(this){
			Set<Entry<String, InnerContextTracer>> entrySet =this.ctxTracerMap.entrySet();
			for(Entry<String, InnerContextTracer> entry : entrySet){
				entry.getValue().cancel();
			}
			this.ctxTracerMap.clear();
			try {
				con.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.deviceMap.clear();
			this.instanceMap.clear();
		}
	}

	public Set<String> getDeviceSessionKeySet(){
		return this.deviceMap.keySet();
	}
	
	public Device removeDevice(String deviceSessionKey){
		Device removeDevice = deviceMap.get(deviceSessionKey);
		if(removeDevice==null) return null;
		String iid = removeDevice.getInstanceId();
		Instance ins = instanceMap.get(iid);
		ins.removeDevice(deviceSessionKey);
		return removeDevice;
	}
	
	public class Instance implements IConnectionInfo{
		String instanceId;
		String ip;
		String port;
		String url;
		Map<String, Device> innerDeviceMap;//sessionKey:device
		
		public static final String STATE_INIT = "init";
		public static final String STATE_START = "start";
		public static final String STATE_STOP = "stop";
		public static final String STATE_SUSPEND = "suspend";
		
		String state = null;
		
		private Instance(String instanceId){
			this(instanceId, null, null, null);
		}
		
		@Deprecated
		private Instance(String iid, String ip, String port){
			this(iid, ip, port, null);
		}
		
		private Instance(String instanceId, String ip, String port, String url){
			this.instanceId = instanceId;
			this.ip = ip;
			this.port = port;
			this.url = url;
			this.innerDeviceMap = new Hashtable<String, Device>();
			this.state = STATE_INIT;
		}
		
		public void changeState(String state){
			this.state = state;
		}
		
		public void putIpPort(String ip, String port){
			this.ip = ip;
			this.port = port;
		}
		
		public void putUrl(String url){
			this.url = url;
		}
		
		public String getState(){
			return this.state;
		}
		
		/**
		 * 동일 DID로 새로운 로그인이 들어오면 기존 Device는 그대로 있으므로 제거해주어야 한다.
		 * @param deviceId
		 * @param sessionKey
		 * @param userId
		 */
		public void putDevice(String deviceId, String sessionKey, String userId){
			synchronized(this){
				Iterator<Device> iter = innerDeviceMap.values().iterator();
				Device dev;
				while(iter.hasNext()){
					dev = iter.next();
					if(dev.getDeviceId().equals(deviceId)) {
						deviceMap.remove(dev.getSessionKey());
						break;
					}
				}
				Device newDevice = this.new Device(deviceId, sessionKey, userId);
				innerDeviceMap.put(sessionKey, newDevice);
				deviceMap.put(sessionKey, newDevice);
			}
		}
		
		public void removeDevice(String sessionKey){
			synchronized(this){
				innerDeviceMap.remove(sessionKey);
				deviceMap.remove(sessionKey);
			}
		}
		
		public Set<String> getSessionKeyList(){ 
			return innerDeviceMap.keySet(); 
		}

		@Override
		public String getConnectIp() { return ip; }

		@Override
		public String getConnectPort() { return port; }
		
		@Override
		public String getConnectUrl() { return url; }

		@Override
		public String getServerId() { return serverId; }
		
		public String getInstanceId(){ return instanceId; }
		
		public class Device {
			String deviceId;
			String sessionKey;
			String userId;
			
			private Device(String deviceId, String sessionKey, String userId){
				this.deviceId = deviceId;
				this.sessionKey = sessionKey;
				this.userId = userId;
			}
			
			public String getServerId(){ return serverId; }
			
			public String getInstanceId(){ return instanceId; }
			
			public String getDeviceId(){ return this.deviceId; }
			
			public String getSessionKey(){ return this.sessionKey; }
			
			public String getUserId(){ return this.userId; }
		}
	}
	
	
	public class InnerContextTracer extends TimerTask implements IContextTracer{
		private String seq;
		private IContext inboundCtx;
		private IContext outboundCtx;
		private IContextCallback callback;
		private IContextTracer parentTracer;
		
		public InnerContextTracer(String seq){
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


	@Override
	public IContextTracer pollAndCallContextTracer(String portOrSeq, IContext outboundCtx) {
		InnerContextTracer tracer = ctxTracerMap.get(portOrSeq);
		if(tracer!=null){
			ctxTracerMap.remove(portOrSeq);
			tracer.outboundCtx = outboundCtx;
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
	public IContextTracer offerContextTracer(IContext inboundCtx, IContextCallback callback) {
		InnerContextTracer tracer = new InnerContextTracer(seq());
		tracer.inboundCtx = inboundCtx;
		tracer.outboundCtx = null;
		tracer.callback = callback;
		ctxTracerMap.put(tracer.getSeq(), tracer);
		pool.addSchedule(tracer, new Date(System.currentTimeMillis()+CONTEXTTRACER_TIMEOUT));
		return tracer;
	}

	@Override
	public boolean isExistContextTracer(String portOrSeq) {
		return ctxTracerMap.containsKey(portOrSeq);
	}

	@Override
	public int count() {
		return ctxTracerMap.size();
	}


	
	
}

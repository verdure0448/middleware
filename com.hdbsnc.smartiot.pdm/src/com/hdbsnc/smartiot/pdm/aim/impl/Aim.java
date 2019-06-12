package com.hdbsnc.smartiot.pdm.aim.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.AieListener;
import com.hdbsnc.smartiot.common.aim.AimException;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceContainer;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEventListener;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.em.AbstractEventProducer;
import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceAttributeObj;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.pdm.aim.container.impl.Aic;
import com.hdbsnc.smartiot.util.logger.Log;

public class Aim extends AbstractEventProducer implements IAdapterInstanceManager, IContextCallback{

	private Queue<IEvent> eventQueue;
	private IAdapterManager am;
	private IProfileManager pm;
	private ICommonService service;
	private IIntegratedSessionManager ism;
	
	private Map<String, IAdapterInstanceContainer> containerMap;//iid:insContainer
//	private List<IAdapterContext> ctxList;
	private IContextProcessor broker;
	private Log log;
	
	public Aim(IAdapterManager am1, IProfileManager pm2, IIntegratedSessionManager ism, ICommonService service){
		this.eventQueue = new LinkedList<IEvent>();
		this.am = am1;
		this.pm = pm2;
		this.service = service;
		this.ism = ism;
		this.log = service.getLogger().logger("AIM");
		this.containerMap = new Hashtable<String, IAdapterInstanceContainer>();
//		this.ctxList = new ArrayList<IAdapterContext>();
		this.broker = null; //마스터 서비스나 슬래이브 서비스가 없으면 널이다. 이게 널이면 스탠드올론 동작용이된다. 
	}
	
	public List<IAdapterInstanceContainer> getAdapterContainerList(){
		return new ArrayList<IAdapterInstanceContainer>(containerMap.values());
	}
	
//	public List<IAdapterContext> getAdapterContestList(){
//		return this.ctxList;
//	}
	
	public IAdapterInstanceContainer getAdapterInstance(String instanceId){
		return containerMap.get(instanceId);
	}
	
//	public IAdapterContext getAdapterContext(String instanceId){
//		IAdapterInstanceContainer container = getAdapterInstance(instanceId);
//		if(container!=null) return container.getContext();
//		return null;
//	}
	
//	private IAdapterContext createContext(IAdapterInstanceInfo info, IAdapter adapter){
//		InnerAdapterContext ctx = new InnerAdapterContext();
//		ctx.instanceInfo = info;
//		ctx.profileInfo = adapter.getManifest();
//		//ctx.processor = adapter.getFactory(service).createProcessor();
//		ctx.instance = adapter.getFactory(service).createInstance();
//		ctx.sm = ism.createSessionManager(info.getAdapterInstanceId());
//		
//		return ctx;
//	}
	
//	public class InnerAdapterContext implements IAdapterContext {
//		private long createdTime;
//		private IAdapterInstanceInfo instanceInfo;
//		private IAdapterManifest profileInfo;
//		private IAdapterInstance instance;
//		private ServicePool servicePool;
//		private Log logger;
//		private ISessionManager sm;		
//		
//		private InnerAdapterContext(){
//			createdTime = System.currentTimeMillis();
//		}
//		
//		@Override
//		public long getCreatedTime() {
//			return createdTime;
//		}
//
//		@Override
//		public IAdapterInstanceInfo getAdapterInstanceInfo() {
//			return instanceInfo;
//		}
//
//		@Override
//		public IAdapterManifest getAdapterManifest() {
//			return profileInfo;
//		}
//
//		@Override
//		public IAdapterProcessor getAdapterProcessor() {
//			return this.instance.getProcessor();
//		}
//		
//		@Override
//		public ISessionManager getSessionManager() {
//			return this.sm;
//		}
//
//	}

	// TODO 2015-07-14 장치 인스턴스속성 가져오는 부분이 비합리적인 구조임. 향후 개선할 필요 있을 듯 함.
	@Override
	public void initialize(final String instanceId) throws AimException {
		final IAdapterInstanceContainer container = createContainer(instanceId);
		
		//초기화가 끝나면 containerMap에 넣는다. 에러나면 안넣겠지?
		container.addEventListener(new AieListener(){

			@Override
			public void onChangeAdapterInstance(IAdapterInstanceEvent e) {
				containerMap.put(instanceId, container);
			}
			
		}, IAdapterInstanceEvent.INITIALIZE_EVENT, IAdapterInstanceEvent.END_STATE, true);//한번만 처리함.
		
		container.asyncProcess(IAdapterInstanceEvent.INITIALIZE_EVENT);
	} 
	
	public void start(final String instanceId) throws AimException{
		start(instanceId, null);
	}
	
	public void stop(final String instanceId) throws AimException{
		stop(instanceId, null);
	}
	
	//명령을 직접 내리거나 제어하고자 할 경우 suspend상태로 변경해야 한다.
	public void suspend(String instanceId) throws AimException{
		suspend(instanceId, null);
	}
	
	public void start(final String instanceId, IAdapterInstanceEventListener completedListener) throws AimException{
		IInstanceObj info;
		try {
			info = pm.getInstanceObj(instanceId);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new AimException("인스턴스 정보를 가져오는데 실패하였습니다.(인스턴스ID: "+instanceId+")", e1);
		}
		if(info==null) throw new AimException("존재하지 않는 인스턴스아이디: "+instanceId);
		String myAid = info.getAdtId();
		String myDpid = info.getDevPoolId();
		String tempAid, tempDpid;
		int eventType, eventState;
		// 디바이스풀아이디가 다른 아답터의 인스턴스에서 사용중이면 예외 발생. 같은 아답터의 다른인스턴스에서는 사용 가능. 2016.3.10 황준
		synchronized(containerMap){
			Iterator<IAdapterInstanceContainer> iter = this.containerMap.values().iterator();
			IAdapterInstanceContainer aic;
			while(iter.hasNext()){
				aic = iter.next();
				tempAid = aic.getLastEvent().getInstanceInfo().getAdtId();
				tempDpid = aic.getLastEvent().getInstanceInfo().getDevPoolId();
				eventType = aic.getLastEvent().getEventType();
				eventState = aic.getLastEvent().getStateType();
				switch(eventType){
				case IAdapterInstanceEvent.START_EVENT:
				case IAdapterInstanceEvent.SUSPEND_EVENT:
					switch(eventState){
					case IAdapterInstanceEvent.BEGIN_STATE:
					case IAdapterInstanceEvent.DOING_STATE:
					case IAdapterInstanceEvent.COMPLETED_STATE:
					case IAdapterInstanceEvent.END_STATE:
						if(myDpid.equals(tempDpid) && !myAid.equals(tempAid)) throw new AimException("디바이스풀아이디가 다른 아답터의 인스턴스에서 사용중입니다.");
						break;
					}
					break;
				}
			}
		}
		
		if(containerMap.containsKey(instanceId)){
			IAdapterInstanceContainer container = containerMap.get(instanceId);
			int evt = container.getLastEvent().getEventType();
			int svt = container.getLastEvent().getStateType();
			switch(evt){
			case IAdapterInstanceEvent.INITIALIZE_EVENT:
			case IAdapterInstanceEvent.SUSPEND_EVENT:
			case IAdapterInstanceEvent.STOP_EVENT:
				if(svt==IAdapterInstanceEvent.END_STATE){
					container.asyncProcess(IAdapterInstanceEvent.START_EVENT); //아답터 개발자가 어떤짓을 했을지 모르니 별도의 쓰레드로 띄운다.
					return;
				}				
			default:
				throw new AimException(instanceId+", reqEvt: "+IAdapterInstanceEvent.START_EVENT+", curEvt: "+evt+", curSvt: "+svt);
			}
		}
		final IAdapterInstanceContainer container = createContainer(instanceId);
		container.addEventListener(new AieListener(){

			@Override
			public void onChangeAdapterInstance(IAdapterInstanceEvent e) {
				containerMap.put(instanceId, container);
				container.asyncProcess(IAdapterInstanceEvent.START_EVENT);
			}
			
		}, IAdapterInstanceEvent.INITIALIZE_EVENT, IAdapterInstanceEvent.END_STATE, true);//한번만 처리됨.
		
		if(completedListener!=null){
			container.addEventListener(completedListener, IAdapterInstanceEvent.START_EVENT, IAdapterInstanceEvent.END_STATE, true); //한번만 처리됨.
		}
		container.asyncProcess(IAdapterInstanceEvent.INITIALIZE_EVENT);
	}
	

	
	private IAdapterInstanceContainer createContainer(String instanceId) throws AimException{
		IInstanceObj info;
		try {
			info = pm.getInstanceObj(instanceId);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new AimException("인스턴스 정보를 가져오는데 실패하였습니다.(인스턴스ID: "+instanceId+")", e1);
		}
		if(info==null) throw new AimException("존재하지 않는 인스턴스아이디: "+instanceId);
		String myAid = info.getAdtId();
		String tempRestart = info.getIsUse();
		boolean restart;
		if(tempRestart==null || tempRestart.equals("") || !Boolean.parseBoolean(tempRestart)) {
			restart = false;
		}else{
			restart = Boolean.valueOf(tempRestart);
		}
		Map<String, String> attInfo = new HashMap<String, String>();
		try{
			List<IInstanceAttributeObj> objList = pm.getInstanceAttributeList(instanceId);
			for(IInstanceAttributeObj obj: objList){
				if(obj!=null) attInfo.put(obj.getKey(), obj.getValue());
			}
		} catch (Exception e1){
			e1.printStackTrace();
			throw new AimException("인스턴스속성 정보를 가져오는데 실패하였습니다.(인스턴스ID: "+instanceId+")", e1);
		}
		
		IAdapter adapter = am.getAdapter(myAid);
		if(adapter==null) throw new AimException("존재하지 않는 아답터아이디: "+myAid);
		ISessionManager sm = ism.createSessionManager(info.getInsId());
		final Aic container = new Aic(this, adapter, info, attInfo, sm, service);
		
		if(restart){
			container.addEventListener(new AieListener(){

				@Override
				public void onChangeAdapterInstance(IAdapterInstanceEvent e) {
					switch(e.getEventType()){
					case IAdapterInstanceEvent.START_EVENT:
						if(e.getStateType()==IAdapterInstanceEvent.ERROR_STATE){
							try {
								log.info("5초 후 재시작("+e.getInstanceInfo().getInsId()+")");
								Thread.sleep(1000*5);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							container.asyncProcess(IAdapterInstanceEvent.START_EVENT);
						}
						break;
					case IAdapterInstanceEvent.STOP_EVENT:
						setRemoveFlag();
						break;
					}
					
				}
				
			}, IAdapterInstanceEvent.START_EVENT | IAdapterInstanceEvent.STOP_EVENT, IAdapterInstanceEvent.ERROR_STATE | IAdapterInstanceEvent.BEGIN_STATE, false);//계속 해서 처리.
		}
		/**
		 * 인스턴스 상태가 바뀌면 마스터서버에 통보하는 코드.
		 */
		container.addEventListener(new AieListener(){
			@Override
			public void onChangeAdapterInstance(IAdapterInstanceEvent e) {
				if(e.getStateType()!=IAdapterInstanceEvent.END_STATE){
					return;
				}
				if(broker==null){
					return;//스탠드 얼론 모
				}
				String cmdPath = "";
				switch(e.getEventType()){
				case IAdapterInstanceEvent.START_EVENT:
					cmdPath = "start";
					break;
				case IAdapterInstanceEvent.SUSPEND_EVENT:
					cmdPath = "suspend";
					break;
				case IAdapterInstanceEvent.STOP_EVENT:
					cmdPath = "stop";
					setRemoveFlag();//스톱 이벤트 이므로 더이상 처리될 일이 없음. 현재 리스너를 제거한다.
					break;
				}
				
				InnerContext ctx = new InnerContext();
				ctx.sid = null;
				ctx.tid = null;
				ctx.paths = Arrays.asList("master",cmdPath);
				ctx.params = new HashMap<String, String>();
				IInstanceObj insObj = e.getInstanceInfo();
				ctx.params.put("iid", insObj.getInsId());
				ctx.params.put("ip", insObj.getIp());
				ctx.params.put("port", insObj.getPort());
				ctx.params.put("url", insObj.getUrl());
				log.info("instance state change(evt:"+e.getEventType()+", "+insObj.getInsId()+")");
				try {
					broker.parallelProcess(ctx);
				} catch (Exception e1) {
					e1.printStackTrace();
					log.err(e1);
				}
			}
		}, IAdapterInstanceEvent.START_EVENT|IAdapterInstanceEvent.SUSPEND_EVENT|IAdapterInstanceEvent.STOP_EVENT, IAdapterInstanceEvent.END_STATE, false);//계속해서 처리됨.

		return container;
	}
	
	public void suspend(String instanceId, IAdapterInstanceEventListener completedListener) throws AimException{
		IAdapterInstanceContainer container;
		//IAdapterContext ctx;
		
		container = containerMap.get(instanceId);
		if(container!=null){
			//ctx = container.getContext();
			int evt = container.getLastEvent().getEventType();
			int svt = container.getLastEvent().getStateType();
			switch(evt){
			case IAdapterInstanceEvent.START_EVENT:
				if(svt==IAdapterInstanceEvent.END_STATE){
					container.addEventListener(completedListener, IAdapterInstanceEvent.SUSPEND_EVENT, IAdapterInstanceEvent.END_STATE, true);//한번만 실행됨.
					container.asyncProcess(IAdapterInstanceEvent.SUSPEND_EVENT);
					return;
				}
			default:
				throw new AimException(instanceId+", reqEvt: "+IAdapterInstanceEvent.SUSPEND_EVENT+", curEvt: "+evt+", curSvt: "+svt);
			}
		}
		throw new AimException(instanceId+"의 인스턴컨테이너가 존재하지 않습니다.");
	}
	

	public void stop(final String instanceId, IAdapterInstanceEventListener completedListener) throws AimException{
		IAdapterInstanceContainer container = containerMap.get(instanceId);
		if(container!=null){
			int evt = container.getLastEvent().getEventType();
			int svt = container.getLastEvent().getStateType();
			switch(evt){
			case IAdapterInstanceEvent.START_EVENT:
			case IAdapterInstanceEvent.SUSPEND_EVENT:
//				if(svt==IAdapterInstanceEvent.END_STATE){
				container.addEventListener(new AieListener(){
	
						@Override
						public void onChangeAdapterInstance(IAdapterInstanceEvent e) {
	//							containerMap.put(instanceId, container);
	//							container.asyncProcess(IAdapterInstanceEvent.DISPOSE_EVENT);
							try {
								dispose(instanceId);
							} catch (AimException e1) {
								e1.printStackTrace();
							}
						}}, IAdapterInstanceEvent.STOP_EVENT, IAdapterInstanceEvent.END_STATE, true);//한번만.
				if(completedListener!=null){
					container.addEventListener(completedListener, IAdapterInstanceEvent.STOP_EVENT, IAdapterInstanceEvent.END_STATE, true);//한번만 실행.
				}
				container.asyncProcess(IAdapterInstanceEvent.STOP_EVENT);
				return;
//			}
			default:
				throw new AimException(instanceId+", reqEvt"+IAdapterInstanceEvent.STOP_EVENT+", curEvt"+evt+", curSvt"+svt);
			}
		}
		throw new AimException(instanceId+"의 인스턴스컨테이너가 존재하지 않습니다.");
	}
	


	@Override
	public void dispose(String instanceId) throws AimException {
		final IAdapterInstanceContainer container;
		//IAdapterContext ctx;
		
		container = containerMap.get(instanceId);
		if(container!=null){
			//ctx = container.getContext();
			int evt = container.getLastEvent().getEventType();
			int svt = container.getLastEvent().getStateType();
			switch(evt){
			case IAdapterInstanceEvent.INITIALIZE_EVENT:
			case IAdapterInstanceEvent.STOP_EVENT:
				// 그냥 dispose 한다.
//				if(svt==IAdapterInstanceEvent.END_STATE){
					container.asyncProcess(IAdapterInstanceEvent.DISPOSE_EVENT);
					this.containerMap.remove(container.getContext().getAdapterInstanceInfo().getInsId());
					return;
//				}
//				break;
			case IAdapterInstanceEvent.START_EVENT:
			case IAdapterInstanceEvent.SUSPEND_EVENT:
				// stop후에 dispose 한다.
//				if(svt==IAdapterInstanceEvent.END_STATE){
					container.addEventListener(new AieListener(){
						
						@Override
						public void onChangeAdapterInstance(IAdapterInstanceEvent e) {
							container.asyncProcess(IAdapterInstanceEvent.DISPOSE_EVENT);
							setRemoveFlag();//현재 리스너 제거.
						}
						
					});
					container.asyncProcess(IAdapterInstanceEvent.STOP_EVENT);
					return;
//				}
//				break;
			default:
				throw new AimException(instanceId+", reqEvt"+IAdapterInstanceEvent.DISPOSE_EVENT+", curEvt"+evt+", curSvt"+svt);
			}
		}
		throw new AimException(instanceId+"의 인스턴스컨테이너가 존재하지 않습니다.");
	}

	/**
	 * @ TODO TID는 this로 넘어오면 안됨. SID는 default로 넘어오면 안됨. 각각 실제 ID로 치환 해서 보내야 함.
	 * TID가 this로 오면 안된다. 아답터의 개별 통신을 지칭하는 SelfID이므로 AIM입장에서는 this가 어디 연결인지 알 수 있는 방법이 없음.
	 * handOverContext 메소드를 호출하기전에 this를 자기자신의 인스턴아이디로 치환 할 것.
	 */
	@Override
	public void handOverContext(IContext exec, IContextCallback callback)throws AimException {
		ISession session = this.ism.getSession(exec.getTID());
		
		if(session==null) {
			if(broker==null) throw new AimException("ContextBroker가 존재하지 않습니다.");
			try{
				this.ism.offerContextTracer(exec, callback);
				this.broker.parallelProcess(exec);
			}catch(Exception e){
				throw new AimException("타켓 장치 세션이 존재하지 않습니다.(장치ID: "+exec.getTID()+")");
			}
			return;
		}
		
		String instanceId = session.getAdapterInstanceId();
		String path = exec.getFullPath();
		IAdapterInstanceContainer container = this.containerMap.get(instanceId);
		if(container==null) throw new AimException("인스턴스가 존재않아 해당 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
// 정책을 결정해서 진행해야 함. 
//		if(!(container.getLastEvent().getEventType()==IAdapterInstanceEvent.SUSPEND_EVENT && container.getLastEvent().getStateType()==IAdapterInstanceEvent.COMPLETED_STATE)){
//			throw new AimException("인스턴스의 상태가 SUSPEND가 아니여 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
//		}
		IAdapterProcessor p = container.getProcessor();
		try {
			this.ism.offerContextTracer(exec, callback);
			p.parallelProcess(exec);
		} catch (Exception e) {
			throw new AimException(e);
		}
		
	}
	
	@Override
	public void handOverContextByCurrentThread(IContext exec, IContextCallback callback)throws AimException {
		ISession session = this.ism.getSession(exec.getTID());
		
		if(session==null) {
			if(broker==null) throw new AimException("ContextBroker가 존재하지 않습니다.");
			try{
				this.ism.offerContextTracer(exec, callback);
				this.broker.process(exec);
			}catch(Exception e){
				throw new AimException("타켓 장치 세션이 존재하지 않습니다.(장치ID: "+exec.getTID()+")");
			}
			return;
		}
		
		String instanceId = session.getAdapterInstanceId();
		String path = exec.getFullPath();
		IAdapterInstanceContainer container = this.containerMap.get(instanceId);
		if(container==null) throw new AimException("인스턴스가 존재않아 해당 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
// 정책을 결정해서 진행해야 함. 
//		if(!(container.getLastEvent().getEventType()==IAdapterInstanceEvent.SUSPEND_EVENT && container.getLastEvent().getStateType()==IAdapterInstanceEvent.COMPLETED_STATE)){
//			throw new AimException("인스턴스의 상태가 SUSPEND가 아니여 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
//		}
		IAdapterProcessor p = container.getProcessor();
		try {
			this.ism.offerContextTracer(exec, callback);
			p.process(exec);
		} catch (Exception e) {
			throw new AimException(e);
		}
		
	}

//	@Override
//	public IContext syncExecute(IContext exec) throws AimException {
//		ISession session = this.ism.getSession(exec.getTID());
//		if(session==null) {
//			try{
//				return this.broker.syncExecute(exec);
//			}catch(Exception e){
//				throw new AimException("타켓 장치 세션이 존재하지 않습니다.(장치ID: "+exec.getTID()+")");
//			}
//		}
//		String instanceId = session.getAdapterInstanceId();
//		String path = exec.getFullPath();
//		IAdapterInstanceContainer container = this.containerMap.get(instanceId);
//		if(container==null) throw new AimException("인스턴스가 존재않아 해당 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
//
//		if(!(container.getLastEvent().getEventType()==IAdapterInstanceEvent.SUSPEND_EVENT && container.getLastEvent().getStateType()==IAdapterInstanceEvent.COMPLETED_STATE)){
//			throw new AimException("인스턴스의 상태가 SUSPEND가 아니여 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
//		}
//		IAdapterProcessor p = container.getProcessor();
//		try {
//			return p.processCurrentThread(exec);
//		} catch (Exception e) {
//			throw new AimException(e);
//		}
//	}

	
	
	@Override
	public void setContextBroker(IContextProcessor broker) {
		this.broker = broker;
	}

	@Override
	public int getModuleValue() {
		return IEvent.MODULE_PDM;
	}

	@Override
	public IEvent consumeFirstEvent() {
		synchronized(eventQueue){
			return eventQueue.poll();
		}
	}

	@Override
	public boolean isEmpty() {
		return eventQueue.isEmpty();
	}

	@Override
	protected void putEvent(IEvent evt) {
		synchronized(eventQueue){
			eventQueue.offer(evt);
		}
	}
	
	public IContextTracer handOverContext(IContext request) throws AimException{
		ISession session = this.ism.getSession(request.getTID());
		IContextTracer tracer = null;
		//슬래이브 서비스로 넘어감 : ism 내에서 TID 세션이 존재하지 않는 경우.
		if(session==null) {
			if(broker==null) throw new AimException("ContextBroker가 존재하지 않습니다.");
			try{
				tracer = this.ism.offerContextTracer(request, this);
				this.broker.parallelProcess(request);
			}catch(Exception e){
				throw new AimException("타켓 장치 세션이 존재하지 않습니다.(장치ID: "+request.getTID()+")");
			}

		}else{
		//내부에서 처리됨: ism 내에서 TID 세션이 존재하는 경우. 
			String instanceId = session.getAdapterInstanceId();
			String path = request.getFullPath();
			IAdapterInstanceContainer container = this.containerMap.get(instanceId);
			if(container==null) throw new AimException("인스턴스가 존재않아 해당 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
			IAdapterProcessor p = container.getProcessor();
			try {
				tracer = this.ism.offerContextTracer(request, this);
				p.parallelProcess(request);
			} catch (Exception e) {
				throw new AimException(e);
			}
		}
		return tracer;
	}
	
	public IContextTracer handOverContextByCurrentThread(IContext request) throws AimException{
		ISession session = this.ism.getSession(request.getTID());
		IContextTracer tracer = null;
		//슬래이브 서비스로 넘어감 : ism 내에서 TID 세션이 존재하지 않는 경우.
		if(session==null) {
			if(broker==null) throw new AimException("ContextBroker가 존재하지 않습니다.");
			try{
				tracer = this.ism.offerContextTracer(request, this);
				this.broker.process(request);
			}catch(Exception e){
				throw new AimException("타켓 장치 세션이 존재하지 않습니다.(장치ID: "+request.getTID()+")");
			}

		}else{
		//내부에서 처리됨: ism 내에서 TID 세션이 존재하는 경우. 
			String instanceId = session.getAdapterInstanceId();
			String path = request.getFullPath();
			IAdapterInstanceContainer container = this.containerMap.get(instanceId);
			if(container==null) throw new AimException("인스턴스가 존재않아 해당 명령을 실행할 수 없습니다.("+instanceId+", "+path+")");
			IAdapterProcessor p = container.getProcessor();
			try {
				tracer = this.ism.offerContextTracer(request, this);
				p.process(request);
			} catch (Exception e) {
				throw new AimException(e);
			}
		}
		return tracer;
	}

	@Override
	public void responseSuccess(IContextTracer ctxTracer) {
		ctxTracer.update();
	}

	@Override
	public void responseFail(IContextTracer ctxTracer) {
		ctxTracer.update();
		
	}
	
	
	
}

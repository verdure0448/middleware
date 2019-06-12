package com.hdbsnc.smartiot.service.slave.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.connection.impl.AdvancedClientSocketChannelConnector;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextTracer;
import com.hdbsnc.smartiot.common.context.IContextTracerSupportBySeq;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.ISessionAllocaterCallback;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.slave.ISlaveService;
import com.hdbsnc.smartiot.service.slave.impl.connection.DefaultSingleConnectionManager;
import com.hdbsnc.smartiot.service.slave.impl.connection.handler.CallContextProcessorHandler;
import com.hdbsnc.smartiot.service.slave.impl.connection.handler.OtpParser2Handler;
import com.hdbsnc.smartiot.service.slave.impl.connection.handler.PutConnectionHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.SlaveContextProcessor2;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.ConnectAckHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.ConnectHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.ConnectNackHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.MasterAllocateHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.MasterStartInstanceHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.MasterStopInstanceHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.MasterSuspendInstanceHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.MasterUnallocateAckHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.MasterUnallocateHandler;
import com.hdbsnc.smartiot.service.slave.impl.process.handler2.SlaveAllocateHandler;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Sss2 implements ISlaveService, IContextTracerSupportBySeq, IService{

	public static final String KEY_SLAVE_SERVERID = "sss.id";
	public static final String KEY_MASTER_SERVERID = "mss.id";
	private ServicePool pool;
	private Log logger;
	private IIntegratedSessionManager ism;
	private IAdapterInstanceManager aim;
	private String masterServerId = "";
	private String slaveServerId = "";
	private String slaveServerSessionId = "";
	
	public static final String STATE_CREATE		= "create"; 	// 최초 생성 된 상태. 초기값. 
	public static final String STATE_INIT		= "initialize";
	public static final String STATE_START 		= "start";
//	public static final String STATE_CONNECT 	= "connect"; 	// connect 요청 상태. 
	public static final String STATE_ACTIVATE	= "activate";	// connect/ack 로 왔을 경우 
	public static final String STATE_UNACTIVATE = "unactivate";	// connect/nack 로 왔을 경우 
	public static final String STATE_STOP 		= "stop";
	
	public static final int CONTEXTTRACER_TIMEOUT = 1000*60;
	
	private String state = null;
	private Map<String, InnerContextTracer> ctxTracerMap;
	private long currentSeq;
	private long lastSeqInitTime;
	private long initTime = 1000*60*60+1000;
	private IConnectionManager cm;
	private SlaveContextProcessor2 processor;
	private AdvancedClientSocketChannelConnector connector;
	
	private int currentState = SERVICE_STATE_REG;
	private long lastAccessedTime = 0;
	private BundleContext ctx;
	private ICommonService cs;
	
	public Sss2(BundleContext ctx, IServerInstance serverInstance){
		this.ctx = ctx;
		this.cs = serverInstance.getCommonService();
		this.pool = cs.getServicePool();
		this.ism = serverInstance.getISM();
		this.aim = serverInstance.getAIM();
		this.state = STATE_CREATE;
		this.ctxTracerMap = new Hashtable<String, InnerContextTracer>();
		this.lastAccessedTime = System.currentTimeMillis();
		serverInstance.getAIM().setContextBroker(this);
		serverInstance.getISM().setSessionAllocater(this);
	}
	
	private synchronized String seq(){
		long currentTime = System.currentTimeMillis();
		if( (this.currentSeq>=999999) || ((currentTime-lastSeqInitTime)> initTime)){
			this.currentSeq = 0;
			this.lastSeqInitTime = currentTime;
		}
		this.currentSeq++;
		return Long.toString(currentSeq);
	}
	
	private ServiceRegistration serviceReg = null;
	public void changeState(String state){
		this.state = state;
		if(state.equals(STATE_ACTIVATE)){
			serviceReg = ctx.registerService(ISlaveService.class.getName(), this, null);
		}else if(state.equals(STATE_UNACTIVATE)){
			if(serviceReg!=null) serviceReg.unregister();
		}
	}
	
	public String currentState(){
		return this.state;
	}
	
	public void init(Map<String, String> params) throws Exception{
		this.currentState = SERVICE_STATE_INIT;
		this.state = STATE_INIT;
		
		slaveServerId = params.get("sss.id");
		masterServerId = params.get("mss.id");
		String ts_ip = params.get("sss.tcpsocket.ip");
		String ts_port = params.get("sss.tcpsocket.port");
		String ts_readbuffersize = params.get("sss.tcpsocket.readbuffersize");
		String ts_retryms = params.get("sss.tcpsocket.retryms");
		
		this.logger = cs.getLogger().logger(slaveServerId);
		
		logger.info("Initialize SlaveServer.");
		
		
		cm = new DefaultSingleConnectionManager();
		processor = new SlaveContextProcessor2(cs, slaveServerId, aim, this, ism, cm);
		RootHandler root = processor.getRootHandler();
		root.putHandler("master", new ConnectHandler(this));
		root.putHandler("master/connect", new ConnectAckHandler(this));
		root.putHandler("master/connect", new ConnectNackHandler(this));
		root.putHandler("master", new MasterAllocateHandler(this));
		root.putHandler("master", new MasterUnallocateHandler(this));
		root.putHandler("master/unalloc", new MasterUnallocateAckHandler(this));
		root.putHandler("master", new MasterStartInstanceHandler(this));
		root.putHandler("master", new MasterStopInstanceHandler(this));
		root.putHandler("master", new MasterSuspendInstanceHandler(this));
		root.putHandler("slave", new SlaveAllocateHandler(ism));
	
		
		ConnectionHandleChain chc = new ConnectionHandleChain(pool);
		
		OtpParser2Handler otpParserHandler = new OtpParser2Handler(logger);
		otpParserHandler.setNext(new PutConnectionHandler(cm)).setNext(new CallContextProcessorHandler(processor, logger));
		chc.setHandlerChain(otpParserHandler);
		
		connector = new AdvancedClientSocketChannelConnector(cm, chc, logger, pool);
		Map<String, String> config = new HashMap<String, String>();
		config.put(AdvancedClientSocketChannelConnector.KEY_IP, ts_ip);
		config.put(AdvancedClientSocketChannelConnector.KEY_PORT, ts_port);
		config.put(AdvancedClientSocketChannelConnector.KEY_READBUFFERSIZE, ts_readbuffersize);
		config.put(AdvancedClientSocketChannelConnector.KEY_RETRY_MS, ts_retryms);
		config.put("sss.id", slaveServerId);
		config.put("mss.id", masterServerId);
		connector.initialize(config);
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	public String getSlaveServerId(){
		return this.slaveServerId;
	}
	
	public String getMasterServerId(){
		return this.masterServerId;
	}
	
	public String getSlaveServerSessionId(){
		return this.slaveServerSessionId;
	}
	
	public void setSessionId(String sessionId){
		this.slaveServerSessionId = sessionId;
	}
	
	
	public void start(){
		this.currentState = SERVICE_STATE_START;
		this.state = STATE_START;
		//clientSocket 이 구동되면 내부적으로 connect 요청이 마스터에게 넘어간다.
		connector.start();
		
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	public void stop(){
		this.currentState = SERVICE_STATE_STOP;
		this.state = STATE_STOP;
		//구동이 정지되면 비활성 상태로 변화.
		connector.stop();
		
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	@Override
	public void parallelProcess(IContext inboundCtx) throws Exception {
		switch(state){
		case STATE_ACTIVATE:
			// 처리 루틴
			processor.parallelProcess(inboundCtx);
			return;
		default: //그외의 케이스는 정상처리 불가. 
			throw new Exception("슬래이브 서비스가 활성화 되지 않았습니다.");	
		}
		
	}

	@Override
	public void process(IContext inboundCtx) throws Exception {
		switch(state){
		case STATE_ACTIVATE:
			// 정상 처리 루틴 
			processor.process(inboundCtx);
			return;
		default: //그외의 케이스는 정상처리 불가. 
			throw new Exception("슬래이브 서비스가 활성화 되지 않았습니다.");	
		}
		
	}

	@Override
	public void cancelAll() {
		Set<Entry<String, InnerContextTracer>> entrySet = this.ctxTracerMap.entrySet();
		for(Entry<String, InnerContextTracer> entry: entrySet){
			entry.getValue().cancel();
		}
		this.ctxTracerMap.clear();
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

		@Override
		public IContextCallback getCallback() {
			return this.callback;
		}

		@Override
		public void run() {
			ctxTracerMap.remove(seq);
			if(this.callback!=null){
				this.callback.responseFail(this);
			}
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
	public void allocateOrder(ISession session, ISessionAllocaterCallback callback) throws Exception {
		switch(state){
		case STATE_ACTIVATE:
			// 정산 처리 루틴
			//마스터에게 세션추가 요청을 해야 한다.
			InnerContext ctx = new InnerContext();
			ctx.paths = new ArrayList<String>();
			ctx.paths.add("master");
			ctx.paths.add("alloc");
			ctx.params = new HashMap<String, String>();
			ctx.params.put("iid",session.getAdapterInstanceId());
			ctx.params.put("did", session.getDeviceId());
			ctx.params.put("uid", session.getUserId());
			ctx.params.put("sid", session.getSessionKey());
			ism.offerContextTracer(ctx, new IContextCallback(){
				public void responseSuccess(IContextTracer ctxTracer) {
					IContext outboundCtx = ctxTracer.getResponseContext();
					if(outboundCtx.getPaths().contains("ack")){//경로 fullpass를 체크하지 않고 있음. 향후 수정할 것.
						callback.sessionCallbackEvent(ISessionAllocaterCallback.EVENT_ALLOC_RESPONSE_SUCCESS_ACK, null);
					}else{
						callback.sessionCallbackEvent(ISessionAllocaterCallback.EVENT_ALLOC_RESPONSE_SUCCESS_NACK, null);
					}
					
				}
				public void responseFail(IContextTracer ctxTracer) {
					callback.sessionCallbackEvent(ISessionAllocaterCallback.EVENT_ALLOC_RESPONSE_FAIL, null);
				}
			});
			processor.parallelProcess(ctx);//비동기로 전송. 
			return;
		default: //그외의 케이스는 정상처리 불가. 
			throw new Exception("슬래이브 서비스가 활성화 되지 않았습니다.");	
		}
	}
	
	@Override
	public void unallocateOrder(String sid, ISessionAllocaterCallback callback) throws Exception {
		switch(state){
		case STATE_ACTIVATE:
			// 정산 처리 루틴
			//마스터에게 세션추가 요청을 해야 한다.
			InnerContext ctx = new InnerContext();
			ctx.sid = null;
			ctx.tid = null;
			ctx.paths = new ArrayList<String>();
			ctx.paths.add("master");
			ctx.paths.add("unalloc");
			ctx.params = new HashMap<String, String>();
			ctx.params.put("sid", sid);
			processor.parallelProcess(ctx);//비동기로 전송. 
			return;
		default: //그외의 케이스는 정상처리 불가. 
			throw new Exception("슬래이브 서비스가 활성화 되지 않았습니다.");	
		}
		
	}

	@Override
	public String getServiceName() {
		return "SlaveServerService";
	}

	@Override
	public int getServiceState() {
		return this.currentState;
	}

	@Override
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

}
package com.hdbsnc.smartiot.service.master.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.connection.IConnector;
import com.hdbsnc.smartiot.common.connection.impl.DefaultConnectionManager;
import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.ism.ISessionAllocaterCallback;
import com.hdbsnc.smartiot.common.ism.sm.ISession;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.master.IMasterService;
import com.hdbsnc.smartiot.service.master.connection.impl.AdvancedServerSocketChannelConnector;
import com.hdbsnc.smartiot.service.master.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.service.master.impl.connection.handler.CallContextProcessorHandler;
import com.hdbsnc.smartiot.service.master.impl.connection.handler.OtpParser2Handler;
import com.hdbsnc.smartiot.service.master.impl.connection.handler.PutConnectionHandler;
import com.hdbsnc.smartiot.service.master.impl.process.MasterContextProcessor3;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.ConnectHandler;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.MasterAllocateHandler;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.MasterStartInstanceHandler;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.MasterStopInstanceHandler;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.MasterSuspendInstanceHandler;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.MasterUnallocateHandler;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.SlaveAllocateHandler;
import com.hdbsnc.smartiot.service.master.impl.process.handler2.SlaveUnallocateHandler;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Mss2 implements IMasterService, IService{
	//주석 테스트 UTF-8
	public static final String KEY_SERVERID = "msId";

	private ServicePool pool;
	private Log logger;
	private IConnector connector;
	private MasterContextProcessor3 processor;
	private IIntegratedSessionManager ism;
	private IAdapterInstanceManager aim;
	private SlaveServerManager ssm;
	private DefaultConnectionManager cm;
	private String masterServerId = "";
	
	private int currentState = 0;
	private long lastAccessedTime = SERVICE_STATE_REG;
	private BundleContext ctx;
	private ICommonService cs;
	
	public Mss2(BundleContext ctx, IServerInstance serverInstance){
		this.ctx = ctx;
		this.cs = serverInstance.getCommonService();
		this.pool = cs.getServicePool();
		
		this.ism = serverInstance.getISM();
		this.aim = serverInstance.getAIM();
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
//	public Mss(ServicePool pool1, Log logger2, IIntegratedSessionManager ism3, IAdapterInstanceManager aim4){
//		this.pool = pool1;
//		this.logger = logger2.logger("MSS");
//		this.ism = ism3;
//		this.aim = aim4;
//		this.lastAccessedTime = System.currentTimeMillis();
//	}
	
	@Override
	public void init(Map<String, String> config) throws Exception {
		String masterServerId = config.get("mss.id");
		String ts_ip = config.get("mss.tcpsocket.ip");
		String ts_port = config.get("mss.tcpsocket.port");
		String ts_readBufferSize = config.get("mss.tcpsocket.readbuffersize");
		String ts_retryMs = config.get("mss.tcpsocket.retryms");
		
		this.logger = cs.getLogger().logger(masterServerId);
		
		currentState = SERVICE_STATE_INIT;
//		masterServerId = config.get(KEY_SERVERID);
		
		ssm = new SlaveServerManager(masterServerId, cs, ism, logger);
		cm = new DefaultConnectionManager();
		
		processor = new MasterContextProcessor3(cs,masterServerId, ism, ssm);
		RootHandler root = processor.getRootHandler();
		
		/**
		 * master/connect
		 * master/connect/ack
		 * master/alloc : ack 같이 구현. 
		 * master/unalloc : ack 같이 구현.
		 * master/start : defautAck
		 * master/stop : defaultAck
		 * master/suspend : defaultAck
		 * slave/alloc
		 * slave/alloc/ack
		 * 
		 */
		
		ConnectHandler connect = new ConnectHandler(ssm, cm);
		MasterAllocateHandler masterAlloc = new MasterAllocateHandler(ssm);
		MasterUnallocateHandler masterUnalloc = new MasterUnallocateHandler(ssm);
		MasterStartInstanceHandler masterStart = new MasterStartInstanceHandler(ssm);
		MasterStopInstanceHandler masterStop = new MasterStopInstanceHandler(ssm);
		MasterSuspendInstanceHandler masterSuspend = new MasterSuspendInstanceHandler(ssm);
		SlaveAllocateHandler slaveAlloc = new SlaveAllocateHandler(ssm);
		SlaveUnallocateHandler slaveUnalloc = new SlaveUnallocateHandler(ssm);
		
		root.putHandler("master", connect);
		root.putHandler("master", masterAlloc);
		root.putHandler("master", masterUnalloc);
		root.putHandler("master", masterStart);
		root.putHandler("master", masterStop);
		root.putHandler("master", masterSuspend);
		root.putHandler("slave", slaveAlloc);
		root.putHandler("slave", slaveUnalloc);
		
		ConnectionHandleChain chc = new ConnectionHandleChain(pool);
		OtpParser2Handler otpParserHandler = new OtpParser2Handler(logger);
		otpParserHandler.setNext(new PutConnectionHandler(cm)).setNext(new CallContextProcessorHandler(processor));
		
		chc.setHandlerChain(otpParserHandler);
		
		connector = new AdvancedServerSocketChannelConnector(chc, logger, pool);
		Map<String, String> params = new HashMap<String, String>();
		params.put(AdvancedServerSocketChannelConnector.KEY_IP, ts_ip);
		params.put(AdvancedServerSocketChannelConnector.KEY_PORT, ts_port);
		params.put(AdvancedServerSocketChannelConnector.KEY_READBUFFERSIZE, ts_readBufferSize);
		params.put(AdvancedServerSocketChannelConnector.KEY_RETRY_MS, ts_retryMs);
		
		connector.initialize(params);
		this.lastAccessedTime = System.currentTimeMillis();
		
	}
	private ServiceRegistration sr;
	public void start() throws Exception{
		this.currentState = SERVICE_STATE_START;
		connector.start();
		sr = ctx.registerService(IMasterService.class.getName(), this, null);
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	public void stop() throws Exception{
		this.currentState = SERVICE_STATE_STOP;
		connector.stop();
		sr.unregister();
		this.lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public void parallelProcess(IContext inboundCtx) throws Exception {
		processor.parallelProcess(inboundCtx);
		
	}
	
	public void process(IContext inboundCtx) throws Exception{
		processor.process(inboundCtx);
	}

	public String getServerId(){
		return this.masterServerId;
	}

	@Override
	public void handOverContext(IContext exec, IContextCallback callback) throws Exception {
		//ism에 contextTracer를 등록하여 처리. 
		ism.offerContextTracer(exec, callback);
		processor.process(exec);
	}

	@Override
	public SlaveServerManager getSlaveServerManager() {
		return this.ssm;
	}
	
	/**
	 * 마스터서버인 경우에는 내부적으로(ISM에서 내부장치만 별도 관리) Device 등록만 한다.
	 * 아답터에서 오는 세션 생성 케이스에는 서버를 할당하지 않는다. 아답터 스스로가 인증 및 통신을 모두 진행하기 때문. 
	 */
	@Override
	public void allocateOrder(ISession session, ISessionAllocaterCallback callback) throws Exception {
		//MSS에서는 별도로 처리할 것이 없다. 
		if(callback!=null) callback.sessionCallbackEvent(ISessionAllocaterCallback.EVENT_NONE, null);
		
	}

	@Override
	public void unallocateOrder(String sid, ISessionAllocaterCallback callback) throws Exception {
		//MSS에서는 별도로 처리할 것이 없다. 
		if(callback!=null) callback.sessionCallbackEvent(ISessionAllocaterCallback.EVENT_NONE, null);
		
	}

	@Override
	public String getServiceName() {
		return "MasterServerService";
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

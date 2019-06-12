package com.hdbsnc.smartiot.service.auth.impl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.connection.impl.DefaultConnectionManager;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;
import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.auth.connection.http.HttpApiServlet;
import com.hdbsnc.smartiot.service.auth.connection.impl.AdvancedServerSocketChannelConnector;
import com.hdbsnc.smartiot.service.auth.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.service.auth.connection.websocket.ApiSocketServlet;
import com.hdbsnc.smartiot.service.auth.impl.connection.handler.CallContextProcessorHandler;
import com.hdbsnc.smartiot.service.auth.impl.connection.handler.WebSocketPutConnectionHandler;
import com.hdbsnc.smartiot.service.auth.impl.process.AuthContextProcessor2;
import com.hdbsnc.smartiot.service.auth.impl.process.handler2.AuthOpenHandler;
import com.hdbsnc.smartiot.service.auth.impl.process.handler2.IsloginHandler;
import com.hdbsnc.smartiot.service.master.IMasterService;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class Ass2 implements IService{

	public static final String WEBSOCKET_PORT = "ws_port";
	public static final String WEBSOCKET_IP = "ws_ip";
	
	public static final String TCPSOCKET_PORT = "ts_port";
	public static final String TCPSOCKET_IP = "ts_ip";
	
	private ServicePool pool;
	private Log logger;
//	private Server wsServer;
	private IProfileManager pm;
	private IMasterService mss;
	private AdvancedServerSocketChannelConnector tsServer;
	private DefaultConnectionManager cm;
	private IIntegratedSessionManager ism;
	private IWebservicePool wsPool;
	private ICommonService cs;
	private int currentState = SERVICE_STATE_REG;
	private long lastAccessedTime = 0;
	
	public Ass2(IServerInstance server, IMasterService mss){
		this.cs = server.getCommonService();
		this.pool = cs.getServicePool();
		this.logger = cs.getLogger().logger("ASS");
		this.pm = server.getPM();
		this.mss = mss;
		this.ism = server.getISM();
		this.wsPool = cs.getWebservicePool();
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	public void init(Map<String, String> params) throws Exception{
		
		String ws_ip = params.get("ass.websocket.ip");
		String ws_port = params.get("ass.websocket.port");
		String ws_path = params.get("ass.websocket.path");
		String ts_ip = params.get("ass.tcpsocket.ip");
		String ts_port = params.get("ass.tcpsocket.port");
		String ts_readbuffersize = params.get("ass.tcpsocket.readbuffersize");
		
		
		currentState = SERVICE_STATE_INIT;
		
		cm = new DefaultConnectionManager();
		AuthContextProcessor2 processor = new AuthContextProcessor2(cs, mss.getServerId(), cm, ism, logger);
		RootHandler root = processor.getRootHandler();
		IsloginHandler islogin = new IsloginHandler(mss);
		AuthOpenHandler authOpen = new AuthOpenHandler(pm, mss, processor, cm, logger);
		
		root.putHandler("auth", authOpen);
		root.putHandler("", islogin);
		
		
		wsPool.addSingletonServerConnector(Integer.parseInt(ws_port));
		
		
		
		
		ConnectionHandleChain chc = new ConnectionHandleChain(pool);
		WebSocketPutConnectionHandler wsPut = new WebSocketPutConnectionHandler(cm);
		wsPut.setNext(new CallContextProcessorHandler(processor, logger));
		chc.setHandlerChain(wsPut);
		
		// 1. 웹소켓 연결 지원.
		ApiSocketServlet apiSocketServlet = new ApiSocketServlet(pool, logger, chc);
		wsPool.addSingletonServerServlet("servlet"+ws_path, apiSocketServlet, ws_path); //   /auth  최초 값 
		logger.info("Auth Websocket Path: "+ws_path);
		
		// 2. HTTP 연결 지원. 
		// 경로가 smartiot/auth2 로 고정되어 있음. 설정파일로 뺄것.
		HttpApiServlet httpApiServlet = new HttpApiServlet(chc);
		wsPool.addSingletonServerServlet("servlet/auth2", httpApiServlet, "/auth2");
		logger.info("Auth Http Path: /auth2");
		
		//크로스도메인 프리플라이트 옵션 처리를 위해 필터 등록
		FilterHolder holder = new FilterHolder(new CrossOriginFilter());
	    holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM,  "*");
	    holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");

	    wsPool.getSingletonServerServletContextHandler().addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST)); 
		
		
		// 3. TCP 연결 지원.
		tsServer = new AdvancedServerSocketChannelConnector(chc, logger, pool);
		Map<String, String> tsServerParams = new HashMap<String, String>();
		tsServerParams.put(AdvancedServerSocketChannelConnector.KEY_IP, ts_ip);
		tsServerParams.put(AdvancedServerSocketChannelConnector.KEY_PORT, ts_port);
		tsServerParams.put(AdvancedServerSocketChannelConnector.KEY_READBUFFERSIZE, ts_readbuffersize);
		tsServer.initialize(tsServerParams);
		this.lastAccessedTime = System.currentTimeMillis();
		
	}
	
	public void start(){
		currentState = SERVICE_STATE_START;
		try {
//			wsServer.start();
			wsPool.restartSingletonServer();//켜져있으면 끄고 다시 스타트 한다. 
			tsServer.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.err(e);
		}
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	public void stop(){
		currentState = SERVICE_STATE_STOP;
		try {
//			wsServer.stop();
			wsPool.stopSingletonServer(); //여기서 끄면, 다른 서블릿들도 같이 꺼진다. 향후 수정방안 마련 할 것. 
			tsServer.stop();
			cm.dispose();
		} catch (Exception e) {
			e.printStackTrace();
			logger.err(e);
		}
		this.lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public String getServiceName() {
		return "AuthServerService";
	}

	@Override
	public int getServiceState() {
		return currentState;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}
}
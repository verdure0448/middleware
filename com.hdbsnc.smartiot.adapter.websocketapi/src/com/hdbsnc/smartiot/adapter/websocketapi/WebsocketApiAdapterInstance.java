package com.hdbsnc.smartiot.adapter.websocketapi;

import java.net.URI;

import org.eclipse.jetty.server.Server;

import com.hdbsnc.smartiot.adapter.websocketapi.connection.websocket.ApiSocketServlet;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcAdapterGetAllHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcGatheringStartEventHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcGatheringStopEventHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcInsAttDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcInsAttPutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcInsAttSearchByIidHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcInstanceStartHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcInstanceStopHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcMonitoringStartEventHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.expansion.plc.PlcMonitoringStopEventHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.AuthCloseHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.DomainSearchByTypeHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.IsloginHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.LogoutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.NfcHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter.AdapterAttGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter.AdapterFuncGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter.AdapterGetAllHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter.AdapterGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter.AdapterInstallHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.adapter.AdapterUninstallHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DeviceDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DeviceGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DeviceMsgStartEventHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DeviceMsgStopEventHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DevicePoolDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DevicePoolGetAllHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DevicePoolGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DevicePoolPutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DevicePoolSetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DevicePutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DeviceSearchByDpid;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.device.DeviceSetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsAttDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsAttGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsAttPutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsAttSearchByIidHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsAttSetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsFuncDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsFuncGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsFuncPutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsFuncSearchByIidHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InsFuncSetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceGetAllHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstancePutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceSearchByAidHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceSetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceStartHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceStopHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.instance.InstanceSuspendHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session.SessionAttGetAllHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session.SessionDisconnectHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session.SessionFuncGetAllHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session.SessionGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.session.SessionSearchByIidHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserFilterDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserFilterGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserFilterPutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserFilterSearchByUid;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserFilterSetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserPoolDelHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserPoolGetAllHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserPoolGetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserPoolPutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserPoolSetHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserPutHandler;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserSearchByUpid;
import com.hdbsnc.smartiot.adapter.websocketapi.processor.handler.user.UserSetHandler;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.aim.IAdapterProcessor;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;
import com.hdbsnc.smartiot.common.connection.impl.CallContextProcessorHandler;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionManagerHandler;
import com.hdbsnc.smartiot.common.context.handler2.impl.RootHandler;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;
import com.hdbsnc.smartiot.util.logger.Log;

public class WebsocketApiAdapterInstance implements IAdapterInstance {

	private ICommonService service;
	private Log logger;
	private IConnectionManager cm;
	private WebsocketApiContextProcessor processor;
	private IProfileManager pm;
	private IAdapterManager am;
	private IEventManager em;
	private IWebservicePool wsPool;
	private String webServerName;

	public WebsocketApiAdapterInstance(ICommonService service, IProfileManager pm, IAdapterManager am, IEventManager em) {
		this.service = service;
		this.cm = null;
		this.processor = null;
		this.pm = pm;
		this.am = am;
		this.em = em;
		this.wsPool = service.getWebservicePool();
	}

	private void createContextHandler(RootHandler root, IAdapterContext ctx) throws Exception{
		ISessionManager sm = ctx.getSessionManager();
		IAdapterInstanceManager aim = ctx.getAdapterInstanceManager();
				
		root.putHandler("", new IsloginHandler());
		root.putHandler("", new LogoutHandler());
		root.putHandler("auth", new AuthCloseHandler());
		
		root.putHandler("adt", new AdapterGetHandler(am));
		root.putHandler("adt", new AdapterInstallHandler(am));
		root.putHandler("adt", new AdapterUninstallHandler(pm, am));
		root.putHandler("adt/att", new AdapterAttGetHandler(am));
		root.putHandler("adt/func", new AdapterFuncGetHandler(am));
		root.putHandler("adt/get", new AdapterGetAllHandler(am));
		
		root.putHandler("ins", new InstanceGetHandler(pm, aim));
		root.putHandler("ins", new InstanceGetAllHandler(pm, am, aim));
		root.putHandler("ins", new InstanceSetHandler(pm, am));
		root.putHandler("ins", new InstancePutHandler(pm, am));
		root.putHandler("ins", new InstanceDelHandler(pm, sm));
		root.putHandler("ins/search", new InstanceSearchByAidHandler(pm, aim));
		
		root.putHandler("ins", new InstanceStartHandler(aim));
		root.putHandler("ins", new InstanceStopHandler(pm, aim));
		root.putHandler("ins", new InstanceSuspendHandler(pm, aim));
		
		root.putHandler("ins/att", new InsAttGetHandler(pm));
		root.putHandler("ins/att", new InsAttSetHandler(pm));
		root.putHandler("ins/att", new InsAttPutHandler(pm));
		root.putHandler("ins/att", new InsAttDelHandler(pm, sm));
		root.putHandler("ins/att/search", new InsAttSearchByIidHandler(pm));
		
		root.putHandler("ins/func", new InsFuncGetHandler(pm));
		root.putHandler("ins/func", new InsFuncSetHandler(pm));
		root.putHandler("ins/func", new InsFuncPutHandler(pm));
		root.putHandler("ins/func", new InsFuncDelHandler(pm, sm));
		root.putHandler("ins/func/search", new InsFuncSearchByIidHandler(pm));
		
		root.putHandler("session/att/get", new SessionAttGetAllHandler(pm, sm));
		root.putHandler("session/func/get", new SessionFuncGetAllHandler(pm, sm));
		root.putHandler("session", new SessionDisconnectHandler(pm));
		root.putHandler("session", new SessionGetHandler(sm));
		root.putHandler("session/search", new SessionSearchByIidHandler(sm));
		
		root.putHandler("devpool", new DevicePoolGetHandler(pm));
		root.putHandler("devpool", new DevicePoolSetHandler(pm));
		root.putHandler("devpool", new DevicePoolPutHandler(pm));
		root.putHandler("devpool", new DevicePoolDelHandler(pm));
		root.putHandler("devpool/get", new DevicePoolGetAllHandler(pm));
		
		root.putHandler("dev", new DeviceGetHandler(pm));
		root.putHandler("dev", new DeviceSetHandler(pm));
		root.putHandler("dev", new DevicePutHandler(pm));
		root.putHandler("dev", new DeviceDelHandler(pm, sm));
		root.putHandler("dev/search", new DeviceSearchByDpid(pm));
		
		root.putHandler("userpool", new UserPoolGetHandler(pm));
		root.putHandler("userpool", new UserPoolSetHandler(pm));
		root.putHandler("userpool", new UserPoolPutHandler(pm));
		root.putHandler("userpool", new UserPoolDelHandler(pm));
		root.putHandler("userpool/get", new UserPoolGetAllHandler(pm));
		
		root.putHandler("user", new UserGetHandler(pm));
		root.putHandler("user", new UserSetHandler(pm));
		root.putHandler("user", new UserPutHandler(pm));
		root.putHandler("user", new UserDelHandler(pm));
		root.putHandler("user/search", new UserSearchByUpid(pm));
		
		root.putHandler("user/filter", new UserFilterGetHandler(pm));
		root.putHandler("user/filter", new UserFilterSetHandler(pm));
		root.putHandler("user/filter", new UserFilterPutHandler(pm));
		root.putHandler("user/filter", new UserFilterDelHandler(pm));
		root.putHandler("user/filter/search", new UserFilterSearchByUid(pm));
		
		root.putHandler("domain/search", new DomainSearchByTypeHandler(pm));
		
		root.putHandler("", new NfcHandler(aim));//root에 넣었음.
		
		root.putHandler("event/dmsg", new DeviceMsgStartEventHandler(cm, em, pm, sm));
		root.putHandler("event/dmsg", new DeviceMsgStopEventHandler(em));
		

		
		/** 2016/04/01 ADD 명화공업용 START */
		root.putHandler("plc/ins", new PlcInstanceStartHandler(aim));
		root.putHandler("plc/ins", new PlcInstanceStopHandler(aim));
		root.putHandler("plc/adt/get", new PlcAdapterGetAllHandler(am));
		root.putHandler("plc/ins/att", new PlcInsAttPutHandler(pm, sm));
		root.putHandler("plc/ins/att", new PlcInsAttDelHandler(pm, sm));
		root.putHandler("plc/ins/att/search", new PlcInsAttSearchByIidHandler(pm, em));
		 
		 root.putHandler("plc/gathering", new PlcGatheringStartEventHandler(cm, em, pm, aim));
		 root.putHandler("plc/gathering", new PlcGatheringStopEventHandler(em));
		 
		 root.putHandler("plc/monitoring", new PlcMonitoringStartEventHandler(cm, em, pm));
		 root.putHandler("plc/monitoring", new PlcMonitoringStopEventHandler(em));
		/** 2016/04/01 ADD 명화공업용 END */		
		
		return;
	}

	@Override
	public void initialize(IAdapterContext ctx) throws Exception{
		cm = ctx.getSessionManager().getConnectionManager();

		// 웹소켓 서버 초기화 루틴.
		IInstanceObj instanceInfo = ctx.getAdapterInstanceInfo();
		String ip = instanceInfo.getIp();
		String port = instanceInfo.getPort();
		String uri = instanceInfo.getUrl();
		
		this.logger = service.getLogger().logger(instanceInfo.getInsId());
		
		// 프로세서 초기화 루틴.
		processor = new WebsocketApiContextProcessor(service, ctx, cm);
		RootHandler root = processor.getRootHandler();

		// 경로에 따른 contextHandler들을 조합한다.
		createContextHandler(root, ctx);
		
		// 컨넥션 처리 및 파싱을 위한 핸들러를 조합한다.
		ConnectionHandleChain chc = new ConnectionHandleChain(service.getServicePool(), logger);
		chc.setRootHandler(new ConnectionManagerHandler(ctx.getSessionManager(), logger)).setNext(new CallContextProcessorHandler(processor, logger));

		
		// sample uri: ws://127.0.0.1:9999/api/1/2/3
		String path = "";//빈 패스 
		if(uri!=null && !uri.equals("")){
			URI uriObj = new URI(uri);
			path = uriObj.getPath();
		}
		webServerName = instanceInfo.getInsId();
//		server = new Server();
//		ServerConnector connector = new ServerConnector(server);
//		connector.setHost(ip);
//		connector.setPort(Integer.parseInt(port));
//		server.addConnector(connector);
//
//		ServletContextHandler svlCtx = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		svlCtx.setContextPath("/");
//		server.setHandler(svlCtx);

		ApiSocketServlet apiSocketServlet = new ApiSocketServlet(service.getServicePool(), logger, chc);

//		ServletHolder holderEvents = new ServletHolder("ws-events", apiSocketServlet);
//		svlCtx.addServlet(holderEvents, "/api");
		
//		wsPool.addSingletonServerConnector(ip, Integer.parseInt(port));
//		wsPool.addSingletonServerServlet(uri, apiSocketServlet, path);
		wsPool.createServer(webServerName, uri, uri, apiSocketServlet);
		
		
		this.logger.info("initialize.");
	}

	@Override
	public void start(IAdapterContext ctx) throws Exception {
		//server.start();
		//wsPool.restartSingletonServer();
		Server server = wsPool.getServer(webServerName);
		if(server.isRunning() || server.isStarted()){
			// 이미 기동 중이므로 아무것도 안함.
			this.logger.info("already started.");
		}else{
			server.start();
			this.logger.info("start.");
		}
	}

	@Override
	public void stop(IAdapterContext ctx) throws Exception {
		//server.stop();
		wsPool.stop(webServerName);
		cm.dispose();
		this.logger.info("stop.");
	}

	@Override
	public void suspend(IAdapterContext ctx) throws Exception {

		this.logger.info("suspend.");
	}

	@Override
	public void dispose(IAdapterContext ctx) throws Exception {

		//server.destroy();
		cm.dispose();
		this.logger.info("dispose.");
	}

	@Override
	public IAdapterProcessor getProcessor() {
		return this.processor;
	}

}
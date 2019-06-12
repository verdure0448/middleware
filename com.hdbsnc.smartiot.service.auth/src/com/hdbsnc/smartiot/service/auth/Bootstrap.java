package com.hdbsnc.smartiot.service.auth;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.IServiceManager;
import com.hdbsnc.smartiot.service.auth.impl.AssFactory;
import com.hdbsnc.smartiot.service.master.IMasterService;

public class Bootstrap implements BundleActivator{
	
//	public static Log bootstrap_logger;
//	private ServiceTracker logTracker;
//	private ServiceTracker poolTracker;
////	private ServiceTracker ismTracker;
////	private ServiceTracker aimTracker;
//	private ServiceTracker pmTracker;
//	private ServiceTracker mssTracker;
//	private ServiceTracker wsTracker;
//	Ass ass;
	
	IServiceManager serviceManager;
	IServiceFactory  assFactory;
	//주석 테스트 
	@Override
	public void start(BundleContext ctx) throws Exception {
//		logTracker = new ServiceTracker(ctx, Log.class.getName(), null);
//		logTracker.open();
//		bootstrap_logger = (Log) logTracker.getService();
//		bootstrap_logger = bootstrap_logger.logger("com.hdbsnc.smartiot.service.auth");
//		
//		poolTracker = new ServiceTracker(ctx, ServicePool.class.getName(), null);
//		poolTracker.open();
//		ServicePool pool = (ServicePool) poolTracker.getService();
//		
////		ismTracker = new ServiceTracker(ctx, IIntegratedSessionManager.class.getName(), null);
////		ismTracker.open();
////		IIntegratedSessionManager ism = (IIntegratedSessionManager) ismTracker.getService();
////		
////		aimTracker = new ServiceTracker(ctx, IAdapterInstanceManager.class.getName(), null);
////		aimTracker.open();
////		IAdapterInstanceManager aim = (IAdapterInstanceManager) aimTracker.getService();
//		
//		mssTracker = new ServiceTracker(ctx, IMasterService.class.getName(), null);
//		mssTracker.open();
//		IMasterService mss = (IMasterService) mssTracker.getService();
//		
//		pmTracker = new ServiceTracker(ctx, IProfileManager.class.getName(), null);
//		pmTracker.open();
//		IProfileManager pm = (IProfileManager) pmTracker.getService();
//		
//		
//		wsTracker = new ServiceTracker(ctx, IWebservicePool.class.getName(), null);
//		wsTracker.open();
//		IWebservicePool wsPool = (IWebservicePool) wsTracker.getService();
//		
//		
//		
//		
//		ass = new Ass(pool, bootstrap_logger, pm, mss, wsPool);
//		
//		Map<String, String> params = new HashMap();
//		params.put(Ass.WEBSOCKET_IP, "localhost");
//		params.put(Ass.WEBSOCKET_PORT, "8899");
//		
//		params.put(Ass.TCPSOCKET_IP, "localhost");
//		params.put(Ass.TCPSOCKET_PORT, "8898");
//		
//		
//		ass.init(params);
//		ass.start();
//		bootstrap_logger.info("WebSocket, TcpSocket Auth Server start.");
		
		
		assFactory = new AssFactory(ctx);
		assFactory.registeService();
		
//		ServiceTracker serviceManagerTracker = new ServiceTracker(ctx, IServiceManager.class.getName(), null);
//		serviceManagerTracker.open();
//		serviceManager = (IServiceManager) serviceManagerTracker.waitForService(0);
//		serviceManagerTracker.close();
//		serviceManager.registeService(assFactory);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {	
//		ass.stop();
//		mssTracker.close();
//		logTracker.close();
//		poolTracker.close();
//		wsTracker.close();
////		ismTracker.close();
////		aimTracker.close();
		
//		serviceManager.unregisteService(assFactory);
	}
}

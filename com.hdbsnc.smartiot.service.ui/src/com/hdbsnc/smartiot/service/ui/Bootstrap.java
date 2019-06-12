package com.hdbsnc.smartiot.service.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.service.IServiceManager;
import com.hdbsnc.smartiot.service.ui.impl.AdminUiFactory;
import com.hdbsnc.smartiot.util.logger.Log;

public class Bootstrap implements BundleActivator{

	public static Log bootstrap_logger;
	private ServiceTracker logTracker;
	private ServiceTracker emTracker;
	private ServiceTracker wsTracker;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
//		logTracker = new ServiceTracker(bundleContext, Log.class.getName(), null);
//		logTracker.open();
//		bootstrap_logger = (Log) logTracker.getService();
//		bootstrap_logger = bootstrap_logger.logger("com.hdbsnc.smartiot.adapter.websocketapi");
//		
//		emTracker = new ServiceTracker<>(bundleContext, IEventManager.class.getName(), null);
//		emTracker.open();
//		IEventManager em = (IEventManager)emTracker.waitForService(0);
//		
//		wsTracker = new ServiceTracker(bundleContext, IWebservicePool.class.getName(), null);
//		wsTracker.open();
//		IWebservicePool wsPool = (IWebservicePool) wsTracker.waitForService(0);
//		
//		AdminUi ui = new AdminUi(wsPool);
//		ui.start();
//		
//		
//		
//		bootstrap_logger.info("Admin UI Started.");
		
		AdminUiFactory auiFactory = new AdminUiFactory(bundleContext);
		auiFactory.registeService();
		
//		ServiceTracker serviceManagerTracker = new ServiceTracker(bundleContext, IServiceManager.class.getName(), null);
//		serviceManagerTracker.open();
//		IServiceManager serviceManager = (IServiceManager) serviceManagerTracker.waitForService(0);
//		serviceManagerTracker.close();
//		serviceManager.registeService(auiFactory);
		
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
//		logTracker.close();
//		emTracker.close();
//		wsTracker.close();
		
	}
}

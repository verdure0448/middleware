package com.hdbsnc.smartiot.ism;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hdbsnc.smartiot.common.factory.IIntegratedSessionManagerFactory;

public class Bootstrap implements BundleActivator{

//	private ServiceRegistration ismService;
//	
//	public static Log bootstrap_logger;
//	private ServiceTracker logTracker;
//	private ServiceTracker poolTracker;
//	private ServiceTracker pmTracker;
//	private Ism ism;
	
	private ServiceRegistration ismFactoryService;
	
	@Override
	public void start(BundleContext ctx) throws Exception {
//		logTracker = new ServiceTracker(ctx, Log.class.getName(), null);
//		logTracker.open();
//		bootstrap_logger = (Log) logTracker.getService();
//		bootstrap_logger = bootstrap_logger.logger("com.hdbsnc.smartiot.ism");
//		
//		poolTracker = new ServiceTracker(ctx, ServicePool.class.getName(), null);
//		poolTracker.open();
//		ServicePool pool = (ServicePool) poolTracker.getService();
//		
//		pmTracker = new ServiceTracker(ctx, IProfileManager.class.getName(), null);
//		pmTracker.open();
//		IProfileManager pm = (IProfileManager) pmTracker.getService();
		
		
		// !!!! 주의
		// 서버이름은 설정파일에서 읽어와야 하고 중복이 되면 안됨!!!!
//		ism = new Ism(pm, pool, bootstrap_logger, "com.hdbsnc.smartiot.master.1");
		
//		ismService = ctx.registerService(IIntegratedSessionManager.class.getName(), ism, null);
		
		IsmFactory ismFactory = new IsmFactory();
		ismFactoryService = ctx.registerService(IIntegratedSessionManagerFactory.class.getName(), ismFactory, null);
		
		System.out.println("registerService: "+IIntegratedSessionManagerFactory.class.getName());
		
//		bootstrap_logger.info("통합세션관리자 등록완료.");
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {	
//		ismService.unregister();
//		ism.cancelAll();
		
//		logTracker.close();
//		poolTracker.close();
//		pmTracker.close();
		
		ismFactoryService.unregister();
		
	}
	
	

}

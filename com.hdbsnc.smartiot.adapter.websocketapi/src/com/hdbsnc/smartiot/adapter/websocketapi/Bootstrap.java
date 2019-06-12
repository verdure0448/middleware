package com.hdbsnc.smartiot.adapter.websocketapi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.common.am.AmException;
import com.hdbsnc.smartiot.common.am.IAdapter;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;

public class Bootstrap implements BundleActivator{

//	public static Log bootstrap_logger;
//	private ServiceTracker logTracker;
//	private ServiceTracker poolTracker;
//	private ServiceTracker pmTracker;
//	private ServiceTracker amTracker;
//	private ServiceTracker emTracker;
//	private ServiceTracker wsTracker;
	IAdapter adapter;
	IAdapterManager am;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
//		logTracker = new ServiceTracker(bundleContext, Log.class.getName(), null);
//		logTracker.open();
//		bootstrap_logger = (Log) logTracker.getService();
//		bootstrap_logger = bootstrap_logger.logger("com.hdbsnc.smartiot.adapter.websocketapi");
//		
//		poolTracker = new ServiceTracker(bundleContext, ServicePool.class.getName(), null);
//		poolTracker.open();
//		ServicePool pool = (ServicePool) poolTracker.getService();
		
//		ServiceTracker pmTracker = new ServiceTracker(bundleContext, IProfileManager.class.getName(), null);
//		pmTracker.open();
//		IProfileManager pm = (IProfileManager) pmTracker.waitForService(0);
//		pmTracker.close();
//		
//		ServiceTracker amTracker = new ServiceTracker(bundleContext, IAdapterManager.class.getName(), null);
//		amTracker.open();
//		am = (IAdapterManager) amTracker.waitForService(0);
//		amTracker.close();
//		
//		ServiceTracker emTracker = new ServiceTracker<>(bundleContext, IEventManager.class.getName(), null);
//		emTracker.open();
//		IEventManager em = (IEventManager)emTracker.waitForService(0);
//		emTracker.close();
//		
//		ServiceTracker wsTracker = new ServiceTracker(bundleContext, IWebservicePool.class.getName(), null);
//		wsTracker.open();
//		IWebservicePool wsPool = (IWebservicePool) wsTracker.waitForService(0);
//		wsTracker.close();
		
		
//		adapter = new WebsocketApiAdapter(bundleContext.getBundle().getHeaders(),pm, am, em, wsPool);
//		try{
//			am.regAdapter(adapter);
//		}catch(AmException e){
//			System.err.println("아답터 등록 실패.("+adapter.getAdapterId()+")");
//			return;
//		}
//		bootstrap_logger.info("아답터 등록 완료.("+adapter.getAdapterId()+")");
		
		adapter = new WebsocketApiAdapter(bundleContext);
		adapter.registe();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
//		logTracker.close();
//		poolTracker.close();
//		pmTracker.close();
//		amTracker.close();
//		emTracker.close();
//		wsTracker.close();
//		am.unregAdapter(adapter);
		adapter.unregiste();
	}

}

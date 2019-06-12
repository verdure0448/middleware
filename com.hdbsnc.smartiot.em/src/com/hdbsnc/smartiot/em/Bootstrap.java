package com.hdbsnc.smartiot.em;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.factory.IEventManagerFactory;
import com.hdbsnc.smartiot.util.logger.Log;

public class Bootstrap implements BundleActivator{

	private ServiceRegistration emService;
	public static Log bootstrap_logger;
	private ServiceTracker logTracker;
	private ServiceTracker poolTracker;
	private ServiceTracker pmTracker;
	private IEventManager em;
	
	private ServiceRegistration emFactoryService;
	
	@Override
	public void start(BundleContext ctx) throws Exception {
//		logTracker = new ServiceTracker(ctx, Log.class.getName(), null);
//		logTracker.open();
//		bootstrap_logger = (Log) logTracker.getService();
//		bootstrap_logger = bootstrap_logger.logger("com.hdbsnc.smartiot.em");
//		
//		poolTracker = new ServiceTracker(ctx, ServicePool.class.getName(), null);
//		poolTracker.open();
//		ServicePool pool = (ServicePool) poolTracker.getService();
//		
//		pmTracker = new ServiceTracker(ctx, IProfileManager.class.getName(), null);
//		pmTracker.open();
//		IProfileManager pm = (IProfileManager) pmTracker.getService();
//		
//		IEventManager em = new Em(bootstrap_logger);
//		em.start();
//		emService = ctx.registerService(IEventManager.class.getName(), em, null);
//		bootstrap_logger.info("이벤트관리자 등록완료.");
		
		EmFactory emFactory = new EmFactory();
		emFactoryService = ctx.registerService(IEventManagerFactory.class.getName(), emFactory, null);
		System.out.println("registerService: "+IEventManagerFactory.class.getName());
		
//		em.addEventConsumer(new IEventConsumer(){
//
//			@Override
//			public void updateEvent(IEvent event) {
//				//System.out.println("[1] event 수신: "+event.eventID());
//				ISystemEvent sevt = (ISystemEvent) event;
//				System.out.println("[1] event 수신: "+event.eventID()+ " "+sevt.eventCodeToUser());
//				
//				if(sevt.isContainsContents()){
//					IInstanceObj insObj = (IInstanceObj) sevt.contents();
//					System.out.println("[1] "+insObj.getInsId()+" ("+sevt.eventCodeToSystem()+") "+sevt.eventCodeToUser());
//				}
//			}
//			
//		}, IEvent.MODULE_PDM | IEvent.EVENT_INSTANCE_LIFECYCLE | IEvent.ALL | IEvent.STATE_COMPLETED);
//		
//		em.addEventConsumer(new IEventConsumer(){
//
//			@Override
//			public void updateEvent(IEvent event) {
//				//System.out.println("[2] event 수신: "+event.eventID());
//				ISystemEvent sevt = (ISystemEvent) event;
//				System.out.println("[2] event 수신: "+event.eventID()+ " "+sevt.eventCodeToUser());
//				
//				if(sevt.isContainsContents()){
//					Object obj = sevt.contents();
//					if(obj instanceof IInstanceObj){
//						IInstanceObj insObj = (IInstanceObj) obj;
//						System.out.println("[2] "+insObj.getInsId()+" ("+sevt.eventCodeToSystem()+") "+sevt.eventCodeToUser());
//					}else if(obj instanceof IContext){
//						IContext ctx = (IContext) obj;
//						System.out.println(ctx.getFullPath());
//					}
//				}
//			}
//			
//		}, "1.4.0.0");
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {	
//		emService.unregister();
//		em.stop();
//		
//		logTracker.close();
//		poolTracker.close();
//		pmTracker.close();
		
		emFactoryService.unregister();
		
	}
	
	

}

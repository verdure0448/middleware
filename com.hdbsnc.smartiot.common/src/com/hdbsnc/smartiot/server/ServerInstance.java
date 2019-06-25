package com.hdbsnc.smartiot.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.common.CommonServiceImpl;
import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.ecm.IEventContextManager;
import com.hdbsnc.smartiot.common.em.AbstractEventProducer;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.exception.CommonExceptionFactory;
import com.hdbsnc.smartiot.common.factory.IAdapterInstanceManagerFactory;
import com.hdbsnc.smartiot.common.factory.IAdapterManagerFactory;
import com.hdbsnc.smartiot.common.factory.ICommonExceptionFactory;
import com.hdbsnc.smartiot.common.factory.IEventContextManagerFactory;
import com.hdbsnc.smartiot.common.factory.IEventManagerFactory;
import com.hdbsnc.smartiot.common.factory.IIntegratedSessionManagerFactory;
import com.hdbsnc.smartiot.common.factory.IProfileManagerFactory;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.IServiceManager;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class ServerInstance implements Runnable, IServerInstance, IServiceManager{

	private List<IServiceFactory> serviceFactoryList;
	private Map<String, IService> serviceMap;
	private Map<String, String> config;
	private BundleContext context;
	private Log rootLogger;
	private ServicePool tPool;
	private IWebservicePool wPool;
	private CommonServiceImpl commonService;
	
	IIntegratedSessionManager ism;
	IAdapterManager am;
	IAdapterInstanceManager aim;
	IEventManager em;
	IProfileManager pm;
	IEventContextManager ecm;
	ICommonExceptionFactory cef;
	
	int currentState = IServerInstance.SERVERINSTANCE_STATE_CREATE;
	
	private ServiceRegistration pmSr;
	private ServiceRegistration ismSr;
	private ServiceRegistration amSr;
	private ServiceRegistration aimSr;
	private ServiceRegistration emSr;
	private ServiceRegistration ecmSr;
	private ServiceRegistration serviceManagerSr;
	
	private String serverInstanceName;
	
	public ServerInstance(BundleContext context, CommonServiceImpl commonService){
		this.serviceFactoryList = new ArrayList<IServiceFactory>();
		this.serviceMap = new Hashtable<String, IService>();
		this.context = context;
		this.commonService = commonService;
		this.rootLogger = commonService.getLogger();
		this.tPool = commonService.getServicePool();
		this.wPool = commonService.getWebservicePool();
	}
	
	public String getServerInstanceName(){
		return this.serverInstanceName;
	}
	
	@Override
	public void run() {
		
		serverInstanceName = config.get("server.id");
		rootLogger.info("Creating ServerInstance: "+serverInstanceName);
		ServiceTracker tracker;
		try {			
			tracker = new ServiceTracker(context, IProfileManagerFactory.class.getName(), null);
			tracker.open();
			IProfileManagerFactory pmFactory = (IProfileManagerFactory) tracker.waitForService(0);
			tracker.close();
			pm = pmFactory.createPm(commonService, config);
			pmSr = context.registerService(IProfileManager.class.getName(), pm, null);
			rootLogger.info("01 module("+IProfileManager.class.getName()+"): "+pm.toString());
			
			// 공통서비스에 공통예외 팩토리 등록
			commonService.setExceptionfactory(new CommonExceptionFactory(pm));
			
			tracker = new ServiceTracker(context, IIntegratedSessionManagerFactory.class.getName(), null);
			tracker.open();
			IIntegratedSessionManagerFactory ismFactory = (IIntegratedSessionManagerFactory) tracker.waitForService(0);
			tracker.close();
			ism = ismFactory.createISM(commonService, pm, serverInstanceName);
			ismSr = context.registerService(IIntegratedSessionManager.class.getName(), ism, null);
			rootLogger.info("02 module("+IIntegratedSessionManager.class.getName()+"): "+ism.toString());
			
			tracker = new ServiceTracker(context, IAdapterManagerFactory.class.getName(), null);
			tracker.open();
			IAdapterManagerFactory amFactory = (IAdapterManagerFactory) tracker.waitForService(0);
			tracker.close();
			am = amFactory.createAM(commonService, config);
			amSr = context.registerService(IAdapterManager.class.getName(), am, null);
			rootLogger.info("03 module("+IAdapterManager.class.getName()+"): "+am.toString());
			
			tracker = new ServiceTracker(context, IAdapterInstanceManagerFactory.class.getName(), null);
			tracker.open();
			IAdapterInstanceManagerFactory aimFactory = (IAdapterInstanceManagerFactory) tracker.waitForService(0);
			tracker.close();
			aim = aimFactory.createAIM(commonService, am, pm, ism);
			aimSr = context.registerService(IAdapterInstanceManager.class.getName(), aim, null);
			rootLogger.info("04 module("+IAdapterInstanceManager.class.getName()+"): "+aim.toString());
			
			tracker = new ServiceTracker(context, IEventManagerFactory.class.getName(), null);
			tracker.open();
			IEventManagerFactory emFactory = (IEventManagerFactory) tracker.waitForService(0);
			tracker.close();
			em = emFactory.createEM(commonService, this);
			em.start();
			emSr = context.registerService(IEventManager.class.getName(), em, null);
			rootLogger.info("05 module("+IEventManager.class.getName()+"): "+em.toString());
			
//			tracker = new ServiceTracker(context, IEventContextManagerFactory.class.getName(), null);
//			tracker.open();
//			IEventContextManagerFactory ecmFactory = (IEventContextManagerFactory) tracker.waitForService(0);
//			tracker.close();
//			ecm = ecmFactory.createECM(commonService, this);
//			ecmSr = context.registerService(IEventContextManager.class.getName(), ecm, null);
//			rootLogger.info("06 module("+IEventContextManager.class.getName()+"): "+ecm.toString());
			
			//tracker = new ServiceTracker(context, ICommonExceptionFactory.class.getName(), null);
			//tracker.open();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		serviceManagerSr = context.registerService(IServiceManager.class.getName(), this, null);
		rootLogger.info("07 module("+IServiceManager.class.getName()+"): "+this.toString());
		
		em.addEventProducer((AbstractEventProducer)am);
		em.addEventProducer((AbstractEventProducer)aim);
		//다른 모듈도 구현해서 등록 요망
		rootLogger.info("EventProducer 등록 완료.");
		//서비스 기동
//		em.addEventConsumer(new IEventConsumer(){
//
//			@Override
//			public void updateEvent(IEvent event) {
//				//System.out.println("[1] event 수신: "+event.eventID());
//				ISystemEvent sevt = (ISystemEvent) event;
////				System.out.println("["+this.getName()+"] event 수신: "+event.eventID()+ " "+sevt.eventCodeToUser());
//				
//				if(sevt.isContainsContents()){
//					IInstanceObj insObj = (IInstanceObj) sevt.contents();
////					System.out.println("["+this.getName()+"] "+insObj.getInsId()+" ("+sevt.eventCodeToSystem()+") "+sevt.eventCodeToUser());
//				}
//			}
//
//			@Override
//			public String getName() {
//				return "Test_EventConsumer_1.3.8.7";
//			}
//			
//		}, IEvent.MODULE_PDM | IEvent.EVENT_INSTANCE_LIFECYCLE | IEvent.ALL | IEvent.STATE_COMPLETED);
		
//		try {
//			em.addEventConsumer(new IEventConsumer(){
//
//				@Override
//				public void updateEvent(IEvent event) {
//					//System.out.println("[2] event 수신: "+event.eventID());
//					ISystemEvent sevt = (ISystemEvent) event;
////					System.out.println("["+this.getName()+"] event 수신: "+event.eventID()+ " "+sevt.eventCodeToUser());
//					
//					if(sevt.isContainsContents()){
//						Object obj = sevt.contents();
//						if(obj instanceof IInstanceObj){
//							IInstanceObj insObj = (IInstanceObj) obj;
////							System.out.println("["+this.getName()+"] "+insObj.getInsId()+" ("+sevt.eventCodeToSystem()+") "+sevt.eventCodeToUser());
//						}else if(obj instanceof IContext){
//							IContext ctx = (IContext) obj;
//							System.out.println(ctx.getFullPath());
//						}
//					}
//				}
//				
//				@Override
//				public String getName() {
//					return "Test_EventConsumer_1.4.0.0";
//				}
//				
//			}, "1.4.0.0");
//		} catch (EventFilterFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public IAdapterManager getAM() {
		return this.am;
	}

	@Override
	public IAdapterInstanceManager getAIM() {
		return this.aim;
	}

	@Override
	public IIntegratedSessionManager getISM() {
		return this.ism;
	}

	@Override
	public IProfileManager getPM() {
		return this.pm;
	}

	@Override
	public IEventManager getEM() {
		return this.em;
	}

	@Override
	public ICommonService getCommonService() {
		return this.commonService;
	}

	@Override
	public int getServerInstanceState() {
		return this.currentState;
	}

	@Override
	public void init(Map config) throws Exception {
		this.currentState = IServerInstance.SERVERINSTANCE_STATE_INIT;
		this.config = config;
	}

	@Override
	public void start() throws Exception {
		this.currentState = IServerInstance.SERVERINSTANCE_STATE_START;
		tPool.execute(this);
	}

	@Override
	public void stop() throws Exception {
		this.currentState = IServerInstance.SERVERINSTANCE_STATE_STOP;
		this.em.stop();
		// 각 모듈별로 자원 해제 메소드 호출해서 정상 종료
		
		unregisteServiceAll();
		// 서비스를 제거 
		serviceManagerSr.unregister();
		emSr.unregister();
		aimSr.unregister();
		amSr.unregister();
		ismSr.unregister();
		pmSr.unregister();
		ecmSr.unregister();
	}
	
	public List<IService> getServiceList(){
		return new ArrayList<IService>(this.serviceMap.values());
	}

	@Override
	public synchronized void registeService(IServiceFactory serviceFactory) throws Exception {
		this.serviceFactoryList.add(serviceFactory);
		if(this.serviceMap.containsKey(serviceFactory.getServiceName())){
			IService oldService = serviceMap.get(serviceFactory.getServiceName());
			oldService.stop();
		}
		IService newService = serviceFactory.createService(this);
		newService.init(config);
		newService.start();
		this.serviceMap.put(serviceFactory.getServiceName(), newService);
		rootLogger.info("Registed Service and Start: "+serviceFactory.getServiceName());
	}

	@Override
	public synchronized void unregisteService(IServiceFactory serviceFactory) throws Exception {
		this.serviceFactoryList.remove(serviceFactory);
		if(this.serviceMap.containsKey(serviceFactory.getServiceName())){
			IService oldService = serviceMap.remove(serviceFactory.getServiceName());
			oldService.stop();
		}
		rootLogger.info("Unregisted Service and Stop: "+serviceFactory.getServiceName());
	}

	@Override
	public List<IServiceFactory> getServiceFactoryList() {
		return this.serviceFactoryList;
	}

	@Override
	public void unregisteServiceAll() throws Exception{
		this.serviceFactoryList.clear();
		Iterator<IService> iter = this.serviceMap.values().iterator();
		IService service;
		while(iter.hasNext()){
			service = iter.next();
			service.stop();
			iter.remove();
		}
		rootLogger.info("Unregisted Service and Stop All");
	}

}

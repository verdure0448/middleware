package com.hdbsnc.smartiot.service.ui.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.IServiceManager;

public class AdminUiFactory implements IServiceFactory, Runnable{

	private BundleContext ctx;
	
	public AdminUiFactory(BundleContext ctx){
		this.ctx = ctx;
	}
	
	@Override
	public String getServiceName() {
		return "AdminUI";
	}

	@Override
	public IService createService(IServerInstance serverInstance) {
		return new AdminUi(serverInstance.getCommonService().getWebservicePool(), ctx);
	}

	@Override
	public void registeService(){
		new Thread(this).start();
	}
	
	public void run(){
		ServiceTracker serviceManagerTracker = new ServiceTracker(ctx, IServiceManager.class.getName(), null);
		serviceManagerTracker.open();
		
		IServiceManager serviceManager;
		try {
			serviceManager = (IServiceManager) serviceManagerTracker.waitForService(0);
			serviceManager.registeService(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		serviceManagerTracker.close();
	}
}

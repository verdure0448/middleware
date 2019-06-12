package com.hdbsnc.smartiot.service.slave.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.IServiceManager;

public class SssFactory implements IServiceFactory, Runnable{

	private BundleContext ctx;
	
	public SssFactory(BundleContext ctx){
		this.ctx = ctx;
	}
	
	@Override
	public String getServiceName() {
		return "SSS";
	}

	@Override
	public IService createService(IServerInstance serverInstance) {
		return new Sss2(ctx, serverInstance);
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

package com.hdbsnc.smartiot.service.master.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.IServiceManager;

public class MssFactory implements IServiceFactory, Runnable{

	private BundleContext ctx;
	
	public MssFactory(BundleContext ctx){
		this.ctx = ctx;
	}
	
	@Override
	public String getServiceName() {
		return "MSS";
	}

	@Override
	public IService createService(IServerInstance serverInstance) {
		Mss2 mss = new Mss2(ctx, serverInstance);
		return mss;
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

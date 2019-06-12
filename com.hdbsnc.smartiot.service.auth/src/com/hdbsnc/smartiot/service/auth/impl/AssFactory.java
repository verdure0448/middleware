package com.hdbsnc.smartiot.service.auth.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.IServiceManager;
import com.hdbsnc.smartiot.service.master.IMasterService;

public class AssFactory implements IServiceFactory, Runnable{

	private IMasterService mss;
	private BundleContext ctx;
	
	public AssFactory(BundleContext ctx){
		this.ctx = ctx;
	}
	
	@Override
	public String getServiceName() {
		return "ASS";
	}

	@Override
	public IService createService(IServerInstance serverInstance) {
		return new Ass2(serverInstance, mss);
	}
	
	@Override
	public void registeService(){
		new Thread(this).start();
	}
	
	public void run(){
		ServiceTracker mssTracker = new ServiceTracker(ctx, IMasterService.class.getName(), null);
		mssTracker.open();
		
		ServiceTracker serviceManagerTracker = new ServiceTracker(ctx, IServiceManager.class.getName(), null);
		serviceManagerTracker.open();
		
		IServiceManager serviceManager;
		try {
			this.mss = (IMasterService) mssTracker.waitForService(0);
			serviceManager = (IServiceManager) serviceManagerTracker.waitForService(0);
			serviceManager.registeService(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mssTracker.close();
		serviceManagerTracker.close();
	}

}

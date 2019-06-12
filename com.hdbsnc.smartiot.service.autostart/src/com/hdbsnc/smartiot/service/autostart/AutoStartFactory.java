package com.hdbsnc.smartiot.service.autostart;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.server.IServerInstance;
import com.hdbsnc.smartiot.service.IService;
import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.IServiceManager;
import com.hdbsnc.smartiot.service.slave.ISlaveService;

public class AutoStartFactory implements IServiceFactory{
	private BundleContext ctx;
	private IServiceFactory self = this;
	
	public AutoStartFactory(BundleContext ctx){
		this.ctx = ctx;
	}

	@Override
	public String getServiceName() {
		return "AutoStart";
	}

	@Override
	public IService createService(IServerInstance serverInstance) {
		return new AutoStart(serverInstance);
	}

	@Override
	public void registeService() {
		new Thread(new Runnable(){

			@Override
			public void run() {
				ServiceTracker slaveServiceTracker = new ServiceTracker(ctx, ISlaveService.class.getName(), null);
				slaveServiceTracker.open();
				//슬래이브 서비스가 등록되어 있지 않다면 auto 스타트를 실행하면 안된다. 
				
				ServiceTracker serviceManagerTracker = new ServiceTracker(ctx, IServiceManager.class.getName(), null);
				serviceManagerTracker.open();
				
				IServiceManager serviceManager;
				ISlaveService slaveService;
				try {
					slaveService = (ISlaveService) slaveServiceTracker.waitForService(0);
					serviceManager = (IServiceManager) serviceManagerTracker.waitForService(0);
					serviceManager.registeService(self);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				slaveServiceTracker.close();
				serviceManagerTracker.close();
				
			}
			
		}).start();
		
	}

}

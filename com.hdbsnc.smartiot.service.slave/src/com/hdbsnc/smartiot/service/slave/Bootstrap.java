package com.hdbsnc.smartiot.service.slave;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.slave.impl.SssFactory;
import com.hdbsnc.smartiot.util.logger.Log;

public class Bootstrap implements BundleActivator{
	
	
	@Override
	public void start(BundleContext ctx) throws Exception {

		
		IServiceFactory sssFactory = new SssFactory(ctx);
		sssFactory.registeService();
		
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {	
		
		
	}

}

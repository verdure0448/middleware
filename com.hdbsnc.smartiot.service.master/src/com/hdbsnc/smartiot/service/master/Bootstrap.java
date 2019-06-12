package com.hdbsnc.smartiot.service.master;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.hdbsnc.smartiot.service.IServiceFactory;
import com.hdbsnc.smartiot.service.master.impl.MssFactory;

public class Bootstrap implements BundleActivator{
	
	
	
	@Override
	public void start(BundleContext ctx) throws Exception {

		
		IServiceFactory mssFactory = new MssFactory(ctx);
		mssFactory.registeService();
		
		
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {	

		
	}

}

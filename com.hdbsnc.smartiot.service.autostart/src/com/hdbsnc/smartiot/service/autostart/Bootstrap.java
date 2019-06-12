package com.hdbsnc.smartiot.service.autostart;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Bootstrap implements BundleActivator{

	AutoStartFactory asFactory;
	
	@Override
	public void start(BundleContext ctx) throws Exception {
		AutoStartFactory asFactory = new AutoStartFactory(ctx);
		asFactory.registeService();
		
	}

	@Override
	public void stop(BundleContext ctx) throws Exception {
		
		
	}

}

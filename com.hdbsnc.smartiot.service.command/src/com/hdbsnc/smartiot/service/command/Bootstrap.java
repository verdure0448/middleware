package com.hdbsnc.smartiot.service.command;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Bootstrap implements BundleActivator{

	@Override
	public void start(BundleContext ctx) throws Exception {
		CommandServiceFactory asFactory = new CommandServiceFactory(ctx);
		asFactory.registeService();
	}

	@Override
	public void stop(BundleContext ctx) throws Exception {
		
		
	}

}

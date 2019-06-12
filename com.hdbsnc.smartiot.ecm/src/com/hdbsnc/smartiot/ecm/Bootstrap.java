package com.hdbsnc.smartiot.ecm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hdbsnc.smartiot.common.factory.IEventContextManagerFactory;

public class Bootstrap implements BundleActivator{

	private ServiceRegistration sr;
	
	@Override
	public void start(BundleContext ctx) throws Exception {
		
		EcmFactory ecmFactory = new EcmFactory();
		sr = ctx.registerService(IEventContextManagerFactory.class.getName(), ecmFactory, null);
		System.out.println("registerService: "+IEventContextManagerFactory.class.getName());
	}

	@Override
	public void stop(BundleContext ctx) throws Exception {
		sr.unregister();
		
	}
	
	
}

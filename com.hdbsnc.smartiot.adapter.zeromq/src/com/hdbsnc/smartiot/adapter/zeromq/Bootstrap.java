package com.hdbsnc.smartiot.adapter.zeromq;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import com.hdbsnc.smartiot.common.am.IAdapter;

public class Bootstrap implements BundleActivator{
	IAdapter adapter;
	/**
	 * 테스트 
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		adapter = new ZeroMqAdapter(bundleContext);
		adapter.registe();
		
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		adapter.unregiste();
		
	}
}

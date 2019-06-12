package com.hdbsnc.smartiot.pdm;

import java.util.Map;

import org.osgi.framework.BundleContext;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.factory.IAdapterInstanceManagerFactory;
import com.hdbsnc.smartiot.common.factory.IAdapterManagerFactory;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.pdm.aim.impl.Aim;
import com.hdbsnc.smartiot.pdm.am.impl.Am;

public class PdmFactory implements IAdapterManagerFactory, IAdapterInstanceManagerFactory{

	private BundleContext ctx;
	PdmFactory(BundleContext ctx){
		this.ctx = ctx;
	}
	
	@Override
	public IAdapterInstanceManager createAIM(ICommonService service, IAdapterManager am1, IProfileManager pm2,
			IIntegratedSessionManager ism) {
		return new Aim(am1, pm2, ism, service);
	}

	@Override
	public IAdapterManager createAM(ICommonService service, Map<String, String> config) {
		return new Am(service, config, ctx);
	}

}

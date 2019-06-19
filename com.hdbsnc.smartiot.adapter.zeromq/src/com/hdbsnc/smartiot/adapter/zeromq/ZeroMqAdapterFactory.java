package com.hdbsnc.smartiot.adapter.zeromq;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.factory.IAdapterFactory;
import com.hdbsnc.smartiot.common.pm.IProfileManager;

public class ZeroMqAdapterFactory implements IAdapterFactory{

	private ICommonService service;
	private IEventManager em;
	private IProfileManager pm;
	
	public ZeroMqAdapterFactory(ICommonService service,IEventManager em, IProfileManager pm){
		this.service = service;
		this.em=em;
		this.pm = pm;
	}
	
	@Override
	public IAdapterInstance createInstance() {
		return new ZeroMqAdapterInstance(service,em,pm);
	}

}

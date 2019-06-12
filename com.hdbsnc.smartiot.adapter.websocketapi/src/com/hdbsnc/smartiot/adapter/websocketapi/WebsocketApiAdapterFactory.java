package com.hdbsnc.smartiot.adapter.websocketapi;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstance;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.factory.IAdapterFactory;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;

public class WebsocketApiAdapterFactory implements IAdapterFactory{
	private IProfileManager pm;
	private ICommonService service;
	private IAdapterManager am;
	private IEventManager em;
	private IWebservicePool wsPool;
	
	public WebsocketApiAdapterFactory(ICommonService service, IProfileManager pm, IAdapterManager am, IEventManager em){
		this.service = service;
		this.pm = pm;
		this.am = am;
		this.em = em;
	}

	@Override
	public IAdapterInstance createInstance() {
		return new WebsocketApiAdapterInstance(service, pm, am, em);
	}

}

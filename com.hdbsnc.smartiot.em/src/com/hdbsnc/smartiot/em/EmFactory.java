package com.hdbsnc.smartiot.em;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.factory.IEventManagerFactory;
import com.hdbsnc.smartiot.em.impl.Em;
import com.hdbsnc.smartiot.server.ServerInstance;

public class EmFactory implements IEventManagerFactory{

	@Override
	public IEventManager createEM(ICommonService commonService, ServerInstance server) {
		return new Em(commonService.getLogger(), server.getISM(), server.getAIM());
	}

}

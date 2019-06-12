package com.hdbsnc.smartiot.common.factory;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.server.ServerInstance;

public interface IEventManagerFactory {

	IEventManager createEM(ICommonService service, ServerInstance server);
}

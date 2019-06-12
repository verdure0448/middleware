package com.hdbsnc.smartiot.common.factory;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.ecm.IEventContextManager;
import com.hdbsnc.smartiot.server.ServerInstance;

public interface IEventContextManagerFactory {

	IEventContextManager createECM(ICommonService service, ServerInstance server);
}

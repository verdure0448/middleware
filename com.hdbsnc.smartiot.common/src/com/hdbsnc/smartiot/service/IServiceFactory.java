package com.hdbsnc.smartiot.service;

import com.hdbsnc.smartiot.server.IServerInstance;

public interface IServiceFactory {

	String getServiceName();
	
	IService createService(IServerInstance serverInstance);
	
	void registeService();
}

package com.hdbsnc.smartiot.service;

import java.util.List;

public interface IServiceManager {

	void registeService(IServiceFactory serviceFactory) throws Exception;
	
	void unregisteService(IServiceFactory serviceFactory) throws Exception;
	
	List<IServiceFactory> getServiceFactoryList();
	
	void unregisteServiceAll() throws Exception;
}

package com.hdbsnc.smartiot.common;

import com.hdbsnc.smartiot.common.factory.ICommonExceptionFactory;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;
import com.hdbsnc.smartiot.common.webserver.impl.WebservicePool;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class CommonServiceImpl implements ICommonService {

	
	private ServicePool servicePool;
	private WebservicePool wsPool;
	private ICommonExceptionFactory iComExFactory;
	private Log log;
	
	private Log logback;
	
	@Override
	public IWebservicePool getWebservicePool() {
		return this.wsPool;
	}
	
	public void setWebservicePool(WebservicePool wsPool) {
		this.wsPool = wsPool;
	}

	@Override
	public ServicePool getServicePool() {
		return servicePool;
	}
	
	public void setServicePool(ServicePool servicePool) {
		this.servicePool = servicePool;
	}

	@Override
	public Log getLogger() {
		return log;
	}

	public void setLogger(Log log){
		this.log = log;
	}
	
	@Override
	public ICommonExceptionFactory getExceptionfactory() {
		return iComExFactory;
	}
	
	public void setExceptionfactory(ICommonExceptionFactory iComExFactory) {
		this.iComExFactory = iComExFactory;
	}

	public void setLogBack(Log logback){
		this.logback = logback;
	}
}

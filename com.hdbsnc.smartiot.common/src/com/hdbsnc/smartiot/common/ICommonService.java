package com.hdbsnc.smartiot.common;

import com.hdbsnc.smartiot.common.factory.ICommonExceptionFactory;
import com.hdbsnc.smartiot.common.webserver.IWebservicePool;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public interface ICommonService {
	
	IWebservicePool getWebservicePool();

	ServicePool getServicePool();
	
	Log getLogger();
	
	ICommonExceptionFactory getExceptionfactory();
	
	//미들웨어 기능중 아답터 개발자들에게 오픈할 기능들이 있으면 여기에 추가한다.
}

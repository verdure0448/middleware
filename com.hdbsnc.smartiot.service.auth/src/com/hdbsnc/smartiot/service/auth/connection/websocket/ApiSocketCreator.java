package com.hdbsnc.smartiot.service.auth.connection.websocket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import com.hdbsnc.smartiot.service.auth.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class ApiSocketCreator implements WebSocketCreator{

	private ServicePool pool;
	private Log logger;
	private ConnectionHandleChain chc;
	
	public ApiSocketCreator(ServicePool pool, Log logger, ConnectionHandleChain chc){
		this.pool = pool;
		this.logger = logger;
		this.chc = chc;
	}
	
	@Override
	public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
		
		
		return new ApiSocket(pool, logger, chc);
	}

}

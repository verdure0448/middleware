package com.hdbsnc.smartiot.service.auth.connection.websocket;

import java.util.Map;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.hdbsnc.smartiot.service.auth.connection.impl.ConnectionHandleChain;
import com.hdbsnc.smartiot.util.logger.Log;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;

public class ApiSocketServlet extends WebSocketServlet{
	
	private ServicePool pool;
	private Log logger;
	private ConnectionHandleChain chc;
	
	public ApiSocketServlet(ServicePool pool, Log logger, ConnectionHandleChain chc){
		super();
		this.pool = pool;
		this.logger = logger;
		this.chc = chc;
		
	}
	
	public void init(Map<String, String> params){
		
	}
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.getPolicy().setIdleTimeout(1000*60);
		factory.setCreator(new ApiSocketCreator(pool, logger, chc));
		
	}

}

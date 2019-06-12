package com.hdbsnc.smartiot.adapter.websocketapi.connection.websocket;

import java.util.Map;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.hdbsnc.smartiot.adapter.websocketapi.connection.impl.ConnectionHandleChain_old;
import com.hdbsnc.smartiot.common.connection.impl.ConnectionHandleChain;
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
		factory.getPolicy().setIdleTimeout(0);
		factory.setCreator(new ApiSocketCreator(pool, logger, chc));
		
	}

}

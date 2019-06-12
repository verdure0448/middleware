package com.hdbsnc.smartiot.service.ui.impl;

import java.net.URL;
import java.util.Map;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.osgi.framework.BundleContext;

import com.hdbsnc.smartiot.common.webserver.IWebservicePool;
import com.hdbsnc.smartiot.service.IService;

public class AdminUi implements IService{
	
	private IWebservicePool wsPool;
	private ResourceHandler handler;
	private int currentState = SERVICE_STATE_REG;
	private long lastAccessedTime = 0;
	private BundleContext ctx;
	
	public AdminUi(IWebservicePool wsPool, BundleContext ctx){
		this.wsPool = wsPool;
		this.lastAccessedTime = System.currentTimeMillis();
		this.ctx = ctx;
	}
	
	@Override
	public void init(Map<String, String> config) throws Exception {
		this.currentState = SERVICE_STATE_INIT;
		handler = new ResourceHandler();
		handler.setDirectoriesListed(true);
		handler.setWelcomeFiles(new String [] { "index.html" });
		URL fileUrl = ctx.getBundle().getEntry("/webapp");

//		URL fileUrl = this.getClass().getResource("/webapp");
		BundleResource br = new BundleResource(fileUrl);
		handler.setBaseResource(br);
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	public void start() throws Exception{
		this.currentState = SERVICE_STATE_START;
		Server server = wsPool.getSingletonServer();
		if(server.isRunning() || server.isStarting() || server.isStarted()) server.stop();
		Handler[] handlerArrays = server.getHandlers();
		HandlerList handlers = new HandlerList();
		if(handlerArrays!=null){
			for(int i=0;i<handlerArrays.length;i++){
				handlers.addHandler(handlerArrays[i]);
			}
		}
		handlers.addHandler(handler);
		handlers.addHandler(new DefaultHandler());
		
		server.setHandler(handlers);
		server.start();
		this.lastAccessedTime = System.currentTimeMillis();
	}
	
	public void stop() throws Exception{
		this.currentState = SERVICE_STATE_STOP;
		Server server = wsPool.getSingletonServer();
		if(server.isRunning() || server.isStarting() || server.isStarted()) server.stop();
		Handler[] handlerArrays = server.getHandlers();
		HandlerList handlers = new HandlerList();
		if(handlerArrays!=null){
			for(int i=0;i<handlerArrays.length;i++){
				if(!handler.equals(handlerArrays[i])) handlers.addHandler(handlerArrays[i]);
			}
		}
		server.setHandler(handlers);
		server.start();
		this.lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public String getServiceName() {
		return "AdminUiService";
	}

	@Override
	public int getServiceState() {
		return this.currentState;
	}

	@Override
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

}

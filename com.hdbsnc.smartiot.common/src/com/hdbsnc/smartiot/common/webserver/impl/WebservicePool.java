package com.hdbsnc.smartiot.common.webserver.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.hdbsnc.smartiot.common.webserver.IWebservicePool;

public class WebservicePool implements IWebservicePool{

	Map<String, Server> serverMap;
	Server server;
	ServletContextHandler svlCtx;
	String localhostWsIp;
	String assWsIp;
	
	public WebservicePool(String localhostWsIp, String assWsIp){
		this.serverMap = new Hashtable<String, Server>();
		this.server = null;
		this.svlCtx = null;
		this.localhostWsIp = localhostWsIp;
		this.assWsIp = assWsIp;
	}
	
	public boolean containServer(String serverName){
		return serverMap.containsKey(serverName);
	}
	
	public Server getServer(String serverName){
		return serverMap.get(serverName);
	}
	
	public void start(String serverName) throws Exception{
		Server temp = serverMap.get(serverName);
		if(temp!=null) {
			temp.start();
		}else{
			throw new Exception("서버 START 실패. 서버가 존재하지 않습니다.("+serverName+")");
		}
	}
	
	public void stop(String serverName) throws Exception{
		Server temp = serverMap.get(serverName);
		if(temp!=null) {
			temp.stop();
		}else{
			throw new Exception("서버 STOP 실패. 서가 존재하지 않습니다.("+serverName+")");
		}
	}
	
	public void restart(String serverName) throws Exception{
		Server temp = serverMap.get(serverName);
		if(temp!=null) {
			temp.stop();
		}else{
			throw new Exception("서버 STOP 실패. 서가 존재하지 않습니다.("+serverName+")");
		}
		
		if(temp.isRunning() || temp.isStarting() || temp.isStarted()){
			temp.stop();
		}
	}
	
	public Server stopAndDestroyServer(String serverName) throws Exception{
		Server disposeServer = serverMap.get(serverName);
		if(disposeServer!=null){
			disposeServer.stop();
			disposeServer.destroy();
			serverMap.remove(serverName);
		}
		return disposeServer;
	}
	
	public Server removeServer(String serverName) throws Exception{
		return serverMap.remove(serverName);
	}
	
	public Server createServer(String uriAndServletName, Servlet servlet) throws URISyntaxException {
		return createServer(uriAndServletName, uriAndServletName, uriAndServletName, servlet);
	}
	
	public Server createServer(String serverName, String uriString, String servletName, Servlet servlet) throws URISyntaxException {
		URI uri = new URI(uriString);
		int port = uri.getPort();
		String path = uri.getPath();
		
		Server newServer = new Server();
		ServletContextHandler newSvlCtx = new ServletContextHandler(ServletContextHandler.SESSIONS);
		newSvlCtx.setContextPath("/");
		newServer.setHandler(newSvlCtx);
		ServerConnector connector = new ServerConnector(newServer);
		connector.setHost(this.localhostWsIp);
		connector.setPort(port);
		newServer.addConnector(connector);
		
		ServletHolder holderEvents = new ServletHolder(servletName, servlet);
		newSvlCtx.addServlet(holderEvents, path);
		this.serverMap.put(serverName, newServer);
		return newServer;
	}
	
	public synchronized Server getSingletonServer() {
		if(server==null){
			server = new Server();
			svlCtx = new ServletContextHandler(ServletContextHandler.SESSIONS);
			svlCtx.setContextPath("/smartiot");
			server.setHandler(svlCtx);
//			ServerConnector connector = new ServerConnector(server);
//			connector.setHost("localhost");
//			connector.setPort(8899);
//			server.addConnector(connector);
		}
		return server;
	}

	public ServerConnector addSingletonServerConnector(int port) {
		ServerConnector connector = new ServerConnector(getSingletonServer());
		connector.setHost(this.assWsIp);
		connector.setPort(port);
		getSingletonServer().addConnector(connector);
		return connector;
	}

	public ServletContextHandler getSingletonServerServletContextHandler() {
		return svlCtx;
	}

	public boolean isRunningSingletonServer() {
		return getSingletonServer().isRunning();
	}

	public void startSingletonServer() throws Exception {
		getSingletonServer().start();
		
	}

	public void stopSingletonServer() throws Exception {
		getSingletonServer().stop();
	}

	public void restartSingletonServer() throws Exception{
		getSingletonServer();
		if(server.isRunning() || server.isStarting() || server.isStarted()){
			server.stop();
		}
		server.start();
	}

	public void addSingletonServerServlet(String name, Servlet servlet, String path) {
		ServletHolder holderEvents = new ServletHolder(name, servlet);
		this.svlCtx.addServlet(holderEvents, path);
	}

	public String getSingletonServerState() {
		return getSingletonServer().getState();
	}
	
	public void dispose(){
		if(server!=null) server.destroy();
		Iterator<Server> iter = serverMap.values().iterator();
		Server temp;
		while(iter.hasNext()){
			temp = iter.next();
			temp.destroy();
		}
	}

}

package com.hdbsnc.smartiot.common.webserver;

import java.net.URISyntaxException;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

public interface IWebservicePool {

	boolean containServer(String serverName);
	Server getServer(String serverName);
	void start(String serverName) throws Exception;
	void stop(String serverName) throws Exception;
	void restart(String serverName) throws Exception;
	Server stopAndDestroyServer(String serverName) throws Exception;
	Server removeServer(String serverName) throws Exception;
	Server createServer(String uriAndServletName, Servlet servlet) throws URISyntaxException;
	Server createServer(String serverName, String uriString, String servletName, Servlet servlet) throws URISyntaxException;
	
	Server getSingletonServer();
	ServerConnector addSingletonServerConnector(int port);
	ServletContextHandler getSingletonServerServletContextHandler();
	boolean isRunningSingletonServer();
	void startSingletonServer() throws Exception;
	void stopSingletonServer() throws Exception;
	void restartSingletonServer() throws Exception;
	void addSingletonServerServlet(String name, Servlet servlet, String path);
	String getSingletonServerState();
	
}

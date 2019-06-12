package com.hdbsnc.smartiot.server;

import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.common.ICommonService;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceManager;
import com.hdbsnc.smartiot.common.am.IAdapterManager;
import com.hdbsnc.smartiot.common.em.IEventManager;
import com.hdbsnc.smartiot.common.ism.IIntegratedSessionManager;
import com.hdbsnc.smartiot.common.pm.IProfileManager;
import com.hdbsnc.smartiot.service.IService;

public interface IServerInstance {

	static final int SERVERINSTANCE_STATE_CREATE = 0;
	static final int SERVERINSTANCE_STATE_INIT = 1;
	static final int SERVERINSTANCE_STATE_START = 2;
	static final int SERVERINSTANCE_STATE_STOP = 3;
	
	String getServerInstanceName();
	
	IAdapterManager getAM();
	
	IAdapterInstanceManager getAIM();
	
	IIntegratedSessionManager getISM();
	
	IProfileManager getPM();
	
	IEventManager getEM();
	
	ICommonService getCommonService();
	
	int getServerInstanceState();
	
	void init(Map config) throws Exception;
	
	void start() throws Exception;
	
	void stop() throws Exception;
	
	List<IService> getServiceList();
}

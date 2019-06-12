package com.hdbsnc.smartiot.common.aim;

import java.util.Set;

import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.ism.sm.ISessionManager;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;

public interface IAdapterContext {

	long getCreatedTime();
	
	ISessionManager getSessionManager();
	
	IInstanceObj getAdapterInstanceInfo();
	
	Set<String> getAttributeKeyList();
	
	String getAttributeValue(String attributeName);
	
	IAdapterManifest getAdapterManifest();
	
	IAdapterInstanceContainer getAdapterInstanceContainer();
	
	IAdapterInstanceManager getAdapterInstanceManager();
}

package com.hdbsnc.smartiot.service.master.slavemanager;

import java.util.Set;

public interface ISlaveServerInstance {

	void changeState(String state);
	
	void putIpPort(String ip, String port);
	
	String getState();
	
	void putDevice(String deviceId, String sessionKey, String userId);
	
	void removeDevice(String sessionKey);
	
	Set<String> getSessionKeySet();
	
	String getConnectIp();
	
	String getConnectPort();
	
	String getServerId();
	
	String getInstanceId();
	
	
}

package com.hdbsnc.smartiot.service;

import java.util.Map;

public interface IService {

	public static final int SERVICE_STATE_REG = 0; //등록만 된 상태 
	public static final int SERVICE_STATE_INIT = 1;
	public static final int SERVICE_STATE_START = 2;
	public static final int SERVICE_STATE_STOP = 3;
	
	String getServiceName();
	
	int getServiceState();
	
	long getLastAccessedTime();
	
	void init(Map<String, String> config) throws Exception;
	void start() throws Exception;
	void stop() throws Exception;
}

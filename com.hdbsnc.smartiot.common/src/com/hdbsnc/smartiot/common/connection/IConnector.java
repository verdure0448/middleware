package com.hdbsnc.smartiot.common.connection;

import java.util.Map;

public interface IConnector extends Runnable{

	public void initialize(Map<String, String> params) throws Exception;

	public void start() throws Exception;

	public void stop() throws Exception;

	public boolean isStart();
}

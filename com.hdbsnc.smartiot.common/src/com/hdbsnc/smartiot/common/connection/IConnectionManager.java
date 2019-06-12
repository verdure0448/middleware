package com.hdbsnc.smartiot.common.connection;

public interface IConnectionManager {

	IConnection getConnection(String sid);
	
	String getSid(IConnection con);
	
	void putConnection(String sid, IConnection con);
	
	void removeConnection(String sid);
	
	void removeConnection(IConnection con);
	
	boolean containsSid(String sid);
	
	int getConnectionCount();
	
	boolean containsConnection(IConnection con);
	
	void dispose();
	
	
//	boolean containsSessionId(String sessionSid);
//	
//	IConnection getConnectionBySessionId(String sessionSid);
//	
//	void putConnectionBySessionId(String sessionId, IConnection con);
//	
//	void removeConnectionBySessionId(String sessionId);
//	
//
//	
//
//	
//	boolean containsDeviceId(String deviceId);
//	
//	IConnection getConnectionByDeviceId(String deviceId);
//	
//	void putConnectionByDeviceId(String deviceId, IConnection con);
//	
//	void removeConnectionByDeviceId(String deviceId);
}

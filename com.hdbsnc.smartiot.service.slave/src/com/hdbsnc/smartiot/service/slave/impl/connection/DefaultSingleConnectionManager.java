package com.hdbsnc.smartiot.service.slave.impl.connection;

import java.io.IOException;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;

public class DefaultSingleConnectionManager implements IConnectionManager{

	public static final String SID = "sid-";
	private String currentDeviceId;
	private String currentSessionId;
	private IConnection currentCon;
	
	public DefaultSingleConnectionManager(){
		currentDeviceId = null;
		currentSessionId = null;
		currentCon = null;
	}
	
	private boolean isSessionKey(String sid){
		return sid.startsWith(SID);
	}
	
	@Override
	public IConnection getConnection(String sid) {
		if(isSessionKey(sid)) return this.getConnectionBySessionId(sid);
		else return getConnectionByDeviceId(sid);
	}

	public void putConnection(String sid, IConnection con){
		if(isSessionKey(sid)) putConnectionBySessionId(sid, con);
		else putConnectionByDeviceId(sid, con);
	}
	
	public void removeConnection(String sid){
		if(isSessionKey(sid)) removeConnectionBySessionId(sid);
		else removeConnectionByDeviceId(sid);
	}

	@Override
	public void removeConnection(IConnection con) {
		if(this.currentCon!=null&&this.currentCon.equals(con)){
			try {
				this.currentCon.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.currentCon = null;
			this.currentDeviceId = null;
			this.currentSessionId = null;
		}
	}

	@Override
	public boolean containsSid(String sid) {
		if(isSessionKey(sid)) return this.containsSessionId(sid);
		else return this.containsDeviceId(sid);
	}

	@Override
	public int getConnectionCount() {
		if(currentCon!=null) return 1;
		else return 0;
	}

	@Override
	public boolean containsConnection(IConnection con) {
		if(this.currentCon!=null&&this.currentCon.equals(con)){
			return true;
		}
		return false;
	}

	@Override
	public void dispose() {
		if(this.currentCon!=null){
			try {
				this.currentCon.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean containsSessionId(String sessionSid) {
		if(this.currentSessionId!=null && this.currentSessionId.equals(sessionSid)){
			return true;
		}
		return false;
	}

	public IConnection getConnectionBySessionId(String sessionSid) {
		if(this.currentSessionId!=null && this.currentSessionId.equals(sessionSid)){
			return this.currentCon;
		}
		return null;
	}

	public void putConnectionBySessionId(String sessionId, IConnection con) {
		
		this.currentSessionId = sessionId;
		this.currentCon = con;
		
	}

	public void removeConnectionBySessionId(String sessionId) {
		if(currentSessionId!=null && currentSessionId.equals(sessionId)){
			try {
				this.currentCon.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.currentCon = null;
			this.currentDeviceId = null;
			this.currentSessionId = null;
		}
		
	}

	public boolean containsDeviceId(String deviceId) {
		if(this.currentDeviceId!=null && this.currentDeviceId.equals(deviceId)){
			return true;
		}
		return false;
	}


	public IConnection getConnectionByDeviceId(String deviceId) {
		if(this.currentDeviceId!=null && this.currentDeviceId.equals(deviceId)){
			return this.currentCon;
		}
		return null;
	}


	public void putConnectionByDeviceId(String deviceId, IConnection con) {
		this.currentDeviceId = deviceId;
		this.currentCon = con;
		
	}


	public void removeConnectionByDeviceId(String deviceId) {
		if(this.currentDeviceId!=null && currentDeviceId.equals(deviceId)){
			if(this.currentCon!=null){
				try {
					currentCon.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			currentCon = null;
			currentDeviceId = null;
			currentSessionId = null;
			
		}
		
	}

	@Override
	public String getSid(IConnection con) {
		if(con!=null && this.currentCon!=null){
			if(con.equals(currentCon)){
				if(currentSessionId!=null && !currentSessionId.equals("")){
					return currentSessionId;
				}else{
					return currentDeviceId;
				}
			}
		}
		return null;
	}

}

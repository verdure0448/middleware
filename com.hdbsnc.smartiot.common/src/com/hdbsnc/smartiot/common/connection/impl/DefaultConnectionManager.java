package com.hdbsnc.smartiot.common.connection.impl;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.hdbsnc.smartiot.common.connection.IConnection;
import com.hdbsnc.smartiot.common.connection.IConnectionManager;

public class DefaultConnectionManager implements IConnectionManager{

	private Map<IConnection, InnerConnection> mainMap;
	private Map<String, InnerConnection> deviceIdMap;
	private Map<String, InnerConnection> sessionIdMap;
	
	private class InnerConnection{
		private InnerConnection(){}
		IConnection con;
		String sessionID;
		String deviceID;
	}
	
	public static final String SID = "sid-";
	
	public static boolean isSessionKey(String sid){
		return sid.startsWith(SID);
	}
	
	public DefaultConnectionManager(){
		this.mainMap = new Hashtable<IConnection, InnerConnection>();
		this.deviceIdMap = new Hashtable<String, InnerConnection>();
		this.sessionIdMap = new Hashtable<String, InnerConnection>();
	}
	
	@Override
	public synchronized IConnection getConnection(String sid) {
		InnerConnection inCon;
		if(isSessionKey(sid)) inCon = sessionIdMap.get(sid);
		else inCon = deviceIdMap.get(sid);
		if(inCon!=null) return inCon.con;
		else return null;
	}

	@Override
	public synchronized void putConnection(String sid, IConnection con) {
		InnerConnection inCon = mainMap.get(con);
		if(inCon==null){
			inCon = new InnerConnection();
			inCon.con = con;
			mainMap.put(con, inCon);
			if(isSessionKey(sid)) {
				inCon.sessionID = sid;
				sessionIdMap.put(sid, inCon);
			}else {
				inCon.deviceID = sid;
				deviceIdMap.put(sid, inCon);
			}
		}else{
			inCon.con = con;
			if(isSessionKey(sid)) {
				inCon.sessionID = sid;
				sessionIdMap.put(sid, inCon);
				if(inCon.deviceID!=null) deviceIdMap.put(inCon.deviceID, inCon);
			}else{
				inCon.deviceID = sid;
				deviceIdMap.put(sid, inCon);
				if(inCon.sessionID!=null) sessionIdMap.put(inCon.sessionID, inCon);
			}
		}
	}

	@Override
	public synchronized void removeConnection(String sid) {
		if(sid!=null){
			InnerConnection inCon;
			if(isSessionKey(sid)){
				inCon = sessionIdMap.remove(sid);
				if(inCon.deviceID!=null) deviceIdMap.remove(inCon.deviceID);
				if(inCon.con!=null) {
					mainMap.remove(inCon.con);
					try {
						inCon.con.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{
				inCon = deviceIdMap.remove(sid);
				if(inCon.sessionID!=null) sessionIdMap.remove(inCon.sessionID);
				if(inCon.con!=null) {
					mainMap.remove(inCon.con);
					try {
						inCon.con.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public synchronized void removeConnection(IConnection con) {
		if(con!=null){
			InnerConnection inCon = mainMap.remove(con);
			if(inCon!=null){
				if(inCon.deviceID!=null) deviceIdMap.remove(inCon.deviceID);
				if(inCon.sessionID!=null) sessionIdMap.remove(inCon.sessionID);
				if(inCon.con!=null){
					try {
						inCon.con.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public boolean containsSid(String sid) {
		if(isSessionKey(sid)){
			return sessionIdMap.containsKey(sid);
		}else{
			return deviceIdMap.containsKey(sid);
		}
	}

	@Override
	public int getConnectionCount() {
		return mainMap.size();
	}

	@Override
	public boolean containsConnection(IConnection con) {
		return mainMap.containsKey(con);
	}

	@Override
	public synchronized void dispose() {
		for(Entry<IConnection, InnerConnection> entry: mainMap.entrySet()){
			try {
				entry.getKey().disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mainMap.clear();
		sessionIdMap.clear();
		deviceIdMap.clear();
	}

	@Override
	public String getSid(IConnection con) {
		InnerConnection inCon = this.mainMap.get(con);
		if(inCon!=null){
			if(inCon.sessionID!=null && !inCon.sessionID.equals("")){
				return inCon.sessionID;
			}else{
				return inCon.deviceID;
			}
		}
		return null;
	}
}

package com.hdbsnc.smartiot.service.master.slavemanager;

import java.util.Set;

import com.hdbsnc.smartiot.common.context.IContextTracerSupportBySeq;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance;
import com.hdbsnc.smartiot.service.master.slavemanager.Server.Instance.Device;

public interface ISlaveServer extends IContextTracerSupportBySeq{

	String getSessionKey();
	String getServerId();
	Instance addInstance(String iid, String ip, String port);
	Instance addInstance(String iid);
	void removeInstance(String iid);
	Instance getInstance(String iid);
	Device getDevice(String sid);
	boolean containsInstance(String iid);
	boolean containsDevice(String sid);
	boolean containsDeviceByDid(String did);
	Set<String> getDeviceSidSet();
	Device removeDevice(String sid);
	
	
//	String getServerId();
//
//	ISessionManager getSessionManager(String instanceId);
//	
//	Map<String, ISessionManager> getSessionManagerMap();
//	
//	ISessionManager createSessionManager(String instanceId);
//	
//	void disposeSessionManager(String instanceId);
//	
//	
//	
//	ISession getSession(String deviceId);
//	
//	boolean containsDeviceId(String deviceId);
//	
//	
//	
//	void setSessionAllocater(ISessionAllocater allocater);
//	
//	void innerNewSession(ISession session) throws Exception; 
//	
//	void outterNewSession(String deviceId, String userId, String sessionKey) throws Exception; //비정상 케이스로 세션객체 생성 및 등록이 안될 경우 발생. 
//	
//	void innerDisposeSession(String sid) throws Exception;
//	
//	void outterDisposeSession(String sid) throws Exception;
}

package com.hdbsnc.smartiot.common.ecm;

import com.hdbsnc.smartiot.common.ecm.eh.IEventHandler;
import com.hdbsnc.smartiot.common.ecm.profile.IEventContextProfile;

public interface IEventContextManager {

	void regEventHandler(IEventHandler eh) throws Exception;
	
	void unRegEventHandler(String ehid);
	
	void addEventContext(IEventContextProfile eventContextProfile) throws Exception;
	
	void removeEventContext(String eid);
	
	void start(String eid) throws Exception;
	
	void stop(String eid) throws Exception;
	
	void suspend(String eid) throws Exception;
	
	void resume(String eid) throws Exception;
	
	
}

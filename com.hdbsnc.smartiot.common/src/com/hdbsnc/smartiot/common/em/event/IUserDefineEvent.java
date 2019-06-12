package com.hdbsnc.smartiot.common.em.event;

public interface IUserDefineEvent extends ISystemEvent{

	String getDID();
	
	String getTID();
	
	String getSessionId();

}

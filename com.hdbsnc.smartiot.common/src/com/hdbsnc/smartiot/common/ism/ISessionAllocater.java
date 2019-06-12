package com.hdbsnc.smartiot.common.ism;

import com.hdbsnc.smartiot.common.ism.sm.ISession;

public interface ISessionAllocater {
	
	public void allocateOrder(ISession session, ISessionAllocaterCallback callback) throws Exception;
	
	
	public void unallocateOrder(String sid, ISessionAllocaterCallback callback) throws Exception;
	
	
}

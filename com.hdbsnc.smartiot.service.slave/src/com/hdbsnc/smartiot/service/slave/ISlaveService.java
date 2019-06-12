package com.hdbsnc.smartiot.service.slave;

import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.ism.ISessionAllocater;

public interface ISlaveService extends  ISessionAllocater, IContextProcessor {

	String getSlaveServerId();
	String getMasterServerId();
	String getSlaveServerSessionId();
}

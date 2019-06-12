package com.hdbsnc.smartiot.service.master;

import com.hdbsnc.smartiot.common.context.IContext;
import com.hdbsnc.smartiot.common.context.IContextCallback;
import com.hdbsnc.smartiot.common.context.IContextProcessor;
import com.hdbsnc.smartiot.common.ism.ISessionAllocater;
import com.hdbsnc.smartiot.service.master.slavemanager.SlaveServerManager;

public interface IMasterService extends  ISessionAllocater, IContextProcessor{

	public void handOverContext(IContext exec, IContextCallback callback) throws Exception;
	
	public SlaveServerManager getSlaveServerManager();
	
	public String getServerId();
}

package com.hdbsnc.smartiot.common.ecm.eh;

import com.hdbsnc.smartiot.common.ecm.IEventProcessor;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;

public interface IEventHandlerInstance {
	
	void initialize(IEventContext ec) throws Exception;
	
	void start(IEventContext ec) throws Exception;
	
	void suspend(IEventContext ec) throws Exception;
	
	void resume(IEventContext ec) throws Exception;
	
	void stop(IEventContext ec) throws Exception;
	
	void dispose(IEventContext ec) throws Exception;
	
	IEventProcessor getProcessor();
	
	IEventHandler getEventHandler();
	
}

package com.hdbsnc.smartiot.common.ecm.ec;

import com.hdbsnc.smartiot.common.ecm.IEventProcessor;

public interface IEventContextInstance {
	
	void initialize() throws Exception;
	
	void start() throws Exception;
	
	void suspend() throws Exception;
	
	void resume() throws Exception;
	
	void stop() throws Exception;
	
	void dispose() throws Exception;
	
	IEventProcessor getProcessor();
	
	IEventContext getEventContext();
	
	
}

package com.hdbsnc.smartiot.common.em;

import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.exception.CommonException;

public interface IAdapterProcessorEventConsumer {

	String getName();
	
	void initialize() throws Exception;
	
	void dispose();
	
	void updateEvent(IAdapterProcessorEvent event) throws CommonException;
}

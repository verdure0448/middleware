package com.hdbsnc.smartiot.common.ecm;

import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;

public interface IEventProcessor {
	
	void handle(IEventContext ec, IEvent evt) throws Exception;
	
}

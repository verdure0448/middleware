package com.hdbsnc.smartiot.common.ecm.eh;

import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;

public interface IEventHandlerFactory {

	
	IEventHandlerInstance createEventHandler(IEventContext eventCtx);
}

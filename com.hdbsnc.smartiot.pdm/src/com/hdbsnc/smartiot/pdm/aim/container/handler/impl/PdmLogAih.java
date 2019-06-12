package com.hdbsnc.smartiot.pdm.aim.container.handler.impl;

import com.hdbsnc.smartiot.common.aim.IAdapterContext;
import com.hdbsnc.smartiot.common.aim.IAdapterInstanceEvent;
import com.hdbsnc.smartiot.pdm.aim.container.handler.AbstractAih;

public class PdmLogAih extends AbstractAih{

	
	public PdmLogAih(int evtPattern, int htPattern){
		super(evtPattern, htPattern);
	}
	
	public PdmLogAih(){
		super();
	}
	
	public String getHandlerName() {
		return "PdmLogHandler";
	}

	@Override
	public void process(IAdapterContext ctx) throws Exception {
		
		IAdapterInstanceEvent event = ctx.getAdapterInstanceContainer().getLastEvent();
		System.out.println(getHandlerName()+": eventType="+event.getEventType()+" stateType="+event.getStateType());
	}

	
	
}

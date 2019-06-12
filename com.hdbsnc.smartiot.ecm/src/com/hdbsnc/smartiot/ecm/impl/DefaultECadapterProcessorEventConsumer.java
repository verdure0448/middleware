package com.hdbsnc.smartiot.ecm.impl;

import com.hdbsnc.smartiot.common.aim.IAdapterProcessorEvent;
import com.hdbsnc.smartiot.common.ecm.IEvent;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;
import com.hdbsnc.smartiot.common.em.IAdapterProcessorEventConsumer;
import com.hdbsnc.smartiot.common.exception.CommonException;
import com.hdbsnc.smartiot.util.logger.Log;

public class DefaultECadapterProcessorEventConsumer implements IAdapterProcessorEventConsumer{

	private DefaultECinstance ecic;
	private String name;
	private IEventContext ec;
	private DefaultEHinstanceContainer root;
	private Log log;
	
	DefaultECadapterProcessorEventConsumer(String name, DefaultECinstance ecic){
		this.name = name;
		this.ec = ecic.getDefaultEC();
		this.root = ecic.getRoot();
		this.log = ec.getCommonService().getLogger().logger(name);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void initialize() throws Exception {
		log.info("initialize");
		
	}

	@Override
	public void dispose() {
		log.info("dispose");
		
	}

	@Override
	public void updateEvent(IAdapterProcessorEvent event) throws CommonException {
		int evtType = event.getAdapterProcessEventType();
		int evtState = event.getAdapterProcessEventStateType();
		
		switch(evtType){
		case IAdapterProcessorEvent.TYPE_REQUEST:
			switch(evtState){
			case IAdapterProcessorEvent.STATE_INBOUND_TRANSFER:
			case IAdapterProcessorEvent.STATE_OUTBOUND_TRANSFER:
				log.debug("evtType: "+evtType+", evtState: "+evtState+", fullPath: "+event.getContext().getFullPath());
				IEvent evt = new Event(event);
				try {
					root.handle(ec, evt);
				} catch (EHprocessorException e) {
					log.err(e);
				}
				break;
			}
			break;
		}
	}

}

package com.hdbsnc.smartiot.em.impl;

import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.em.event.ISystemEvent;

public class DefaultSystemEvent extends DefaultEvent implements ISystemEvent{
	
	protected DefaultSystemEvent(){
		super();
	}
	
	protected DefaultSystemEvent(int eventID) {
		super();
		setEventID(eventID);
	}

	@Override
	public String eventCodeToMemory() {
		return String.valueOf(super.eventID());
	}

	@Override
	public String eventCodeToSystem() {
		StringBuilder sb = new StringBuilder();
		sb.append(moduleValue()).append(".");
		sb.append(eventValue()).append(".");
		sb.append(eventTypeValue()).append(".");
		sb.append(eventStateValue());
		return sb.toString();
	}

	@Override
	public String eventCodeToUser() {
		StringBuilder sb = new StringBuilder();
		sb.append(moduleName()).append("/");
		sb.append(eventName()).append("/");
		sb.append(eventTypeName()).append("/");
		sb.append(eventStateName());
		return sb.toString();
	}

	@Override
	public String moduleName() {
		return IEvent.MODLUE_NAME[super.moduleValue()];
	}

	@Override
	public String eventName() {
		return IEvent.EVENT_NAME[super.eventValue()];
	}

	@Override
	public String eventTypeName() {
		return IEvent.TYPE_NAME[super.eventTypeValue()];
	}

	@Override
	public String eventStateName() {
		return IEvent.STATE_NAME[super.eventStateValue()];
	}

}

package com.hdbsnc.smartiot.em.impl;

import com.hdbsnc.smartiot.common.em.event.IEvent;

public class DefaultEvent implements IEvent{
	protected int eventID;
	protected long eventTime;
	protected int[] eventValues;
	protected Object contents = null;
	
	protected DefaultEvent(){
		this.eventTime = System.currentTimeMillis();
	}
	
	protected void setEventID(int eventID){
		this.eventID = eventID;
		eventValues = new int[4];
		eventValues[0] = (eventID >> 12) & 0xF;
		eventValues[1] = (eventID >> 8)  & 0xF;
		eventValues[2] = (eventID >> 4)  & 0xF;
		eventValues[3] = eventID & 0xF;	
	}
	
	@Override
	public long eventTime() {
		return this.eventTime;
	}

	@Override
	public int eventID() {
		return this.eventID;
	}

	@Override
	public Object contents() {
		return this.contents;
	}

	@Override
	public boolean isContainsContents() {
		if(contents!=null) return true;
		return false;
	}

	@Override
	public int moduleValue() {
		return eventValues[0];
	}

	@Override
	public int eventValue() {
		return eventValues[1];
	}

	@Override
	public int eventTypeValue() {
		return eventValues[2];
	}

	@Override
	public int eventStateValue() {
		return eventValues[3];
	}

	@Override
	public int[] eventValues() {
		return this.eventValues;
	}

}

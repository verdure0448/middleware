package com.hdbsnc.smartiot.ecm.impl;

import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleEvent;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;

public class DefaultEClifeCycleEvent implements IEClifeCycleEvent{

	private long createdTime;
	private int eventType;
	private int eventState;
	private Exception exception;
	private IEventContext ec;
	
	DefaultEClifeCycleEvent(IEventContext ec){
		this.createdTime = System.currentTimeMillis();
		this.ec = ec;
	}
	
	DefaultEClifeCycleEvent(IEventContext ec, int eventType, int eventState, Exception e){
		this(ec);
		this.eventType = eventType;
		this.eventState = eventState;
		this.exception = e;
	}
	
	@Override
	public long createdTime() {
		return this.createdTime;
	}

	@Override
	public int getLifeCycleEventState() {
		return this.eventState;
	}

	@Override
	public int getLifeCycleEventType() {
		return this.eventType;
	}

	@Override
	public IEventContext getEventContext() {
		return this.ec;
	}

	@Override
	public Exception getException() {
		return this.exception;
	}

}

package com.hdbsnc.smartiot.pdm.aim.container.handler;

import com.hdbsnc.smartiot.common.aim.IAdapterInstanceHandler;

public abstract class AbstractAih implements IAdapterInstanceHandler{


	private int evt;
	private int ht;
	private boolean isOnce;
	
	
	public AbstractAih(int eventTypes, int handlerTypes, boolean isOnce){
		this.evt = eventTypes;
		this.ht = handlerTypes;
		this.isOnce = isOnce;
	}
	
	public AbstractAih(int eventTypes, int handlerTypes){
		this(eventTypes, handlerTypes, false);
	}
	
	public AbstractAih(boolean isOnce){
		this(ALL_EVENT, ALL_HANDLER, isOnce);
	}
	
	public AbstractAih(){
		this(false);
	}
	
	@Override
	public int getHandlerTypes(){
		return this.ht;
	}
	
	@Override
	public int getEventTypes(){
		return this.evt;
	}
	
	@Override
	public boolean isOnce() {
		return isOnce;
	}

	
//	public boolean isProcessEventType(int eventType) {
//		if(evt == 0) return true; //all 개념으로 모든 상태에서 동작가능.
//		int check = evt & eventType;
//		if(check == eventType) return true;
//		return false;
//	}
	
}
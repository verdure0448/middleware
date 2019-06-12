package com.hdbsnc.smartiot.ecm.impl;

import com.hdbsnc.smartiot.common.ecm.eh.IEventHandlerInstance;

public class EHlifeCycleException extends Exception{

	private int evtState = -1;
	private IEventHandlerInstance ehi = null;
	
	public EHlifeCycleException(int evtState, IEventHandlerInstance ehi, Exception e){
		super(e);
		this.evtState = evtState;
		this.ehi = ehi;
	}
	
	public int getEClifeCycleState(){
		return evtState;
	}
	
	public IEventHandlerInstance getEHinstance(){
		return this.ehi;
	}
}

package com.hdbsnc.smartiot.ecm.impl;

import com.hdbsnc.smartiot.common.ecm.IEvent;
import com.hdbsnc.smartiot.common.ecm.eh.IEventHandlerInstance;

public class EHprocessorException extends Exception{
	
	private IEventHandlerInstance ehi = null;
	private IEvent evt = null;
	
	public EHprocessorException(IEventHandlerInstance ehi, IEvent evt, Exception e){
		super(e);
		this.ehi = ehi;
		this.evt = evt;
	}
	
	public EHprocessorException(IEventHandlerInstance ehi, IEvent evt, String msg){
		super(msg);
		this.ehi = ehi;
		this.evt = evt;
	}
	
	
	public IEventHandlerInstance getEventHandlerInstance(){
		return this.ehi;
	}
	
	public IEvent getEvent(){
		return this.evt;
	}

}

package com.hdbsnc.smartiot.ecm.impl;

import com.hdbsnc.smartiot.common.ecm.IEvent;
import com.hdbsnc.smartiot.common.ecm.IEventProcessor;
import com.hdbsnc.smartiot.common.ecm.ec.IEClifeCycleEvent;
import com.hdbsnc.smartiot.common.ecm.ec.IEventContext;
import com.hdbsnc.smartiot.common.ecm.eh.IEventHandler;
import com.hdbsnc.smartiot.common.ecm.eh.IEventHandlerInstance;

public class DefaultEHinstanceContainer implements IEventProcessor{

	private IEventHandler eh;
	private IEventHandlerInstance ehi;
	private DefaultEHinstanceContainer next = null;
	
	
	public DefaultEHinstanceContainer(IEventHandler eh){
		this.eh = eh;
	}
	
	void initialize(IEventContext ec) throws EHlifeCycleException{
		this.ehi = eh.getEventHandlerFactory().createEventHandler(ec);
		try{
			ehi.initialize(ec);
		}catch(Exception e){
			throw new EHlifeCycleException(IEClifeCycleEvent.TYPE_INITIALIZE, ehi, e);
		}
		if(next!=null) next.initialize(ec);
	}
	
	void start(IEventContext ec) throws EHlifeCycleException{
		try{
			ehi.start(ec);
		}catch(Exception e){
			throw new EHlifeCycleException(IEClifeCycleEvent.TYPE_START, ehi, e);
		}
		if(next!=null) next.start(ec);
	}
	
	void stop(IEventContext ec) throws EHlifeCycleException{
		try{
			ehi.stop(ec);
		}catch(Exception e){
			throw new EHlifeCycleException(IEClifeCycleEvent.TYPE_STOP, ehi, e);
		}
		if(next!=null) next.stop(ec);
	}
	
	void suspend(IEventContext ec) throws EHlifeCycleException{
		try{
			ehi.suspend(ec);
		}catch(Exception e){
			throw new EHlifeCycleException(IEClifeCycleEvent.TYPE_SUSPEND, ehi, e);
		}
		if(next!=null) next.suspend(ec);
	}
	
	void resume(IEventContext ec) throws EHlifeCycleException{
		try{
			ehi.resume(ec);
		}catch(Exception e){
			throw new EHlifeCycleException(IEClifeCycleEvent.TYPE_RESUME, ehi, e);
		}
		if(next!=null) next.resume(ec);
	}
	
	void dispose(IEventContext ec) throws EHlifeCycleException{
		try{
			ehi.dispose(ec);
		}catch(Exception e){
			throw new EHlifeCycleException(IEClifeCycleEvent.TYPE_DISPOSE, ehi, e);
		}
		if(next!=null) next.dispose(ec);
	}
	
	void setNext(DefaultEHinstanceContainer ehc){
		this.next = ehc;
	}
	
	public void handle(IEventContext ec, IEvent evt) throws EHprocessorException{
		IEventProcessor ep;
		try{
			ep = ehi.getProcessor();
			if(ep==null) throw new EHprocessorException(ehi, evt, "EHprocessor is null.");
			ep.handle(ec, evt);
		}catch(Exception e){
			throw new EHprocessorException(ehi, evt, e);
		}
		if(next!=null) next.handle(ec, evt);
	}
	
}

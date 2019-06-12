package com.hdbsnc.smartiot.common.em;

import com.hdbsnc.smartiot.common.em.event.IEvent;

public abstract class AbstractEventProducer implements IEventProducer{

	private IEventManager em = null;
	private boolean isDisposed = false;

	public void pushEvent(IEvent event) throws EventProducerDisposedException{
		if(isDisposed()) throw new EventProducerDisposedException(this);
		putEvent(event);
		if(em!=null){
			em.wakeup();
		}
	}
	
	public void setEm(IEventManager em){
		this.em = em;
	}
	
	public abstract int getModuleValue();
	
	public abstract IEvent consumeFirstEvent();
	
	protected abstract void putEvent(IEvent evt);
	
	public abstract boolean isEmpty();
	
	public void dispose(){
		this.isDisposed = true;
	}
	
	public boolean isDisposed(){
		return this.isDisposed;
	}
	

}

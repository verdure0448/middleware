package com.hdbsnc.smartiot.common.em;

import com.hdbsnc.smartiot.common.em.event.IEvent;

public interface IEventProducer {
	
	int getModuleValue();
	
	/**
	 * IEventManager에서 사용하는 메소드 
	 * @return
	 */
	IEvent consumeFirstEvent();
	
//	IEvent consumeLastEvent();
//	
//	Set<IEvent> consumeAllEvent();
//	
//	void clearEvent();
	
	boolean isEmpty();
	
//	int getEventCount();
	
	/**
	 * 구현 모듈에서 호출하는 메소드
	 * AbstractEventProducer에서 미리 구현해두고 모듈 쪽에서 호출만 한다. 
	 * @param event
	 */
	void pushEvent(IEvent event) throws EventProducerDisposedException;
}

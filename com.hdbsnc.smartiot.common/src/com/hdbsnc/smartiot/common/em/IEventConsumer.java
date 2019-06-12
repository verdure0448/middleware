package com.hdbsnc.smartiot.common.em;

import com.hdbsnc.smartiot.common.em.event.IEvent;
import com.hdbsnc.smartiot.common.exception.CommonException;

public interface IEventConsumer {
	
	/**
	 * 제거시 이름으로 제거 가능.
	 * @return
	 */
	String getName();
	
	/**
	 * EventConsumer 시작시 처음 한번 호출됨. 
	 * EM에 등록되고 updateEvent 호출되기 전에 최초 한번 호출.
	 * 초기화시에 에외가 발생하면 EM에서 제거해버린다.
	 */
	void initialize() throws Exception;
	
	/**
	 * EventConsumer 종료시 마지막에 한번 호출됨.
	 * EM에서 제거될때 호출됨.
	 */
	void dispose();
	
	/**
	 * End-User가 작성해야하는 메소드 
	 * @param event
	 */
	void updateEvent(IEvent event) throws CommonException;
}

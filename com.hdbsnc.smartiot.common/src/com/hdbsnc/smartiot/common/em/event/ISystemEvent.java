package com.hdbsnc.smartiot.common.em.event;

public interface ISystemEvent extends IEvent{

	/**
	 * example: 100402407
	 * @return
	 */
	String eventCodeToMemory();
	
	/**
	 * example: "1.2.5.7"
	 * @return
	 */
	String eventCodeToSystem();
	
	/**
	 * example: "aim/lifecycle/start/completed"
	 * @return
	 */
	String eventCodeToUser();
	
	String moduleName();
	
	String eventName();
	
	String eventTypeName();
	
	String eventStateName();
}

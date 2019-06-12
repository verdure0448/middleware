package com.hdbsnc.smartiot.common.ecm.ec;

public interface IEClifeCycleEvent {

	static final int TYPE_NONE				= -1; //디폴트 값.
	static final int TYPE_INITIALIZE 		= 1;
	static final int TYPE_START 			= 1<<1; //2
	static final int TYPE_STOP				= 1<<2; //4
	static final int TYPE_SUSPEND			= 1<<3;	//8
	static final int TYPE_RESUME			= 1<<4; //16
	static final int TYPE_DISPOSE 			= 1<<5; //32
	
	static final int STATE_NONE 			= -1; //디폴트 값. 
	static final int STATE_BEGIN 			= 1;
	static final int STATE_DOING			= 1<<1;
	static final int STATE_COMPLETED		= 1<<2;
	static final int STATE_END				= 1<<3;
	static final int STATE_ERROR			= 1<<4;
	
	long createdTime();
	
	int getLifeCycleEventState();
	
	int getLifeCycleEventType();
	
	IEventContext getEventContext();
	
	Exception getException();
}

package com.hdbsnc.smartiot.common.ecm.eh;

public interface IEventHandler {

	public static final int EH_TYPE_DATA_FILTER = 		1;
	public static final int EH_TYPE_DATA_BUFFER = 		1<<1;
	public static final int EH_TYPE_DATA_STORAGE = 		1<<2;
	public static final int EH_TYPE_DATA_PARSER = 		1<<3;
	public static final int EH_TYPE_DEVICE_CONTROLL = 	1<<4;
	public static final int EH_TYPE_SERVICE_CALL = 			1<<5;
	
	String getEHID();
	
	int getType();
		
	IEventHandlerFactory getEventHandlerFactory();
	
	
}

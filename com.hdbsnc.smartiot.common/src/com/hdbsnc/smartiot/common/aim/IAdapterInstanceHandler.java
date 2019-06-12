package com.hdbsnc.smartiot.common.aim;

public interface IAdapterInstanceHandler {

	public static final int ALL_EVENT = 0b00000000;

	public static final int ALL_HANDLER 	= 0b00000000;
	public static final int BEFORE_HANDLER 	= 0b00000001;
	public static final int AFTER_HANDLER 	= 0b00000010;
	
	int getHandlerTypes();
	
	int getEventTypes();
	
	boolean isOnce();
	
	void process(IAdapterContext ctx) throws Exception;
	
}

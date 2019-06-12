package com.hdbsnc.smartiot.common.ism;

public interface ISessionAllocaterCallback {

	
	public static final int EVENT_ALLOC_RESPONSE_SUCCESS_ACK 	= 1;
	public static final int EVENT_ALLOC_RESPONSE_SUCCESS_NACK 	= 1 << 1;
	public static final int EVENT_ALLOC_RESPONSE_FAIL 			= 1 << 2;
	public static final int EVENT_UNALLOC_RESPONSE_SUCCESS_ACK 	= 1 << 3;
	public static final int EVENT_UNALLOC_RESPONSE_SUCCESS_NACK = 1 << 4;
	public static final int EVENT_UNALLOC_RESPONSE_FAIL			= 1 << 5;
	public static final int EVENT_NONE							= 1 >> 3; //처리할 것이 없을 경우 리턴 
	
	public void sessionCallbackEvent(int eventType, IConnectionInfo conInfo);
}

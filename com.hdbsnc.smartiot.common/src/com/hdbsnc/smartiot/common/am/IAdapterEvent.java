package com.hdbsnc.smartiot.common.am;


public interface IAdapterEvent {

	static final int REG_EVENT 			= 1;
	static final int REG_FAIL_EVENT 	= 1<<1;
	static final int UNREG_EVENT 		= 1<<2;
	static final int UNREG_FAIL_EVENT 	= 1<<3;
	static final int ERROR_EVENT		= 1<<4;
	
	long getCreatedTime();
	
	int getEventType();
	
	Exception getException();

	IAdapterManifest getManifest();
}

package com.hdbsnc.smartiot.common.aim;

import com.hdbsnc.smartiot.common.am.IAdapterManifest;
import com.hdbsnc.smartiot.common.pm.vo.IInstanceObj;


public interface IAdapterInstanceEvent {
	
	//EventType
	static final int CREATE_EVENT		= 1;
	static final int INITIALIZE_EVENT	= 1 << 1;//2
	static final int START_EVENT 		= 1 << 2;//4
	static final int SUSPEND_EVENT		= 1 << 3;//8
	static final int STOP_EVENT	 		= 1 << 4;//16
	static final int DISPOSE_EVENT		= 1 << 5;
	
	//StateType
	static final int CREATED_STATE		= 1;
	static final int BEGIN_STATE		= 1 << 1;//2
	static final int DOING_STATE		= 1 << 2;//4
	static final int COMPLETED_STATE	= 1 << 3;//8
	static final int END_STATE			= 1 << 4;//16
	static final int ERROR_STATE		= 1 << 5;//32
//	static final int FAILED_STATE 		= 1 << 5;
//	static final int EXECUTE_STATE		= 1 << 6;
//	static final int RESULT_STATE		= 1 << 7;
//	static final int PUSH_STATE			= 1 << 8;

	
	long getCreatedTime();
	
	int getEventType();
	
	int getStateType();
	
	Exception getException();

	IAdapterManifest getManifest();
	
	IInstanceObj getInstanceInfo();
}

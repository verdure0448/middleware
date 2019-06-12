package com.hdbsnc.smartiot.common.aim;

import com.hdbsnc.smartiot.common.context.IContext;

public interface IAdapterProcessorEvent {

	static final int TYPE_NONE=-1;
	static final int TYPE_INIT=0;
	static final int TYPE_REQUEST 	= 1;
	static final int TYPE_RESPONSE 	= 1<<1;
	static final int TYPE_EVENT 	= 1<<2;


	static final int STATE_TYPE_NONE=-1;
	static final int STATE_TYPE_INIT=0;
	static final int STATE_BEGIN 	= 1;
	static final int STATE_SUCCESS 	= 1<<1;
	static final int STATE_FAIL		= 1<<2;
	static final int STATE_ERROR	= 1<<3;
	static final int STATE_INBOUND_TRANSFER = 1<<4;
	static final int STATE_OUTBOUND_TRANSFER = 1<<5;
	
//	//전송방향을 알 수 있음.
//	static final int EVENT_DEVICE_REQUEST 	= 0b0000000000000001;
//	static final int EVENT_DEVICE_RESPONSE 	= 0b0000000000000010;
//	static final int EVENT_DEVICE_PUSH		= 0b0000000000000100;
//	static final int EVENT_ADAPTER_REQUEST 	= 0b0000000000001000;
//	static final int EVENT_ADAPTER_RESPONSE = 0b0000000000010000;
//	static final int EVENT_ADAPTER_PUSH		= 0b0000000000100000;
//	
//	//데이터의 상태를 알 수 있음.
//	static final int STATE_ERROR			= 0b0000000000000001; // error
//	static final int STATE_WARNNING			= 0b0000000000000010; // warnning
//	static final int STATE_DEBUG			= 0b0000000000000100; // programming debug
//	static final int STATE_INFO				= 0b0000000000001000; // notify, state, report, etc
//	static final int STATE_DATA				= 0b0000000000010000; // property, data, config, setting, etc
//	static final int STATE_NOTHING			= 0b0000000000100000; // nothing
//	static final int STATE_UNKNOWN			= 0b0000000001000000; // unknown
	
	// EVENT_CLIENT_PUSH + STATE_DATA : RFID리더의 경우 TAG를 인식해서 정보를 보내왔을 경우.
	
	// EVENT_SERVER_REQUEST + STATE_DATA : 장치에게 뭔가를 요청할 경우.
	// EVENT_CLIENT_RESPONSE + STATE_NOTHING : ack만 왔을 경우.
	
	long getCreatedTime();
	
	int getAdapterProcessEventType();
	int getAdapterProcessEventStateType();
	
	Exception getException();
	IContext getContext();
	
	String getIID();
	
	
}

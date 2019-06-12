package com.hdbsnc.smartiot.common.em.event;

public interface IEvent {
	public static final String EVENT_FILTER_ALL = "0.0.0.0";
	public static final int ALL = 0;
	
	public static final int MODULE_PDM 					= 1 << 12;
	public static final int MODULE_ISM 					= 2 << 12;
	public static final int MODULE_MSS 					= 3 << 12;
	public static final int MODULE_SSS 					= 4 << 12;
	public static final int MODULE_ASS 					= 5 << 12;
	public static final int MODULE_PM  					= 6 << 12;
	public static final int MODULE_EM					= 7 << 12;
	public static final int MODULE_ECM 					= 8 << 12;
	
	public static final int EVENT_MODULE_LIFECYCLE 		= 1 << 8;
	public static final int EVENT_ADAPTER_LIFECYCLE 	= 2 << 8;
	public static final int EVENT_INSTANCE_LIFECYCLE 	= 3 << 8;
	public static final int EVENT_INSTANCE_PROCESSOR	= 4 << 8;
	public static final int EVENT_SM_LIFECYCLE			= 5 << 8;
	public static final int EVENT_SESSION_LIFECYCLE		= 6 << 8;
	public static final int EVENT_SERVER_LIFECYCLE		= 7 << 8;
	public static final int EVENT_DEVICE_LIFECYCLE		= 8 << 8;
	public static final int EVENT_CONNECTION_LIFECYCLE	= 9 << 8;
	public static final int EVENT_AUTH					= 10 << 8;
	public static final int EVENT_EVENTCONSUMER			= 11 << 8;
	
	public static final int TYPE_INIT					= 1 << 4;
	public static final int TYPE_OPEN					= 2 << 4;
	public static final int TYPE_CLOSE					= 3 << 4;
	public static final int TYPE_INSTALL				= 4 << 4;
	public static final int TYPE_UNINSTALL				= 5 << 4;
	public static final int TYPE_CREATE					= 6 << 4;
	public static final int TYPE_INITIALIZE				= 7 << 4;
	public static final int TYPE_START					= 8 << 4;
	public static final int TYPE_STOP					= 9 << 4;
	public static final int TYPE_SUSPEND				= 10 << 4;
	public static final int TYPE_DISPOSE				= 11 << 4;
	public static final int TYPE_REQUEST				= 12 << 4;
	public static final int TYPE_RESPONSE				= 13 << 4;
	public static final int TYPE_EVENT					= 14 << 4;
	public static final int TYPE_ACTIVATE				= 15 << 4;
	public static final int TYPE_UNACTIVATE				= 16 << 4;
	public static final int TYPE_TIMEOUT				= 17 << 4;
	public static final int TYPE_CONNECT				= 18 << 4;
	public static final int TYPE_DISCONNECT				= 19 << 4;
	public static final int TYPE_TRYCONNECT				= 20 << 4;
	public static final int TYPE_SUCCESS				= 21 << 4;
	public static final int TYPE_FAIL					= 22 << 4;
	public static final int TYPE_UPDATEEVENT			= 23 << 4;
	public static final int TYPE_RESUME					= 24 << 4; // ECM 때문에 추가됨. PDM에서도 향후 버젼에는 추가가 필요함.
	
	public static final int STATE_BEGIN					= 1;
	public static final int STATE_SUCCESS				= 2;
	public static final int STATE_FAIL					= 3;
	public static final int STATE_ERROR					= 4;
	public static final int STATE_CREATED				= 5;
	public static final int STATE_DOING					= 6;
	public static final int STATE_COMPLETED				= 7;
	public static final int STATE_END					= 8;
	public static final int STATE_TRANSFER				= 9;
	
	public static final String[] MODLUE_NAME = new String[] { "ALL", "PDM", "ISM", "MSS", "SSS", "ASS", "PM", "EM", "ECM" };
	public static final String[] EVENT_NAME = new String[] { "ALL","MODULE_LIFECYCLE","ADAPTER_LIFECYCLE","INSTANCE_LIFECYCLE","INSTANCE_PROCESSOR","SM_LIFECYCLE",
			"SESSION_LIFECYCLE","SERVER_LIFECYCLE","DEVICE_LIFECYCLE","CONNECTION_LIFECYCLE","AUTH",
			"EVENTCONSUMER"};
	public static final String[] TYPE_NAME = new String[] { "ALL", "INIT", "OPEN", "CLOSE", "INSTALL", "UNINSTALL",
			"CREATE","INITIALIZE","START","STOP","SUSPEND",
			"DISPOSE","REQUEST","RESPONSE","EVENT","ACTIVATE",
			"UNACTIVATE","TIMEOUT","CONNECT","DISCONNECT","TRYCONNECT",
			"SUCCESS", "FAIL", "UPDATEEVENT", "RESUME"};
	public static final String[] STATE_NAME = new String[] { "ALL", "BEGIN","SUCCESS","FAIL","ERROR","CREATED",
			"DOING","COMPLETED","END", "TRANSFER"};
	
	/**
	 * 이벤트 발생 시간 
	 * @return
	 */
	long eventTime();
	
	/**
	 * 이벤트 아이디 
	 * @return
	 */
	int eventID();
	
	/**
	 * 이벤트 컨텐츠: 로그, 에러메시지, 데이터 등 
	 * @return
	 */
	Object contents();
	
	boolean isContainsContents();
	
	int moduleValue();
	
	int eventValue();
	
	int eventTypeValue();
	
	int eventStateValue();
	
	int[] eventValues();
}

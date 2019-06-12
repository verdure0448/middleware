package com.hdbsnc.smartiot.adapter.websocketapi.constant;

public interface WebSocketAdapterConst {
	
	/**라스트 이벤트**/
	static final String	ADAPTER_PROCESS_EVNET="process.event";
	static final String ADAPTER_PROCESS_STATE_EVENT="process.state.event";
	static final String LAST_TIME_EVENT="last.time.event";
	
	/** 주요 항목 */
	static final String AID = "adapter.id";
	static final String IID = "instance.id";
	static final String DID = "device.id";
	static final String UID = "user.id";
	static final String SID = "session.id";
	static final String TID = "target.id";
	static final String DPID = "device.pool.id";
	static final String UPID = "user.pool.id";
	
	static final String FULL_PATH = "full.path";
	static final String CONTENT = "content";
	static final String TRAN = "transmission";
	static final String CMD_KEY = "command.key";
	static final String CMD_VALUE = "command.value";
	
	/** 아답터 */
	static final String ADT_NAME = "adapter.name";
	static final String ADT_KIND = "adapter.kind";
	static final String ADT_TYPE = "adapter.type";
	static final String IS_ALLOCATE_IP = "is.allocate.ip";
	static final String DEFAULT_IP = "default.ip";
	static final String DEFAULT_PORT = "default.port";
	
	static final String ADT_ATTRIBUTION = "adapter.attribution";
	static final String ADT_FUNCTION = "adapter.function";
	
	static final String ADT_FILE_NAME = "adapter.file.name";
	static final String ADT_FILE_SIZE = "adapter.file.size";
	static final String ADT_TOTAL_SEQUENCE = "total.sequence";
	static final String ADT_CURRENT_SEQUENCE = "current.sequence";
	
	static final String ADT_DESCRIPTION = "adapter.description";
	static final String ADT_HYPERLINK = "adapter.hyperlink";
	
	/** 인스턴스 */
	static final String INS_NAME = "instance.name";
	static final String INS_KIND = "instance.kind";
	static final String DEFAULT_DEV_ID = "default.device.id";
	static final String SESSION_TIMEOUT = "session.timeout";
	static final String INIT_DEV_STATUS = "init.device.status";
	static final String SELF_ID = "self.id";
	static final String SELF_PW = "self.pw";
	
	static final String ATTRIBUTE_LIST = "attribute.list";
	static final String FUNCTION_LIST = "function.list";
	
	/** 속성 */
	static final String ATT_KEY = "attribution.key";
	static final String ATT_DESCRIPTION = "attribution.description";
	static final String ATT_VALUE_TYPE = "attribution.value.type";
	static final String ATT_VALUE = "attribution.value";
	
	/** 기능 */
	static final String FUNC_KEY = "function.key";
	static final String FUNC_DESCRIPTION = "function.description";
	static final String CONTENT_TYPE = "content.type";
	static final String PARAM_1 = "param1";
	static final String PARAM_2 = "param2";
	static final String PARAM_3 = "param3";
	static final String PARAM_4 = "param4";
	static final String PARAM_5 = "param5";
	static final String PARAM_TYPE1 = "param.type1";
	static final String PARAM_TYPE2 = "param.type2";
	static final String PARAM_TYPE3 = "param.type3";
	static final String PARAM_TYPE4 = "param.type4";
	static final String PARAM_TYPE5 = "param.type5";

	static final String PARAM = "param";
	static final String PARAM_TYPE = "param.type";
	
	
	/** 장치풀 */
	static final String DEV_POOL_NAME = "device.pool.name";
	
	/** 장치 */
	static final String DEV_NAME = "device.name";
	static final String DEV_TYPE = "device.type";
	
	/** 유저풀 */
	static final String USER_POOL_NAME = "user.pool.name";
	
	/** 유저 */
	static final String USER_PASSWORD = "user.password";
	static final String USER_TYPE = "user.type";
	static final String USER_NAME = "user.name";
	static final String COMPANY_NAME = "company.name";
	static final String DEPARTMENT_NAME = "department.name";
	static final String JOB_TITLE = "job.title";
	
	/** 유저 필터 */
	static final String AUTHORITY_FILTER = "authority.filter";
	
	/** 도메인 */
	static final String DOMAIN_ID = "domain.id";
	static final String DOMAIN_NAME = "domain.name";
	static final String DOMAIN_TYPE = "domain.type";
	
	/** 인스턴스 상태 */
	static final String INS_EVENT = "instance.event";
	static final String INS_STATUS = "instance.status";
	
	/** 세션 상태 */
	static final String SESSION_STATUS = "session.status";	
	
	/** 공통 */
	static final String INS_TYPE = "instance.type";
	static final String IS_USE = "is.use";
	static final String REMARK = "remark";
	static final String IP = "ip";
	static final String PORT = "port";
	static final String URL = "url";
	static final String LAT = "latitude";
	static final String LON = "longitude";
	
	static final String ALTER_DATE = "alter.date";
	static final String REG_DATE = "registration.date";
	
	/** 이벤트 */
	static final String EVENT_ID = "event.id";
	static final String EVENT_TIME = "event.time";
	static final String EVENT_STATUS = "event.status";	
	
	/** 기본 관리자 */
	static final String DEFAULT_ADMIN = "smartiot.admin";
	

	/** 2016/04/01 ADD 명화공업용 START */
	static final String ADT_IMAGE = "adapter.image";
	static final String DEVICE_TYPE = "device.type";
	static final String DEVICE_ADDRESS = "device.address";
	static final String DEVICE_SCORE = "device.score";
	static final String GATHERING_PERIOD = "gathering.period";
	static final String EXIT_CODE = "exit.code";
	/** 2016/04/01 ADD 명화공업용 END */
}

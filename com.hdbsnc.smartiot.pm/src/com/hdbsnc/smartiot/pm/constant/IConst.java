package com.hdbsnc.smartiot.pm.constant;

public interface IConst {

	public static final String EMPTY_STRING = "";

	/**
	 * 인스턴스 속성
	 * 
	 * @author KANG
	 *
	 */
	interface InstanceAttribute {
		public static final String C_INS_ID = "insId";
		public static final String C_KEY = "key";
		public static final String C_DSCT = "dsct";
		public static final String C_VALUE = "value";
		public static final String C_VALUE_TYPE = "valueType";
		public static final String C_INIT = "init";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}

	/**
	 * 인스턴스 기능
	 */
	interface InstanceFunction {
		public static final String C_INS_ID = "insId";
		public static final String C_KEY = "key";
		public static final String C_DSCT = "dsct";
		public static final String C_CONT_TYPE = "contType";
		public static final String C_PARAM1 = "param1";
		public static final String C_PARAM2 = "param2";
		public static final String C_PARAM3 = "param3";
		public static final String C_PARAM4 = "param4";
		public static final String C_PARAM5 = "param5";
		public static final String C_PARAM_TYPE1 = "paramType1";
		public static final String C_PARAM_TYPE2 = "paramType2";
		public static final String C_PARAM_TYPE3 = "paramType3";
		public static final String C_PARAM_TYPE4 = "paramType4";
		public static final String C_PARAM_TYPE5 = "paramType5";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}
	
	/**
	 * 인스턴스
	 * 
	 * @author KANG
	 *
	 */
	interface AdapterInstance {
		public static final String C_INS_ID = "insId";
		public static final String C_DEV_POOL_ID = "devPoolId";
		public static final String C_ADT_ID = "adtId";
		public static final String C_DEFAULT_DID = "defaultDevId";
		public static final String C_INS_NAME = "insNm";
		public static final String C_INS_KIND = "insKind";
		public static final String C_INS_TYPE = "insType";
		public static final String C_IS_USE = "isUse";
		public static final String C_SESSION_TIMEOUT = "sessionTimeout";
		public static final String C_INIT_DEV_STATUS = "initDevStatus";
		public static final String C_IP = "ip";
		public static final String C_PORT = "port";
		public static final String C_URL = "url";
		public static final String C_LAT = "lat";
		public static final String C_LON = "lon";
		public static final String C_SELF_ID = "selfId";
		public static final String C_SELF_PW = "selfPw";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}

	/**
	 * 장치
	 * 
	 * @author KANG
	 *
	 */
	interface Device {
		public static final String C_DEVICE_ID = "devId";
		public static final String C_DEVICE_POOL_ID = "devPoolId";
		public static final String C_DEVICE_NAME = "devNm";
		public static final String C_DEVICE_TYPE = "devType";
		public static final String C_IS_USE = "isUse";
		public static final String C_SESSION_TIMEOUT = "sessionTimeout";
		public static final String C_IP = "ip";
		public static final String C_PORT = "port";
		public static final String C_LAT = "lat";
		public static final String C_LON = "lon";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}

	/**
	 * 장치풀
	 * 
	 * @author KANG
	 *
	 */
	interface DevicePool {
		public static final String C_DEVICE_POOL_ID = "devPoolId";
		public static final String C_DEVICE_POOL_NAME = "devPoolNm";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}

	/**
	 * 도메인
	 * 
	 * @author KANG
	 *
	 */
	interface DomainIdMast {
		public static final String C_DOMAIN_ID = "domainId";
		public static final String C_DOMAIN_NAME = "domainNm";
		public static final String C_DOMAIN_TYPE = "domainType";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}

	/**
	 * 유저마스터
	 * 
	 * @author KANG
	 *
	 */
	interface UserPool {
		public static final String C_USER_POOL_ID = "userPoolId";
		public static final String C_USER_POOL_NAME = "userPoolNm";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}

	/**
	 * 유저
	 * 
	 * @author KANG
	 *
	 */
	interface User {
		public static final String C_USER_ID = "userId";
		public static final String C_USER_POOL_ID = "userPoolId";
		public static final String C_USER_TYPE = "userType";
		public static final String C_USER_NAME = "userNm";
		public static final String C_USER_PW = "userPw";
		public static final String C_COMP_NAME = "compNm";
		public static final String C_DEPT_NAME = "deptNm";
		public static final String C_TITLE_NAME = "titleNm";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}

	/**
	 * 유저필터
	 * 
	 * @author KANG
	 *
	 */
	interface UserFilter {
		public static final String C_USER_ID = "userId";
		public static final String C_AUTH_FILTER = "authFilter";
		public static final String C_REMARK = "remark";
		public static final String C_ALTER_DATE = "alterDate";
		public static final String C_REG_DATE = "regDate";
	}
	
	interface MsgMast {
		public static final String C_INNER_CODE = "InnerCode";
		public static final String C_OUTER_CODE = "OuterCode";
		public static final String C_TYPE = "type";
		public static final String C_MSG = "Msg";
		public static final String C_CAUSE_CONTEXT = "CauseContext";
		public static final String C_SOLUTION_CONTEXT = "SolutionContext";
	}
}

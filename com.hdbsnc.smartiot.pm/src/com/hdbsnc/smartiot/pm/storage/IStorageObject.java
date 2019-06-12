package com.hdbsnc.smartiot.pm.storage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IStorageObject {

	// 아답타 인스턴스 DATA
	public static final String C_ADT_INS_DATA = "AdtInsData";
	// 인스턴스 속성 DATA
	public static final String C_INS_ATT_DATA = "InsAttData";
	// 인스턴스 속성 DATA
	public static final String C_INS_FUNC_DATA = "InsFuncData";
	// 유저프로파일 마스터 DATA
	public static final String C_USER_POOL_DATA = "USerPoolData";
	// 유저프로파일 DATA
	public static final String C_USER_DATA = "USerData";
	// 유저프로파일 필터 DATA
	public static final String C_USER_FILTER_DATA = "USerFilterData";
	// 도메인 식별자 마스터 DATA
	public static final String C_DOMAIN_ID_MAST_DATA = "DomainIdMastData";
	// 장치 DATA
	public static final String C_DEVICE_DATA = "DeviveData";
	// 장치 DATA
	public static final String C_DEVICE_POOL_DATA = "DevivePoolData";
	// 메세지 마스터 DATA
	public static final String C_MSG_MAST_DATA = "MsgMastData";

	public List<Map<String, String>> getStorgeEntity(String type,
			List<KeyValueEntity<String, String>> conditionEntityList, Map<String, String> config) throws Exception;

	public List<Map<String, String>> setStorgeEntity(String type,
			List<Map<String, String>> dataList, Map<String, String> config) throws Exception;

	public List<Map<String, String>> putStorgeEntity(String type,
			List<Map<String, String>> dataList, Map<String, String> config) throws Exception;

	public List<Map<String, String>> removeStorgeEntity(String type,
			List<KeyValueEntity<String, String>> conditionEntityList, Map<String, String> config) throws Exception;
}

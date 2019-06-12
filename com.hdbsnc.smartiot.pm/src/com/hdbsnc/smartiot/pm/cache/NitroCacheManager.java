////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.cache;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import com.hdbsnc.smartiot.pm.constant.IConst;
import com.hdbsnc.smartiot.pm.storage.IStorageObject;
import com.hdbsnc.smartiot.pm.storage.KeyValueEntity;
import com.hdbsnc.smartiot.pm.storage.Storage;
import com.hdbsnc.smartiot.pm.util.TimeUtil;
import com.hdbsnc.smartiot.pm.vo.impl.CacheManagerObj;
import com.hdbsnc.smartiot.pm.vo.impl.DeviceObj;
import com.hdbsnc.smartiot.pm.vo.impl.DevicePoolObj;
import com.hdbsnc.smartiot.pm.vo.impl.DomainIdMastObj;
import com.hdbsnc.smartiot.pm.vo.impl.InstanceAttributeObj;
import com.hdbsnc.smartiot.pm.vo.impl.InstanceFunctionObj;
import com.hdbsnc.smartiot.pm.vo.impl.InstanceObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyDeviceObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyDevicePoolObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyDomainIdMastObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyInstanceAttributeObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyInstanceFunctionObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyInstanceObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyMsgMastObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyUserFilterObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyUserPoolObj;
import com.hdbsnc.smartiot.pm.vo.impl.MsgMastObj;
import com.hdbsnc.smartiot.pm.vo.impl.ModifyUserObj;
import com.hdbsnc.smartiot.pm.vo.impl.UserFilterObj;
import com.hdbsnc.smartiot.pm.vo.impl.UserPoolObj;
import com.hdbsnc.smartiot.pm.vo.impl.UserObj;
import com.hv.nitroCache.CacheEviction;
import com.hv.nitroCache.NitroCache;

/**
 * 
 * 니트로 캐쉬 매니저
 * 
 * @author KANG
 *
 */
public class NitroCacheManager {

	/** 프로퍼티 */
	private static Properties _Prop = null;
	/** 아답터 인스턴스 Cache */
	private static NitroCache<String, Object> _InsCache = null;
	/** 아답터 인스턴스 속성 Cache */
	private static NitroCache<String, Object> _InsAttCache = null;
	/** 아답터 인스턴스 기능 Cache */
	private static NitroCache<String, Object> _InsFuncCache = null;
	/** 유저풀 Cache */
	private static NitroCache<String, Object> _UserPoolCache = null;
	/** 유저 Cache */
	private static NitroCache<String, Object> _UserCache = null;
	/** 유저 필터 Cache */
	private static NitroCache<String, Object> _UserFilterCache = null;
	/** 도메인 식별자 마스터 Cache */
	private static NitroCache<String, Object> _DomainIdMastCache = null;
	/** 장치풀 Cache */
	private static NitroCache<String, Object> _DevicePoolCache = null;
	/** 장치 Cache */
	private static NitroCache<String, Object> _DeviceCache = null;

	/** 메세지 마스터 Cache */
	private static NitroCache<String, Object> _MsgMastCache = null;

	/** 환경설정 파일 */
	private static Map<String, String> _config = null;
	
	/**
	 * 초기화 함수
	 */
	public static void init(URL url, Map<String, String> config) {

		if (_Prop == null) {
			_Prop = new Properties();

			try {

				_Prop.loadFromXML(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(_config == null){
			_config = config;
		}

		if (_InsCache == null) {
			_InsCache = NitroCache.getInstance(Integer.parseInt(getEnv("AdapterInstanceCacheSize")),
					CacheEviction.FIFO);
		}

		if (_InsAttCache == null) {
			_InsAttCache = NitroCache.getInstance(Integer.parseInt(getEnv("InstanceAttributeCacheSize")),
					CacheEviction.FIFO);
		}

		if (_InsFuncCache == null) {
			_InsFuncCache = NitroCache.getInstance(Integer.parseInt(getEnv("InstanceFunctionCacheSize")),
					CacheEviction.FIFO);
		}

		if (_UserPoolCache == null) {
			_UserPoolCache = NitroCache.getInstance(Integer.parseInt(getEnv("UserPoolCacheSize")), CacheEviction.FIFO);
		}

		if (_UserCache == null) {
			_UserCache = NitroCache.getInstance(Integer.parseInt(getEnv("UserCacheSize")), CacheEviction.FIFO);
		}

		if (_UserFilterCache == null) {
			_UserFilterCache = NitroCache.getInstance(Integer.parseInt(getEnv("UserFilterCacheSize")),
					CacheEviction.FIFO);
		}

		if (_DomainIdMastCache == null) {
			_DomainIdMastCache = NitroCache.getInstance(Integer.parseInt(getEnv("DomainIdMastCacheSize")),
					CacheEviction.FIFO);
		}

		if (_DevicePoolCache == null) {
			_DevicePoolCache = NitroCache.getInstance(Integer.parseInt(getEnv("DevicePoolCacheSize")),
					CacheEviction.FIFO);
		}

		if (_DeviceCache == null) {
			_DeviceCache = NitroCache.getInstance(Integer.parseInt(getEnv("DeviceCacheSize")), CacheEviction.FIFO);
		}

		if (_MsgMastCache == null) {
			_MsgMastCache = NitroCache.getInstance(Integer.parseInt(getEnv("MsgMastCacheSize")), CacheEviction.FIFO);
		}
	}

	/**
	 * 프로퍼티 조회
	 * 
	 * @param key 키
	 * @return 결과
	 */
	public static String getEnv(String key) {
		return _Prop.getProperty(key).trim();
	}

	/**
	 * 스토리지 인스턴스 조회
	 * 
	 * @return 스토리지 인스턴스
	 */
	private static IStorageObject _storageInstance = null;

	private static IStorageObject getStorageInstance() {
		if (_storageInstance == null) {
			_storageInstance = Storage.getInstance(getEnv("StorageClass"));
		}
		return _storageInstance;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 아답터 인스턴스
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 아답터 인스턴스 조회
	 * 
	 * @param insId
	 *            인스턴스ID
	 * @return IAdapterInstance VO객체
	 * @throws Exception
	 */
	public static InstanceObj getInstance(String insId) throws Exception {

		InstanceObj resultVo = (InstanceObj) _InsCache.get(insId);

		// 캐쉬에 데이터가 존재하지 않으면
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> conditionEntity = new KeyValueEntity<String, String>(
					IConst.AdapterInstance.C_INS_ID, insId);

			conditionEntityList.add(conditionEntity);

			// 스토리지에서 조회

			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_ADT_INS_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyInstanceObj.createVoFromMap(resultList.get(0));
			}

			// 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_InsCache.put(insId, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	/**
	 * 장치풀ID로 인스턴스 조회
	 * 
	 * @param devPoolId
	 * @return 인스턴스 VO
	 * @throws Exception
	 */
	public static InstanceObj getInstanceByDevPoolId(String devPoolId) throws Exception {
		InstanceObj resultVo = null;

		InstanceObj tempVo = null;
		for (String key : _InsCache.keySet()) {
			tempVo = (InstanceObj) _InsCache.get(key);
			if (tempVo == null)
				continue;
			if (devPoolId.equals(tempVo.getDevPoolId())) {
				resultVo = tempVo;
				break;
			}
		}

		// 캐쉬에 데이터가 존재하지 않으면
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> conditionEntity = new KeyValueEntity<String, String>(
					IConst.AdapterInstance.C_DEV_POOL_ID, devPoolId);

			conditionEntityList.add(conditionEntity);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_ADT_INS_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyInstanceObj.createVoFromMap(resultList.get(0));
			}

			// 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_InsCache.put(resultVo.getInsId(), resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	/**
	 * 아답터ID로 인스턴스 조회
	 * 
	 * @param adtId
	 * @return
	 * @throws Exception
	 */
	public static List<InstanceObj> getInstanceByAdtId(String adtId) throws Exception {
		List<InstanceObj> resultVoList = null;

		// 캐시 검색은 없음(key항목과 무관하고 복수건 검색이기 때문)

		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> conditionEntity = new KeyValueEntity<String, String>(
				IConst.AdapterInstance.C_ADT_ID, adtId);
		conditionEntityList.add(conditionEntity);

		// 스토리지에서 조회
		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_ADT_INS_DATA,
				conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVoList = new ArrayList<>();
			InstanceObj vo = null;
			for (Map<String, String> map : resultList) {
				vo = ModifyInstanceObj.createVoFromMap(map);

				// 캐쉬접속 정보 Update
				vo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_InsCache.put(vo.getInsId(), vo);

				resultVoList.add(vo);
			}

		}

		return resultVoList;
	}

	/**
	 * 디폴트 장치ID로 인스턴스 조회
	 * 
	 * @param defaultDevId
	 * @return
	 * @throws Exception
	 */
	public static InstanceObj getInstanceByDefaultDevId(String defaultDevId) throws Exception {
		// 캐시 검색은 없음(key검색이 아님)
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> conditionEntity = new KeyValueEntity<String, String>(
				IConst.AdapterInstance.C_DEFAULT_DID, defaultDevId);
		conditionEntityList.add(conditionEntity);

		// 스토리지에서 조회
		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_ADT_INS_DATA,
				conditionEntityList, _config);

		InstanceObj resultVo = null;

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceObj.createVoFromMap(resultList.get(0));
		}

		// 해당 데이터가 존재하면
		if (resultVo != null) {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			// 캐쉬등록
			_InsCache.put(resultVo.getInsId(), resultVo);
		}

		return resultVo;
	}

	/**
	 * 아답터 인스턴스 변경
	 * 
	 * @param insId
	 *            인스턴스ID
	 * @param vo
	 *            IAdapterInstance VO객체
	 * @return 결과
	 * @throws Exception
	 */
	public static InstanceObj setInstance(InstanceObj vo) throws Exception {
		InstanceObj resultVo = null;

		// 데이터 리스트 작성
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		Map<String, String> dataMap = ModifyInstanceObj.createMapFromVo(vo);
		dataList.add(dataMap);

		// 스토리지에 해당 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_ADT_INS_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_InsCache.put(vo.getInsId(), resultVo);
		}

		return resultVo;
	}

	/**
	 * 아답터 인스턴스 등록
	 * 
	 * @param vos
	 *            등록 할 IAdapterInstance VO 리스트
	 * @return 처리결과
	 * @throws Exception
	 */
	public static InstanceObj putInstance(InstanceObj vo) throws Exception {

		InstanceObj resultVo = null;
		Map<String, String> inMap = ModifyInstanceObj.createMapFromVo(vo);
		List<Map<String, String>> inMapList = new ArrayList<Map<String, String>>();
		inMapList.add(inMap);

		// 스토리지에 해당 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_ADT_INS_DATA,
				inMapList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_InsCache.put(vo.getInsId(), resultVo);
		}
		return resultVo;
	}

	/**
	 * 아답터 인스턴스 등록
	 * 
	 * @param insId
	 *            삭제 할 인스턴스ID
	 * @return 처리결과
	 * @throws Exception
	 */
	public static InstanceObj removeInstance(String insId) throws Exception {
		InstanceObj resultVo = null;

		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> conditionEntity = new KeyValueEntity<String, String>(
				IConst.AdapterInstance.C_INS_ID, insId);

		conditionEntityList.add(conditionEntity);

		List<Map<String, String>> resultList = getStorageInstance().removeStorgeEntity(IStorageObject.C_ADT_INS_DATA,
				conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceObj.createVoFromMap(resultList.get(0));
		}

		// 캐시 삭제
		_InsCache.remove(insId);

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 아답터 인스턴스 속성
	// ////////////////////////////////////////////////////////////////////////
	public static InstanceAttributeObj getInstanceAttribute(String insId, String attKey) throws Exception {

		InstanceAttributeObj resultVo = (InstanceAttributeObj) _InsAttCache.get(insId + ":" + attKey);

		// 캐쉬에 정보가 없을 경우
		if (resultVo == null) {
			// 스토리지에서 아답타 속성 정보를 조회
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition1 = new KeyValueEntity<String, String>(
					IConst.InstanceAttribute.C_INS_ID, insId);
			KeyValueEntity<String, String> condition2 = new KeyValueEntity<String, String>(
					IConst.InstanceAttribute.C_KEY, attKey);
			conditionEntityList.add(condition1);
			conditionEntityList.add(condition2);

			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_INS_ATT_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyInstanceAttributeObj.createVoFromMap(resultList.get(0));
			}

			// 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_InsAttCache.put(insId + ":" + attKey, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	public static List<InstanceAttributeObj> getInstanceAttributeList(String insId) throws Exception {

		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.InstanceAttribute.C_INS_ID,
				insId);

		conditionEntityList.add(condition);

		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_INS_ATT_DATA,
				conditionEntityList, _config);

		InstanceAttributeObj resultVo = null;
		List<InstanceAttributeObj> resultVoList = new ArrayList<InstanceAttributeObj>();
		for (Map<String, String> map : resultList) {
			resultVo = ModifyInstanceAttributeObj.createVoFromMap(map);
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			resultVoList.add(resultVo);
			_InsAttCache.put(resultVo.getInsId() + ":" + resultVo.getKey(), resultVo);
		}

		return resultVoList;
	}

	public static InstanceAttributeObj setInstanceAttribute(InstanceAttributeObj vo) throws Exception {

		InstanceAttributeObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyInstanceAttributeObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_INS_ATT_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceAttributeObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_InsAttCache.put(vo.getInsId() + ":" + vo.getKey(), resultVo);
		}

		return resultVo;
	}

	public static InstanceAttributeObj putInstanceAttribute(InstanceAttributeObj vo) throws Exception {
		InstanceAttributeObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyInstanceAttributeObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 등록
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_INS_ATT_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceAttributeObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_InsAttCache.put(resultVo.getInsId() + ":" + resultVo.getKey(), vo);
		}

		return resultVo;
	}

	public static InstanceAttributeObj removeInstanceAttribute(String insId, String attKey) throws Exception {
		InstanceAttributeObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition1 = new KeyValueEntity<String, String>(
				IConst.InstanceAttribute.C_INS_ID, insId);
		KeyValueEntity<String, String> condition2 = new KeyValueEntity<String, String>(IConst.InstanceAttribute.C_KEY,
				attKey);
		conditionEntityList.add(condition1);
		conditionEntityList.add(condition2);

		// 스토리지에 데이터 삭제
		List<Map<String, String>> resultList = getStorageInstance().removeStorgeEntity(IStorageObject.C_INS_ATT_DATA,
				conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceAttributeObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬 삭제
		_InsAttCache.remove(resultVo.getInsId() + ":" + resultVo.getKey());

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 아답터 인스턴스 기능
	// ////////////////////////////////////////////////////////////////////////
	public static InstanceFunctionObj getInstanceFunction(String insId, String funcKey) throws Exception {
		InstanceFunctionObj resultVo = (InstanceFunctionObj) _InsFuncCache.get(insId + ":" + funcKey);

		if (resultVo == null) {
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition1 = new KeyValueEntity<String, String>(
					IConst.InstanceFunction.C_INS_ID, insId);
			KeyValueEntity<String, String> condition2 = new KeyValueEntity<String, String>(
					IConst.InstanceFunction.C_KEY, funcKey);
			conditionEntityList.add(condition1);
			conditionEntityList.add(condition2);

			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_INS_FUNC_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyInstanceFunctionObj.createVoFromMap(resultList.get(0));
			}

			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_InsFuncCache.put(insId + ":" + funcKey, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	public static List<InstanceFunctionObj> getInstanceFunctionList(String insId) throws Exception {
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition1 = new KeyValueEntity<String, String>(IConst.InstanceFunction.C_INS_ID,
				insId);
		conditionEntityList.add(condition1);

		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_INS_FUNC_DATA,
				conditionEntityList, _config);

		InstanceFunctionObj resultVo = null;
		List<InstanceFunctionObj> resultVoList = new ArrayList<InstanceFunctionObj>();
		for (Map<String, String> map : resultList) {
			resultVo = ModifyInstanceFunctionObj.createVoFromMap(map);
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			resultVoList.add(resultVo);
			_InsFuncCache.put(resultVo.getInsId() + ":" + resultVo.getKey(), resultVo);
		}

		return resultVoList;
	}

	public static InstanceFunctionObj setInstanceFunction(InstanceFunctionObj vo) throws Exception {
		InstanceFunctionObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyInstanceFunctionObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_INS_FUNC_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceFunctionObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_InsFuncCache.put(vo.getInsId() + ":" + vo.getKey(), resultVo);
		}

		return resultVo;
	}

	public static InstanceFunctionObj putInstanceFunction(InstanceFunctionObj vo) throws Exception {
		InstanceFunctionObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyInstanceFunctionObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 등록
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_INS_FUNC_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceFunctionObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_InsAttCache.put(resultVo.getInsId() + ":" + resultVo.getKey(), vo);
		}

		return resultVo;
	}

	public static InstanceFunctionObj removeInstanceFunction(String insId, String funcKey) throws Exception {
		InstanceFunctionObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition1 = new KeyValueEntity<String, String>(IConst.InstanceFunction.C_INS_ID,
				insId);
		KeyValueEntity<String, String> condition2 = new KeyValueEntity<String, String>(IConst.InstanceFunction.C_KEY,
				funcKey);
		conditionEntityList.add(condition1);
		conditionEntityList.add(condition2);

		// 스토리지에 데이터 삭제
		List<Map<String, String>> resultList = getStorageInstance().removeStorgeEntity(IStorageObject.C_INS_FUNC_DATA,
				conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyInstanceFunctionObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬 삭제
		_InsAttCache.remove(resultVo.getInsId() + ":" + resultVo.getKey());

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 장치풀
	// ////////////////////////////////////////////////////////////////////////
	public static DevicePoolObj getDevicePool(String devPoolId) throws Exception {

		// 캐쉬에서 조회
		DevicePoolObj resultVo = (DevicePoolObj) _DevicePoolCache.get(devPoolId);

		// 캐쉬에 해당 데이터가 존재하지 않을경우 스토리지에서 조회
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(
					IConst.DevicePool.C_DEVICE_POOL_ID, devPoolId);
			conditionEntityList.add(condition);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance()
					.getStorgeEntity(IStorageObject.C_DEVICE_POOL_DATA, conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyDevicePoolObj.createVoFromMap(resultList.get(0));
			}

			// 스토리지에 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_DevicePoolCache.put(devPoolId, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	public static List<DevicePoolObj> getAllDevicePool() throws Exception {

		List<DevicePoolObj> resultVoList = new ArrayList<DevicePoolObj>();

		// 스토리지에서 조회
		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_DEVICE_POOL_DATA,
				null, _config);

		DevicePoolObj resultVo = null;
		for (Map<String, String> map : resultList) {
			resultVo = ModifyDevicePoolObj.createVoFromMap(map);
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());

			_InsFuncCache.put(resultVo.getDevPoolId(), resultVo);

			resultVoList.add(resultVo);
		}

		return resultVoList;
	}

	public static DevicePoolObj setDevicePool(DevicePoolObj vo) throws Exception {
		DevicePoolObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyDevicePoolObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_DEVICE_POOL_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDevicePoolObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_DevicePoolCache.put(vo.getDevPoolId(), resultVo);
		}

		return resultVo;
	}

	public static DevicePoolObj putDevicePool(DevicePoolObj vo) throws Exception {
		DevicePoolObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyDevicePoolObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 등록
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_DEVICE_POOL_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDevicePoolObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_DevicePoolCache.put(vo.getDevPoolId(), resultVo);
		}
		return resultVo;
	}

	public static DevicePoolObj removeDevicePool(String devPoolId) throws Exception {
		DevicePoolObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(
				IConst.DevicePool.C_DEVICE_POOL_ID, devPoolId);
		conditionEntityList.add(condition);

		// 스토리지에서 삭제
		List<Map<String, String>> resultList = getStorageInstance()
				.removeStorgeEntity(IStorageObject.C_DEVICE_POOL_DATA, conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDevicePoolObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬 삭제
		_DevicePoolCache.remove(devPoolId);

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 장치
	// ////////////////////////////////////////////////////////////////////////
	public static DeviceObj getDevice(String devId) throws Exception {
		// 캐쉬에서 조회
		DeviceObj resultVo = (DeviceObj) _DeviceCache.get(devId);
		// 캐쉬에 해당 데이터가 존재하지 않을경우 스토리지에서 조회
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.Device.C_DEVICE_ID,
					devId);
			conditionEntityList.add(condition);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_DEVICE_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyDeviceObj.createVoFromMap(resultList.get(0));
			}

			// 스토리지에 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_DeviceCache.put(devId, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;

	}

	public static List<DeviceObj> getDeviceByDevPoolId(String devPoolId) throws Exception {
		List<DeviceObj> resultVoList = new ArrayList<DeviceObj>();

		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.Device.C_DEVICE_POOL_ID,
				devPoolId);
		conditionEntityList.add(condition);

		// 스토리지에서 조회
		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_DEVICE_DATA,
				conditionEntityList, _config);

		DeviceObj resultVo = null;
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, String> map : resultList) {
				resultVo = ModifyDeviceObj.createVoFromMap(map);
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_DeviceCache.put(resultVo.getDevId(), resultVo);

				resultVoList.add(resultVo);
			}
		}

		return resultVoList;
	}

	public static DeviceObj setDevice(DeviceObj vo) throws Exception {
		DeviceObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyDeviceObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_DEVICE_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDeviceObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_DeviceCache.put(vo.getDevId(), resultVo);
		}

		return resultVo;
	}

	public static DeviceObj putDevice(DeviceObj vo) throws Exception {
		DeviceObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyDeviceObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 등록
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_DEVICE_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDeviceObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_DeviceCache.put(vo.getDevId(), resultVo);
		}

		return resultVo;
	}

	public static DeviceObj removeDevice(String devId) throws Exception {
		DeviceObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.Device.C_DEVICE_ID, devId);
		conditionEntityList.add(condition);

		// 스토리지에서 삭제
		List<Map<String, String>> resultList = getStorageInstance().removeStorgeEntity(IStorageObject.C_DEVICE_DATA,
				conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDeviceObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬 삭제
		_DeviceCache.remove(devId);

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 유저풀
	// ////////////////////////////////////////////////////////////////////////
	public static UserPoolObj getUserPool(String userPoolId) throws Exception {
		// 캐쉬에서 조회
		UserPoolObj resultVo = (UserPoolObj) _UserPoolCache.get(userPoolId);
		// 캐쉬에 해당 데이터가 존재하지 않을경우 스토리지에서 조회
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(
					IConst.UserPool.C_USER_POOL_ID, userPoolId);
			conditionEntityList.add(condition);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_USER_POOL_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyUserPoolObj.createVoFromMap(resultList.get(0));
			}

			// 스토리지에 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_UserPoolCache.put(userPoolId, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	public static List<UserPoolObj> getAllUserPool() throws Exception {
		List<UserPoolObj> resultVoList = new ArrayList<UserPoolObj>();

		// 스토리지에서 조회
		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_USER_POOL_DATA,
				null, _config);

		UserPoolObj resultVo = null;
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, String> map : resultList) {
				resultVo = ModifyUserPoolObj.createVoFromMap(map);

				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_UserPoolCache.put(resultVo.getUserPoolId(), resultVo);

				resultVoList.add(resultVo);
			}
		}
		return resultVoList;
	}

	public static UserPoolObj setUserPool(UserPoolObj vo) throws Exception {
		UserPoolObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyUserPoolObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_USER_POOL_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserPoolObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_UserPoolCache.put(vo.getUserPoolId(), resultVo);
		}
		return resultVo;
	}

	public static UserPoolObj putUserPool(UserPoolObj vo) throws Exception {
		UserPoolObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyUserPoolObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_USER_POOL_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserPoolObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_UserPoolCache.put(vo.getUserPoolId(), resultVo);
		}
		return resultVo;
	}

	public static UserPoolObj removeUserPool(String userPoolId) throws Exception {
		UserPoolObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.UserPool.C_USER_POOL_ID,
				userPoolId);
		conditionEntityList.add(condition);

		// 스토리지에서 삭제
		List<Map<String, String>> resultList = getStorageInstance().removeStorgeEntity(IStorageObject.C_USER_POOL_DATA,
				conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserPoolObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬 삭제
		_UserPoolCache.remove(userPoolId);

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 유저
	// ////////////////////////////////////////////////////////////////////////
	public static UserObj getUser(String userId) throws Exception {
		// 캐쉬에서 조회
		UserObj resultVo = (UserObj) _UserCache.get(userId);
		// 캐쉬에 해당 데이터가 존재하지 않을경우 스토리지에서 조회
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.User.C_USER_ID,
					userId);
			conditionEntityList.add(condition);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_USER_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyUserObj.createVoFromMap(resultList.get(0));
			}

			// 스토리지에 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_UserPoolCache.put(userId, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	public static List<UserObj> getUserByUserPoolId(String userPoolId) throws Exception {
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.User.C_USER_POOL_ID,
				userPoolId);
		conditionEntityList.add(condition);

		// 스토리지에서 조회
		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_USER_DATA,
				conditionEntityList, _config);

		List<UserObj> resultVoList = new ArrayList<UserObj>();
		if (resultList != null && resultList.size() > 0) {
			UserObj resultVo = null;
			for (Map<String, String> map : resultList) {
				resultVo = ModifyUserObj.createVoFromMap(map);

				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_UserPoolCache.put(resultVo.getUserId(), resultVo);
				resultVoList.add(resultVo);
			}
		}

		return resultVoList;
	}

	public static UserObj setUser(UserObj vo) throws Exception {
		UserObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyUserObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_USER_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_UserCache.put(vo.getUserId(), resultVo);
		}
		return resultVo;
	}

	public static UserObj putUser(UserObj vo) throws Exception {
		UserObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyUserObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_USER_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_UserCache.put(vo.getUserId(), resultVo);
		}
		return resultVo;
	}

	public static UserObj removeUser(String userId) throws Exception {
		UserObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.User.C_USER_ID, userId);
		conditionEntityList.add(condition);

		// 스토리지에서 삭제
		List<Map<String, String>> resultList = getStorageInstance().removeStorgeEntity(IStorageObject.C_USER_DATA,
				conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬 삭제
		_UserCache.remove(userId);

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 유저 필터
	// ////////////////////////////////////////////////////////////////////////
	public static UserFilterObj getUserFilter(String userId, String authFilter) throws Exception {
		// 캐쉬에서 조회
		UserFilterObj resultVo = (UserFilterObj) _UserFilterCache.get(userId + ":" + authFilter);
		// 캐쉬에 해당 데이터가 존재하지 않을경우 스토리지에서 조회
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition1 = new KeyValueEntity<String, String>(IConst.UserFilter.C_USER_ID,
					userId);
			KeyValueEntity<String, String> condition2 = new KeyValueEntity<String, String>(
					IConst.UserFilter.C_AUTH_FILTER, authFilter);
			conditionEntityList.add(condition1);
			conditionEntityList.add(condition2);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance()
					.getStorgeEntity(IStorageObject.C_USER_FILTER_DATA, conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyUserFilterObj.createVoFromMap(resultList.get(0));
			}
			// 스토리지에 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_UserFilterCache.put(userId + ":" + authFilter, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	public static List<UserFilterObj> getUserFilterList(String userId) throws Exception {

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.UserFilter.C_USER_ID,
				userId);
		conditionEntityList.add(condition);

		// 스토리지에서 조회

		List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_USER_FILTER_DATA,
				conditionEntityList, _config);

		UserFilterObj resultVo = null;
		List<UserFilterObj> resultVoList = new ArrayList<UserFilterObj>();
		for (Map<String, String> map : resultList) {
			resultVo = ModifyUserFilterObj.createVoFromMap(map);

			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_UserFilterCache.put(resultVo.getUserId() + ":" + resultVo.getAuthFilter(), resultVo);

			resultVoList.add(resultVo);
		}

		return resultVoList;
	}

	public static UserFilterObj setUserFilter(UserFilterObj vo) throws Exception {
		UserFilterObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyUserFilterObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().setStorgeEntity(IStorageObject.C_USER_FILTER_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserFilterObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_UserFilterCache.put(vo.getUserId() + ":" + vo.getAuthFilter(), resultVo);
		}
		return resultVo;
	}

	public static UserFilterObj putUserFilter(UserFilterObj vo) throws Exception {
		UserFilterObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyUserFilterObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance().putStorgeEntity(IStorageObject.C_USER_FILTER_DATA,
				dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserFilterObj.createVoFromMap(resultList.get(0));
		}

		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_UserFilterCache.put(vo.getUserId() + ":" + vo.getAuthFilter(), resultVo);
		}
		return resultVo;
	}

	public static UserFilterObj removeUserFilter(String userId, String authFilter) throws Exception {
		UserFilterObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition1 = new KeyValueEntity<String, String>(IConst.UserFilter.C_USER_ID,
				userId);
		KeyValueEntity<String, String> condition2 = new KeyValueEntity<String, String>(IConst.UserFilter.C_AUTH_FILTER,
				authFilter);
		conditionEntityList.add(condition1);
		conditionEntityList.add(condition2);

		// 스토리지에서 삭제
		List<Map<String, String>> resultList = getStorageInstance()
				.removeStorgeEntity(IStorageObject.C_USER_FILTER_DATA, conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyUserFilterObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬 삭제
		_UserFilterCache.remove(userId + ":" + authFilter);

		return resultVo;
	}

	// ////////////////////////////////////////////////////////////////////////
	// 도메인 식별자 마스터
	// ////////////////////////////////////////////////////////////////////////
	public static DomainIdMastObj getDomainIdMast(String domainId) throws Exception {
		// 캐쉬에서 조회
		DomainIdMastObj resultVo = (DomainIdMastObj) _DomainIdMastCache.get(domainId);
		// 캐쉬에 해당 데이터가 존재하지 않을경우 스토리지에서 조회
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(
					IConst.DomainIdMast.C_DOMAIN_ID, domainId);
			conditionEntityList.add(condition);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance()
					.getStorgeEntity(IStorageObject.C_DOMAIN_ID_MAST_DATA, conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyDomainIdMastObj.createVoFromMap(resultList.get(0));
			}
			// 스토리지에 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_DomainIdMastCache.put(domainId, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	public static List<DomainIdMastObj> getDomainIdMastByDomainType(String domainType) throws Exception {
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.DomainIdMast.C_DOMAIN_TYPE,
				domainType);
		conditionEntityList.add(condition);

		// 스토리지에서 조회
		List<Map<String, String>> resultList = getStorageInstance()
				.getStorgeEntity(IStorageObject.C_DOMAIN_ID_MAST_DATA, conditionEntityList, _config);

		List<DomainIdMastObj> resultVoList = new ArrayList<DomainIdMastObj>();
		DomainIdMastObj resultVo = null;

		if (resultList != null && resultList.size() > 0) {
			for (Map<String, String> map : resultList) {
				resultVo = ModifyDomainIdMastObj.createVoFromMap(map);
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_DomainIdMastCache.put(resultVo.getDomainId(), resultVo);

				resultVoList.add(resultVo);
			}
		}

		return resultVoList;
	}

	public static DomainIdMastObj setDomainIdMast(DomainIdMastObj vo) throws Exception {
		DomainIdMastObj resultVo = null;
		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyDomainIdMastObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance()
				.setStorgeEntity(IStorageObject.C_DOMAIN_ID_MAST_DATA, dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDomainIdMastObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_DomainIdMastCache.put(vo.getDomainId(), resultVo);
		}
		return resultVo;
	}

	public static DomainIdMastObj putDomainIdMast(DomainIdMastObj vo) throws Exception {
		DomainIdMastObj resultVo = null;

		// 데이터 리스트 작성
		Map<String, String> dataMap = ModifyDomainIdMastObj.createMapFromVo(vo);
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		dataList.add(dataMap);

		// 스토리지에 데이터 변경
		List<Map<String, String>> resultList = getStorageInstance()
				.putStorgeEntity(IStorageObject.C_DOMAIN_ID_MAST_DATA, dataList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDomainIdMastObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬접속 정보 Update
		if (resultVo != null) {
			resultVo.setCacheAcessTime(System.currentTimeMillis());
			_DomainIdMastCache.put(vo.getDomainId(), resultVo);
		}
		return resultVo;
	}

	public static DomainIdMastObj removeDomainIdMast(String domainId) throws Exception {
		DomainIdMastObj resultVo = null;

		// 조건엔티티 리스트 작성
		List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
		KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.DomainIdMast.C_DOMAIN_ID,
				domainId);
		conditionEntityList.add(condition);

		// 스토리지에서 삭제
		List<Map<String, String>> resultList = getStorageInstance()
				.removeStorgeEntity(IStorageObject.C_DOMAIN_ID_MAST_DATA, conditionEntityList, _config);

		if (resultList != null && resultList.size() > 0) {
			resultVo = ModifyDomainIdMastObj.createVoFromMap(resultList.get(0));
		}
		// 캐쉬 삭제
		_DomainIdMastCache.remove(domainId);

		return resultVo;
	}

	public static MsgMastObj getMsgMast(String innerCode) throws Exception {
		// 캐쉬에서 조회
		MsgMastObj resultVo = (MsgMastObj) _MsgMastCache.get(innerCode);
		// 캐쉬에 해당 데이터가 존재하지 않을경우 스토리지에서 조회
		if (resultVo == null) {
			// 조건엔티티 리스트 작성
			List<KeyValueEntity<String, String>> conditionEntityList = new ArrayList<KeyValueEntity<String, String>>();
			KeyValueEntity<String, String> condition = new KeyValueEntity<String, String>(IConst.MsgMast.C_INNER_CODE,
					innerCode);
			conditionEntityList.add(condition);

			// 스토리지에서 조회
			List<Map<String, String>> resultList = getStorageInstance().getStorgeEntity(IStorageObject.C_MSG_MAST_DATA,
					conditionEntityList, _config);

			if (resultList != null && resultList.size() > 0) {
				resultVo = ModifyMsgMastObj.createVoFromMap(resultList.get(0));
			}
			// 스토리지에 해당 데이터가 존재하면
			if (resultVo != null) {
				// 캐쉬접속 정보 Update
				resultVo.setCacheAcessTime(System.currentTimeMillis());
				// 캐쉬등록
				_MsgMastCache.put(innerCode, resultVo);
			}
		} else {
			// 캐쉬접속 정보 Update
			resultVo.setCacheAcessTime(System.currentTimeMillis());
		}

		return resultVo;
	}

	/**
	 * 스냅샷
	 * 
	 * @param cacheName
	 *            캐쉬명
	 */
	public static void snapshot(String cacheName) {

		Map<String, String> dataMap = null;

		if (cacheName == null) {
			cacheName = IConst.EMPTY_STRING;
		}
		switch (cacheName) {
		case "adt-ins":
			// 아답터 인스턴스 출력
			for (String cacheKey : _InsCache.keySet()) {
				dataMap = ModifyInstanceObj.createMapFromVo((InstanceObj) _InsCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "ins-att":
			// 아답터 속성 출력
			for (String cacheKey : _InsAttCache.keySet()) {
				dataMap = ModifyInstanceAttributeObj.createMapFromVo((InstanceAttributeObj) _InsAttCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "ins-func":
			// 아답터 기능 출력
			for (String cacheKey : _InsFuncCache.keySet()) {
				dataMap = ModifyInstanceFunctionObj.createMapFromVo((InstanceFunctionObj) _InsFuncCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "dev-pool":
			// 장치풀 출력
			for (String cacheKey : _DevicePoolCache.keySet()) {
				dataMap = ModifyDevicePoolObj.createMapFromVo((DevicePoolObj) _DevicePoolCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "dev":
			// 장치 출력
			for (String cacheKey : _DeviceCache.keySet()) {
				dataMap = ModifyDeviceObj.createMapFromVo((DeviceObj) _DeviceCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "user-pool":
			// 유저풀 출력
			for (String cacheKey : _UserPoolCache.keySet()) {
				dataMap = ModifyUserPoolObj.createMapFromVo((UserPoolObj) _UserPoolCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "user":
			// 유저 출력
			for (String cacheKey : _UserCache.keySet()) {
				dataMap = ModifyUserObj.createMapFromVo((UserObj) _UserCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "user-filter":
			// 유저 출력
			for (String cacheKey : _UserFilterCache.keySet()) {
				dataMap = ModifyUserFilterObj.createMapFromVo((UserFilterObj) _UserFilterCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		case "domain":
			// 도메인 마스터 출력
			for (String cacheKey : _DomainIdMastCache.keySet()) {
				dataMap = ModifyDomainIdMastObj.createMapFromVo((DomainIdMastObj) _DomainIdMastCache.get(cacheKey));
				print(cacheKey, dataMap);
			}
			break;
		default:
			System.out.println("[adt-ins] : 아답터 인스턴스\n[ins-att] : 인스턴스 속성\n[ins-func]인스턴스 기능\n[dev-pool] : 장치풀\n"
					+ "[user-pool] : 유저풀\n[user] : 유저\n[user-filter] : 유저 필터\n[domain] : 도메인 식별자");
			break;
		}

	}

	/**
	 * Map항목 출력
	 * 
	 * @param key
	 *            대표키
	 * @param map
	 *            Map
	 */
	public static void print(String key, Map<String, String> map) {

		if (map == null)
			return;

		StringBuffer buf = new StringBuffer();

		buf.append(key).append("=[");
		for (String itemKey : map.keySet()) {
			buf.append(itemKey).append("=").append(map.get(itemKey)).append(",");
		}

		buf.delete(buf.length() - 1, buf.length()).append("]\r\n");

		System.out.print(buf.toString());
	}

	/**
	 * 캐시 초기화
	 */
	public static void cacheClear() {
		_InsCache.clear();
		_InsAttCache.clear();
		_InsFuncCache.clear();
		_UserPoolCache.clear();
		_UserCache.clear();
		_UserFilterCache.clear();
		_DomainIdMastCache.clear();
		_DevicePoolCache.clear();
		_DeviceCache.clear();
		_MsgMastCache.clear();
	}

	// ////////////////////////////////////////////////////////////////////////
	// 캐쉬 스케줄러
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 캐쉬 스케줄러 클래스
	 * 
	 * @author KANG
	 *
	 */
	public static class ScheduledCacheClean extends TimerTask {

		@Override
		public void run() {
			// Adapter 인스턴스 캐쉬 정리
			cacheClean("AdapterInstanceCache", _InsCache);
			// 인스턴스 속성 캐쉬 정리
			cacheClean("InstanceAttributeCache", _InsAttCache);
			// 인스턴스 기능 캐쉬 정리
			cacheClean("InstanceFunctionCache", _InsFuncCache);
			// 유저풀 캐쉬 정리
			cacheClean("UserPoolCache", _UserPoolCache);
			// 유저 캐쉬 정리
			cacheClean("UserProfileCache", _UserCache);
			// 유저 필터 캐쉬 정리
			cacheClean("UserProfileFilterCache", _UserFilterCache);
			// 도메인 식별자 마스터 캐쉬 정리
			cacheClean("DomainIdMastCache", _DomainIdMastCache);
			// 장치풀 캐쉬 정리
			cacheClean("DevicePoolCache", _DevicePoolCache);
			// 장치 캐쉬 정리
			cacheClean("DeviceProfileCache", _DeviceCache);

			// 메세지 마스터 캐쉬 정리
			cacheClean("MsgMastCache", _MsgMastCache);
		}

		/**
		 * 캐쉬 정리
		 * 
		 * @param cache
		 *            캐쉬
		 */
		private void cacheClean(String cacheName, NitroCache<String, Object> cache) {
			CacheManagerObj vo = null;
			long cacheVaildTime = Long.parseLong(NitroCacheManager.getEnv("CacheVaildTime"));
			List<String> removeKey = new ArrayList<String>();

			for (String key : cache.keySet()) {
				vo = (CacheManagerObj) cache.get(key);
				if (vo == null)
					continue;

				long currentTime = System.currentTimeMillis();
				if (cacheVaildTime < (currentTime - vo.getCacheAcessTime()) / 1000) {
					removeKey.add(key);
//					System.out.println("[cache delete] NAME = " + cacheName + ", key =" + key + ", CacheVaildTime(s) = "
//							+ cacheVaildTime + ", CurrentTime = " + TimeUtil.getYYYYMMddHHss2(currentTime)
//							+ ", CacheAcessTime = " + TimeUtil.getYYYYMMddHHss2(vo.getCacheAcessTime()));

				}
			}

			for (String key : removeKey) {
				cache.remove(key);
			}
		}

	}

}

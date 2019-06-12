////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.storage.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.pm.cache.NitroCacheManager;
import com.hdbsnc.smartiot.pm.storage.IStorageObject;
import com.hdbsnc.smartiot.pm.storage.KeyValueEntity;
import com.hdbsnc.smartiot.pm.util.TimeUtil;

public class SqliteDBManager implements IStorageObject {

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 커넥션 문자열을 구한다.
	 * 
	 * @param instance
	 * @param propfileNameKey
	 * @return
	 * @throws IOException
	 */
	static String getConnectionString(Object instance, String propfileNameKey)
			throws IOException {

		//유저 루트경로 취득
		//String rootPath = new File("").getAbsolutePath();
		String rootPath = System.getenv("SMARTIOT_HOME");
		String dirPath = rootPath + NitroCacheManager.getEnv("DbFilePath");

		if(rootPath==null) {
			rootPath = System.getenv("HOME");
			if(rootPath==null){
				rootPath = System.getenv("USERPROFILE");
			}				
			dirPath = rootPath + "/smartiot" + NitroCacheManager.getEnv("DbFilePath");
		}
		
		
		String filePath = dirPath + "/"
				+ NitroCacheManager.getEnv(propfileNameKey);

		File dir = new File(dirPath);
		File file = new File(filePath);

		// 오버라이트 true이거나 파일이 존재하지 않으면 리소스 DB파일을 복사
		if ("true".equals(NitroCacheManager.getEnv("DbFileOverwrite"))
				|| !file.isFile()) {

			if (!"".equals(dirPath)) {
				dir.mkdirs();
			}
			
			// 파일이 존재한 경우 백업처리
			if(file.isFile()){
				boolean result = file.renameTo(new File(filePath + "_" + TimeUtil.getYYYYMMddHHss()));
				if(result == false){
					throw new IOException("DB파일 백업 실패.");
				}
			}
			
			URL dbFileUrl = instance.getClass().getResource(
					"/" + NitroCacheManager.getEnv(propfileNameKey));
			InputStream is = null;
			OutputStream os = null;
			try {
				is = dbFileUrl.openStream();

				os = new FileOutputStream(file);

				byte[] buf = new byte[1024];
				int len = 0;

				while ((len = is.read(buf)) > 0) {
					os.write(buf, 0, len);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					os.close();
				}

				if (is != null) {
					is.close();
				}
			}
		}

		return "jdbc:sqlite:" + filePath;
	}

	@Override
	public List<Map<String, String>> getStorgeEntity(String type,
			List<KeyValueEntity<String, String>> conditionList, Map<String, String> config)
			throws Exception {

		List<Map<String, String>> resultList = null;

		switch (type) {
		case IStorageObject.C_ADT_INS_DATA:
			resultList = SqliteInstanceDao.getInstance().selectIns(
					conditionList, config);
			break;
		case IStorageObject.C_INS_ATT_DATA:
			resultList = SqliteInstanceDao.getInstance().selectAttribute(
					conditionList);
			break;
		case IStorageObject.C_INS_FUNC_DATA:
			resultList = SqliteInstanceDao.getInstance().selectFunction(
					conditionList);
			break;
		case IStorageObject.C_USER_POOL_DATA:
			resultList = SqliteUserDao.getInstance().selectUserPool(
					conditionList);
			break;
		case IStorageObject.C_USER_DATA:
			resultList = SqliteUserDao.getInstance().selectUser(
					conditionList);
			break;
		case IStorageObject.C_USER_FILTER_DATA:
			resultList = SqliteUserDao.getInstance().selectUserFilter(
					conditionList);
			break;
		case IStorageObject.C_DOMAIN_ID_MAST_DATA:
			resultList = SqliteDomainDao.getInstance().selectDomain(
					conditionList);
			break;
		case IStorageObject.C_DEVICE_POOL_DATA:
			resultList = SqliteDeviceDao.getInstance().selectDevicePool(
					conditionList);
			break;
		case IStorageObject.C_DEVICE_DATA:
			resultList = SqliteDeviceDao.getInstance().selectDevice(
					conditionList);
			break;
			
		case IStorageObject.C_MSG_MAST_DATA:
			resultList = SqliteMsgMastDao.getInstance().selectMsgMast(conditionList);
			break;
			
		default:
			break;
		}
		return resultList;
	}

	@Override
	public List<Map<String, String>> setStorgeEntity(String type,
			List<Map<String, String>> dataList, Map<String, String> config) throws Exception {

		List<Map<String, String>> resultList = null;
		switch (type) {
		case IStorageObject.C_ADT_INS_DATA:
			resultList = SqliteInstanceDao.getInstance()
					.updateIns(dataList, config);
			break;
		case IStorageObject.C_INS_ATT_DATA:
			resultList = SqliteInstanceDao.getInstance().updateAttribute(
					dataList);
			break;
		case IStorageObject.C_INS_FUNC_DATA:
			resultList = SqliteInstanceDao.getInstance().updateFunction(
					dataList);
			break;
		case IStorageObject.C_USER_POOL_DATA:
			resultList = SqliteUserDao.getInstance()
					.updateUserPool(dataList);
			break;
		case IStorageObject.C_USER_DATA:
			resultList = SqliteUserDao.getInstance().updateUser(dataList);
			break;
		case IStorageObject.C_USER_FILTER_DATA:
			resultList = SqliteUserDao.getInstance().updateUserFilter(
					dataList);
			break;
		case IStorageObject.C_DOMAIN_ID_MAST_DATA:
			resultList = SqliteDomainDao.getInstance().updateDomain(dataList);
			break;
		case IStorageObject.C_DEVICE_POOL_DATA:
			resultList = SqliteDeviceDao.getInstance().updateDevicePool(
					dataList);
			break;
		case IStorageObject.C_DEVICE_DATA:
			resultList = SqliteDeviceDao.getInstance().updateDevice(dataList);
			break;
		default:
			break;
		}

		return resultList;
	}

	@Override
	public List<Map<String, String>> putStorgeEntity(String type,
			List<Map<String, String>> dataList, Map<String, String> config) throws Exception {

		List<Map<String, String>> resultList = null;

		switch (type) {
		case IStorageObject.C_ADT_INS_DATA:
			resultList = SqliteInstanceDao.getInstance()
					.insertIns(dataList, config);
			break;
		case IStorageObject.C_INS_ATT_DATA:
			resultList = SqliteInstanceDao.getInstance().insertAttribute(
					dataList);
			break;
		case IStorageObject.C_INS_FUNC_DATA:
			resultList = SqliteInstanceDao.getInstance().insertFunction(
					dataList);
			break;
		case IStorageObject.C_USER_POOL_DATA:
			resultList = SqliteUserDao.getInstance()
					.insertUserPool(dataList);
			break;
		case IStorageObject.C_USER_DATA:
			resultList = SqliteUserDao.getInstance().insertUser(dataList);
			break;
		case IStorageObject.C_USER_FILTER_DATA:
			resultList = SqliteUserDao.getInstance().insertUserFilter(
					dataList);
			break;
		case IStorageObject.C_DOMAIN_ID_MAST_DATA:
			resultList = SqliteDomainDao.getInstance().insertDomain(dataList);
			break;
		case IStorageObject.C_DEVICE_POOL_DATA:
			resultList = SqliteDeviceDao.getInstance().insertDevicePool(
					dataList);
			break;
		case IStorageObject.C_DEVICE_DATA:
			resultList = SqliteDeviceDao.getInstance().insertDevice(dataList);
			break;
		default:
			break;
		}
		return resultList;
	}

	@Override
	public List<Map<String, String>> removeStorgeEntity(String type,
			List<KeyValueEntity<String, String>> conditionList, Map<String, String> config)
			throws Exception {

		List<Map<String, String>> resultList = null;

		switch (type) {
		case IStorageObject.C_ADT_INS_DATA:
			resultList = SqliteInstanceDao.getInstance().deleteIns(
					conditionList, config);
			break;
		case IStorageObject.C_INS_ATT_DATA:
			resultList = SqliteInstanceDao.getInstance().deleteAttribute(
					conditionList);
			break;
		case IStorageObject.C_INS_FUNC_DATA:
			resultList = SqliteInstanceDao.getInstance().deleteFunction(
					conditionList);
			break;
		case IStorageObject.C_USER_POOL_DATA:
			resultList = SqliteUserDao.getInstance().deleteUserPool(
					conditionList);
			break;
		case IStorageObject.C_USER_DATA:
			resultList = SqliteUserDao.getInstance().deleteUser(
					conditionList);
			break;
		case IStorageObject.C_USER_FILTER_DATA:
			resultList = SqliteUserDao.getInstance().deleteUserFilter(
					conditionList);
			break;
		case IStorageObject.C_DOMAIN_ID_MAST_DATA:
			resultList = SqliteDomainDao.getInstance().deleteDomain(
					conditionList);
			break;
		case IStorageObject.C_DEVICE_POOL_DATA:
			resultList = SqliteDeviceDao.getInstance().deleteDevicePool(
					conditionList);
			break;
		case IStorageObject.C_DEVICE_DATA:
			resultList = SqliteDeviceDao.getInstance().deleteDevice(
					conditionList);
			break;
		default:
			break;
		}
		return resultList;
	}
	
	public void selectQuery(String query) throws Exception {
		
		if(query.indexOf("T_ADT_INS") > 0){
			SqliteInstanceDao.getInstance().selectQuery(query);
		} else if(query.indexOf("T_DEVICE") > 0) {
			SqliteDeviceDao.getInstance().selectQuery(query);
		} else if(query.indexOf("T_USER") > 0) {
			SqliteUserDao.getInstance().selectQuery(query);
		} else if(query.indexOf("T_DOMAIN") > 0) {
			SqliteDomainDao.getInstance().selectQuery(query);
		}
		return;
	}
}

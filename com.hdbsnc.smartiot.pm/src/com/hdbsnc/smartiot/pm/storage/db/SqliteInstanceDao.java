////////////////////////////////////////////////////////////////////
// Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
////////////////////////////////////////////////////////////////////
package com.hdbsnc.smartiot.pm.storage.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hdbsnc.smartiot.pm.constant.IConst;
import com.hdbsnc.smartiot.pm.storage.KeyValueEntity;
import com.hdbsnc.smartiot.pm.util.TimeUtil;

public class SqliteInstanceDao {

	final private Object syncObj = new Object();
	private static SqliteInstanceDao instance;
	private static String connectionString = null;

	
	static {
		instance = new SqliteInstanceDao();
		try {
			connectionString = SqliteDBManager.getConnectionString(instance, "AdapterDbFileName");
			
			System.out.println(connectionString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 클래스 인스턴스 취득
	 * 
	 * @return 아답터 DAO
	 * @throws IOException
	 */
	public static SqliteInstanceDao getInstance() throws IOException {
//		if (instance == null) {
//			instance = new SqliteInstanceDao();
//
//			connectionString = SqliteDBManager.getConnectionString(instance, "AdapterDbFileName");
//		}
		return instance;
	}

	/**
	 * DB커넥션 취득
	 * 
	 * @return DB커넥션
	 * @throws SQLException
	 *             예외
	 */
	private Connection getConnection() throws Exception {
		return DriverManager.getConnection(connectionString);
	}

	/**
	 * 아답터 인스턴스 조회
	 * 
	 * @param con
	 *            DB커넥션
	 * @param conditionEntity
	 *            조건엔티티
	 * @return 결과리스트
	 * @throws SQLException
	 *             예외
	 */
	private List<Map<String, String>> selectIns(Connection con,List<KeyValueEntity<String, String>> conditionEntity, Map<String, String> config)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> selectList = new ArrayList<Map<String, String>>();
		Map<String, String> selectMap = null;

		StringBuffer query = new StringBuffer("SELECT * FROM T_ADT_INS where ");
		int cnt = 0;
		for (KeyValueEntity<String, String> entity : conditionEntity) {
			if (cnt > 0) {
				query.append(" AND ");
			}

			query.append(entity.getKey()).append("==?");
			cnt++;
		}
		query.append(";");

		psmt = con.prepareStatement(query.toString());
		cnt = 1;
		for (KeyValueEntity<String, String> entity : conditionEntity) {
			psmt.setString(cnt, entity.getValue());
			cnt++;
		}

		// 쿼리 실행
		rs = psmt.executeQuery();

		String convertUrl = null;
		while (rs.next()) {
			selectMap = new HashMap<String, String>();

			selectMap.put(IConst.AdapterInstance.C_INS_ID, rs.getString(IConst.AdapterInstance.C_INS_ID));
			selectMap.put(IConst.AdapterInstance.C_DEV_POOL_ID, rs.getString(IConst.AdapterInstance.C_DEV_POOL_ID));
			selectMap.put(IConst.AdapterInstance.C_ADT_ID, rs.getString(IConst.AdapterInstance.C_ADT_ID));
			selectMap.put(IConst.AdapterInstance.C_INS_NAME, rs.getString(IConst.AdapterInstance.C_INS_NAME));
			selectMap.put(IConst.AdapterInstance.C_INS_KIND, rs.getString(IConst.AdapterInstance.C_INS_KIND));
			selectMap.put(IConst.AdapterInstance.C_DEFAULT_DID, rs.getString(IConst.AdapterInstance.C_DEFAULT_DID));
			selectMap.put(IConst.AdapterInstance.C_INS_TYPE, rs.getString(IConst.AdapterInstance.C_INS_TYPE));
			selectMap.put(IConst.AdapterInstance.C_IS_USE, rs.getString(IConst.AdapterInstance.C_IS_USE));
			selectMap.put(IConst.AdapterInstance.C_SESSION_TIMEOUT,
					rs.getString(IConst.AdapterInstance.C_SESSION_TIMEOUT));
			selectMap.put(IConst.AdapterInstance.C_INIT_DEV_STATUS,
					rs.getString(IConst.AdapterInstance.C_INIT_DEV_STATUS));
			selectMap.put(IConst.AdapterInstance.C_IP, rs.getString(IConst.AdapterInstance.C_IP));
			selectMap.put(IConst.AdapterInstance.C_PORT, rs.getString(IConst.AdapterInstance.C_PORT));
			
			if(rs.getString(IConst.AdapterInstance.C_URL) != null){
				convertUrl = rs.getString(IConst.AdapterInstance.C_URL);
				convertUrl = convertUrl.replaceAll("dynamichost", config.get("server.dynamichost"));
			}
			
			selectMap.put(IConst.AdapterInstance.C_URL, convertUrl);
			
			selectMap.put(IConst.AdapterInstance.C_LAT, rs.getString(IConst.AdapterInstance.C_LAT));
			selectMap.put(IConst.AdapterInstance.C_LON, rs.getString(IConst.AdapterInstance.C_LON));
			selectMap.put(IConst.AdapterInstance.C_SELF_ID, rs.getString(IConst.AdapterInstance.C_SELF_ID));
			selectMap.put(IConst.AdapterInstance.C_SELF_PW, rs.getString(IConst.AdapterInstance.C_SELF_PW));
			selectMap.put(IConst.AdapterInstance.C_REMARK, rs.getString(IConst.AdapterInstance.C_REMARK));
			selectMap.put(IConst.AdapterInstance.C_ALTER_DATE, TimeUtil.changeFormat(rs.getString(IConst.AdapterInstance.C_ALTER_DATE)));
			selectMap.put(IConst.AdapterInstance.C_REG_DATE, TimeUtil.changeFormat(rs.getString(IConst.AdapterInstance.C_REG_DATE)));

			selectList.add(selectMap);
		}

		rs.close();
		psmt.close();

		return selectList;
	}
	
	

	/**
	 * 
	 * 인스턴스 속성 조회
	 * 
	 * @param con
	 *            DB커넥션
	 * @param conditionEntity
	 *            조건 엔티티
	 * @return 결과리스트
	 * @throws SQLException
	 *             예외
	 */
	private List<Map<String, String>> selectAttribute(Connection con,
			List<KeyValueEntity<String, String>> conditionEntity) throws SQLException {

		List<Map<String, String>> selectList = new ArrayList<Map<String, String>>();
		Map<String, String> selectMap = null;

		PreparedStatement psmt = null;
		ResultSet rs = null;
		int cnt = 0;

		StringBuffer query = new StringBuffer("SELECT * FROM T_ADT_INS_ATT where ");

		for (KeyValueEntity<String, String> entity : conditionEntity) {
			if (cnt > 0) {
				query.append(" AND ");
			}
			query.append(entity.getKey()).append("==?");
			cnt++;
		}
		query.append(";");

		psmt = con.prepareStatement(query.toString());

		cnt = 1;
		for (KeyValueEntity<String, String> entity : conditionEntity) {
			psmt.setString(cnt, entity.getValue());
			cnt++;
		}

		rs = psmt.executeQuery();

		while (rs.next()) {
			selectMap = new HashMap<String, String>();

			selectMap.put(IConst.InstanceAttribute.C_INS_ID, rs.getString(IConst.InstanceAttribute.C_INS_ID));
			selectMap.put(IConst.InstanceAttribute.C_KEY, rs.getString(IConst.InstanceAttribute.C_KEY));
			selectMap.put(IConst.InstanceAttribute.C_DSCT, rs.getString(IConst.InstanceAttribute.C_DSCT));
			selectMap.put(IConst.InstanceAttribute.C_VALUE, rs.getString(IConst.InstanceAttribute.C_VALUE));
			selectMap.put(IConst.InstanceAttribute.C_VALUE_TYPE, rs.getString(IConst.InstanceAttribute.C_VALUE_TYPE));
			selectMap.put(IConst.InstanceAttribute.C_INIT, rs.getString(IConst.InstanceAttribute.C_INIT));
			selectMap.put(IConst.InstanceAttribute.C_REMARK, rs.getString(IConst.InstanceAttribute.C_REMARK));
			selectMap.put(IConst.InstanceAttribute.C_ALTER_DATE, TimeUtil.changeFormat(rs.getString(IConst.InstanceAttribute.C_ALTER_DATE)));
			selectMap.put(IConst.InstanceAttribute.C_REG_DATE, TimeUtil.changeFormat(rs.getString(IConst.InstanceAttribute.C_REG_DATE)));

			selectList.add(selectMap);
		}
		return selectList;
	}

	/**
	 * 인스턴스 기능 조회
	 * 
	 * @param con
	 * @param conditionEntity
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, String>> selectFunction(Connection con,
			List<KeyValueEntity<String, String>> conditionEntity) throws SQLException {
		List<Map<String, String>> selectList = new ArrayList<Map<String, String>>();
		Map<String, String> selectMap = null;

		PreparedStatement psmt = null;
		ResultSet rs = null;
		int cnt = 0;

		StringBuffer query = new StringBuffer("SELECT * FROM T_ADT_INS_FUNC where ");

		for (KeyValueEntity<String, String> entity : conditionEntity) {
			if (cnt > 0) {
				query.append(" AND ");
			}
			query.append(entity.getKey()).append("==?");
			cnt++;
		}
		query.append(";");

		psmt = con.prepareStatement(query.toString());

		cnt = 1;
		for (KeyValueEntity<String, String> entity : conditionEntity) {
			psmt.setString(cnt, entity.getValue());
			cnt++;
		}

		rs = psmt.executeQuery();

		while (rs.next()) {
			selectMap = new HashMap<String, String>();

			selectMap.put(IConst.InstanceFunction.C_INS_ID, rs.getString(IConst.InstanceFunction.C_INS_ID));
			selectMap.put(IConst.InstanceFunction.C_KEY, rs.getString(IConst.InstanceFunction.C_KEY));
			selectMap.put(IConst.InstanceFunction.C_DSCT, rs.getString(IConst.InstanceFunction.C_DSCT));
			selectMap.put(IConst.InstanceFunction.C_CONT_TYPE, rs.getString(IConst.InstanceFunction.C_CONT_TYPE));
			selectMap.put(IConst.InstanceFunction.C_PARAM1, rs.getString(IConst.InstanceFunction.C_PARAM1));
			selectMap.put(IConst.InstanceFunction.C_PARAM2, rs.getString(IConst.InstanceFunction.C_PARAM2));
			selectMap.put(IConst.InstanceFunction.C_PARAM3, rs.getString(IConst.InstanceFunction.C_PARAM3));
			selectMap.put(IConst.InstanceFunction.C_PARAM4, rs.getString(IConst.InstanceFunction.C_PARAM4));
			selectMap.put(IConst.InstanceFunction.C_PARAM5, rs.getString(IConst.InstanceFunction.C_PARAM5));
			selectMap.put(IConst.InstanceFunction.C_PARAM_TYPE1, rs.getString(IConst.InstanceFunction.C_PARAM_TYPE1));
			selectMap.put(IConst.InstanceFunction.C_PARAM_TYPE2, rs.getString(IConst.InstanceFunction.C_PARAM_TYPE2));
			selectMap.put(IConst.InstanceFunction.C_PARAM_TYPE3, rs.getString(IConst.InstanceFunction.C_PARAM_TYPE3));
			selectMap.put(IConst.InstanceFunction.C_PARAM_TYPE4, rs.getString(IConst.InstanceFunction.C_PARAM_TYPE4));
			selectMap.put(IConst.InstanceFunction.C_PARAM_TYPE5, rs.getString(IConst.InstanceFunction.C_PARAM_TYPE5));
			selectMap.put(IConst.InstanceFunction.C_REMARK, rs.getString(IConst.InstanceFunction.C_REMARK));
			selectMap.put(IConst.InstanceFunction.C_ALTER_DATE, TimeUtil.changeFormat(rs.getString(IConst.InstanceFunction.C_ALTER_DATE)));
			selectMap.put(IConst.InstanceFunction.C_REG_DATE, TimeUtil.changeFormat(rs.getString(IConst.InstanceFunction.C_REG_DATE)));

			selectList.add(selectMap);
		}
		return selectList;
	}

	/**
	 * 아답터 인스턴스 정보 조회
	 * 
	 * @param conditionEntity
	 *            엔티티
	 * @return 결과리스트
	 * @throws Throwable
	 */
	public List<Map<String, String>> selectIns(List<KeyValueEntity<String, String>> conditionEntity, Map<String, String> config) throws Exception {

		Connection con = null;

		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {

			try {
				con = getConnection();
				selectList = selectIns(con, conditionEntity, config);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

			return selectList;
		}
	}

	/**
	 * 인스턴스 속성 조회
	 * 
	 * @param conditionEntity
	 *            조건엔티티
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> selectAttribute(List<KeyValueEntity<String, String>> conditionEntity)
			throws Exception {
		Connection con = null;

		List<Map<String, String>> resultList = null;
		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);
				resultList = selectAttribute(con, conditionEntity);
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 인스턴스 기능 조회
	 * 
	 * @param conditionEntity
	 *            조건엔티티
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> selectFunction(List<KeyValueEntity<String, String>> conditionEntity)
			throws Exception {
		Connection con = null;

		List<Map<String, String>> resultList = null;
		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);
				resultList = selectFunction(con, conditionEntity);
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 아답터 인스턴스 정보 변경
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> updateIns(List<Map<String, String>> dataList, Map<String, String> config) throws Exception {

		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

		List<Map<String, String>> selectList = null;

		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> entity = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> map : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();
//					entity = new KeyValueEntity<String, String>(IConst.AdapterInstance.C_DEV_POOL_ID,
//							map.get(IConst.AdapterInstance.C_DEV_POOL_ID));
//					conditionList.add(entity);

					// 장치풀 아이디 중복 확인 -> 장치풀 중복은 여기서 안함
//					selectList = selectIns(con, conditionList);
//
//					if (selectList.size() > 0) {
//						throw new SQLException("장치풀ID 중복");
//					}

					// 대상데이터 존재 유무 체크
					//conditionList.clear();
					entity = new KeyValueEntity<String, String>(IConst.AdapterInstance.C_INS_ID,
							map.get(IConst.AdapterInstance.C_INS_ID));
					conditionList.add(entity);

					selectList = selectIns(con, conditionList, config);

					if (selectList.size() != 1) {
						throw new SQLException("변경 대상 부정합");
					}

					// Update 실시

					psmt = con.prepareStatement("UPDATE T_ADT_INS SET "
							+ "devPoolId=?, adtId=?, insNm=?, insKind=?, defaultDevId=?, insType=?, isUse=?, sessionTimeout=?,"
							+ "initDevStatus=?,  ip=?, port=?, url=?, lat=?, lon=?, selfId=?, selfPw=?, remark=?, alterDate=? "
							+ "where insId == ?;");

					psmt.setString(1, map.get(IConst.AdapterInstance.C_DEV_POOL_ID));
					psmt.setString(2, map.get(IConst.AdapterInstance.C_ADT_ID));
					psmt.setString(3, map.get(IConst.AdapterInstance.C_INS_NAME));
					psmt.setString(4, map.get(IConst.AdapterInstance.C_INS_KIND));
					psmt.setString(5, map.get(IConst.AdapterInstance.C_DEFAULT_DID));
					psmt.setString(6, map.get(IConst.AdapterInstance.C_INS_TYPE));
					psmt.setString(7, map.get(IConst.AdapterInstance.C_IS_USE));
					psmt.setString(8, map.get(IConst.AdapterInstance.C_SESSION_TIMEOUT));
					psmt.setString(9, map.get(IConst.AdapterInstance.C_INIT_DEV_STATUS));
					psmt.setString(10, map.get(IConst.AdapterInstance.C_IP));
					psmt.setString(11, map.get(IConst.AdapterInstance.C_PORT));
					psmt.setString(12, map.get(IConst.AdapterInstance.C_URL));
					psmt.setString(13, map.get(IConst.AdapterInstance.C_LAT));
					psmt.setString(14, map.get(IConst.AdapterInstance.C_LON));
					psmt.setString(15, map.get(IConst.AdapterInstance.C_SELF_ID));
					psmt.setString(16, map.get(IConst.AdapterInstance.C_SELF_PW));
					psmt.setString(17, map.get(IConst.AdapterInstance.C_REMARK));
					map.put(IConst.AdapterInstance.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(18, map.get(IConst.AdapterInstance.C_ALTER_DATE));

					// PK설정
					psmt.setString(19, map.get(IConst.AdapterInstance.C_INS_ID));

					// Query 발행
					psmt.executeUpdate();

					// 결과리스트 설정
					map.put(IConst.AdapterInstance.C_REG_DATE,
							selectList.get(0).get(IConst.AdapterInstance.C_REG_DATE));

					resultList.add(map);
				}

				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * 아답터 속성 변경
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> updateAttribute(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;
		List<KeyValueEntity<String, String>> conditionEntity = new ArrayList<KeyValueEntity<String, String>>();
		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				PreparedStatement psmt = null;

				for (Map<String, String> map : dataList) {
					// 변경 대상 유무를 체크하여
					KeyValueEntity<String, String> entity1 = new KeyValueEntity<String, String>(
							IConst.InstanceAttribute.C_INS_ID, map.get(IConst.InstanceAttribute.C_INS_ID));
					KeyValueEntity<String, String> entity2 = new KeyValueEntity<String, String>(
							IConst.InstanceAttribute.C_KEY, map.get(IConst.InstanceAttribute.C_KEY));

					conditionEntity.clear();
					conditionEntity.add(entity1);
					conditionEntity.add(entity2);

					selectList = selectAttribute(con, conditionEntity);

					if (selectList.size() == 0) {
						// 변경대상이 존재 하지 않을 경우 예외처리
						throw new SQLException("변경 대상 레코드가 없음");
					}

					psmt = con.prepareStatement("UPDATE T_ADT_INS_ATT SET "
							+ "dsct=?, value=?, valueType =? ,remark=?, alterDate=? where insId == ? AND key == ?;");
					psmt.setString(1, map.get(IConst.InstanceAttribute.C_DSCT));
					psmt.setString(2, map.get(IConst.InstanceAttribute.C_VALUE));
					psmt.setString(3, map.get(IConst.InstanceAttribute.C_VALUE_TYPE));
					psmt.setString(4, map.get(IConst.InstanceAttribute.C_REMARK));
					map.put(IConst.InstanceAttribute.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(5, map.get(IConst.InstanceAttribute.C_ALTER_DATE));

					psmt.setString(6, map.get(IConst.InstanceAttribute.C_INS_ID));
					psmt.setString(7, map.get(IConst.InstanceAttribute.C_KEY));

					// Query 발행
					psmt.executeUpdate();

					// 변경된 항목을 저장
					map.put(IConst.InstanceAttribute.C_REG_DATE,
							selectList.get(0).get(IConst.InstanceAttribute.C_REG_DATE));

					// 결과리스트에 데이터 설정
					resultList.add(map);
				}

				// commit
				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * 아답터 기능 변경
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> updateFunction(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;
		List<KeyValueEntity<String, String>> conditionEntity = new ArrayList<KeyValueEntity<String, String>>();
		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				PreparedStatement psmt = null;

				for (Map<String, String> map : dataList) {
					// 변경 대상 유무를 체크하여
					KeyValueEntity<String, String> entity1 = new KeyValueEntity<String, String>(
							IConst.InstanceFunction.C_INS_ID, map.get(IConst.InstanceFunction.C_INS_ID));
					KeyValueEntity<String, String> entity2 = new KeyValueEntity<String, String>(
							IConst.InstanceFunction.C_KEY, map.get(IConst.InstanceFunction.C_KEY));

					conditionEntity.clear();
					conditionEntity.add(entity1);
					conditionEntity.add(entity2);

					selectList = selectFunction(con, conditionEntity);

					if (selectList.size() == 0) {
						// 변경대상이 존재 하지 않을 경우 예외처리
						throw new SQLException("업데이트 대상이 없음.");
					}

					psmt = con.prepareStatement("UPDATE T_ADT_INS_FUNC SET "
							+ "dsct=?, contType=?, param1=?, param2 =?, param3=?, param4=?, param5=?, paramType1=?, paramType2=?, paramType3=?, paramType4=?, paramType5=?, remark=?, alterDate=? WHERE insId == ? AND key == ?;");
					psmt.setString(1, map.get(IConst.InstanceFunction.C_DSCT));
					psmt.setString(2, map.get(IConst.InstanceFunction.C_CONT_TYPE));
					psmt.setString(3, map.get(IConst.InstanceFunction.C_PARAM1));
					psmt.setString(4, map.get(IConst.InstanceFunction.C_PARAM2));
					psmt.setString(5, map.get(IConst.InstanceFunction.C_PARAM3));
					psmt.setString(6, map.get(IConst.InstanceFunction.C_PARAM4));
					psmt.setString(7, map.get(IConst.InstanceFunction.C_PARAM5));
					psmt.setString(8, map.get(IConst.InstanceFunction.C_PARAM_TYPE1));
					psmt.setString(9, map.get(IConst.InstanceFunction.C_PARAM_TYPE2));
					psmt.setString(10, map.get(IConst.InstanceFunction.C_PARAM_TYPE3));
					psmt.setString(11, map.get(IConst.InstanceFunction.C_PARAM_TYPE4));
					psmt.setString(12, map.get(IConst.InstanceFunction.C_PARAM_TYPE5));
					psmt.setString(13, map.get(IConst.InstanceFunction.C_REMARK));
					map.put(IConst.InstanceFunction.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(14, map.get(IConst.InstanceFunction.C_ALTER_DATE));
					psmt.setString(15, map.get(IConst.InstanceFunction.C_INS_ID));
					psmt.setString(16, map.get(IConst.InstanceFunction.C_KEY));

					// Query 발행
					psmt.executeUpdate();

					// 변경된 항목을 저장
					map.put(IConst.InstanceFunction.C_REG_DATE,
							selectList.get(0).get(IConst.InstanceFunction.C_REG_DATE));

					// 결과리스트에 데이터 설정
					resultList.add(map);
				}

				// commit
				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 아답터 인스턴스 등록
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> insertIns(List<Map<String, String>> dataList, Map<String, String> config) throws Exception {
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;

		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> entity = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> data : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();
					entity = new KeyValueEntity<String, String>(IConst.AdapterInstance.C_INS_ID,
							data.get(IConst.AdapterInstance.C_INS_ID));
					conditionList.add(entity);
					selectList = selectIns(con, conditionList, config);

					if (selectList.size() > 0) {
						throw new SQLException("PK중복");
					}

					psmt = con.prepareStatement("INSERT INTO T_ADT_INS VALUES "
							+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
					// 인스턴스ID
					psmt.setString(1, data.get(IConst.AdapterInstance.C_INS_ID));
					// 장치풀ID
					psmt.setString(2, data.get(IConst.AdapterInstance.C_DEV_POOL_ID));
					// 아답터ID
					psmt.setString(3, data.get(IConst.AdapterInstance.C_ADT_ID));
					// 인스턴스명
					psmt.setString(4, data.get(IConst.AdapterInstance.C_INS_NAME));
					// 인스턴스종류
					psmt.setString(5, data.get(IConst.AdapterInstance.C_INS_KIND));
					// 디폴트 장치ID
					psmt.setString(6, data.get(IConst.AdapterInstance.C_DEFAULT_DID));
					// 인스턴스구분
					psmt.setString(7, data.get(IConst.AdapterInstance.C_INS_TYPE));
					// 사용여부
					psmt.setString(8, data.get(IConst.AdapterInstance.C_IS_USE));
					// 섹션타임아웃
					psmt.setString(9, data.get(IConst.AdapterInstance.C_SESSION_TIMEOUT));
					// 초기기동상태
					psmt.setString(10, data.get(IConst.AdapterInstance.C_INIT_DEV_STATUS));
					// IP
					psmt.setString(11, data.get(IConst.AdapterInstance.C_IP));
					// Port
					psmt.setString(12, data.get(IConst.AdapterInstance.C_PORT));
					// Port
					psmt.setString(13, data.get(IConst.AdapterInstance.C_URL));
					// 위도
					psmt.setString(14, data.get(IConst.AdapterInstance.C_LAT));
					// 경도
					psmt.setString(15, data.get(IConst.AdapterInstance.C_LON));
					// 장치ID
					psmt.setString(16, data.get(IConst.AdapterInstance.C_SELF_ID));
					// 장치암호
					psmt.setString(17, data.get(IConst.AdapterInstance.C_SELF_PW));
					// 비고
					psmt.setString(18, data.get(IConst.AdapterInstance.C_REMARK));

					// 변경일시
					data.put(IConst.AdapterInstance.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(19, data.get(IConst.AdapterInstance.C_ALTER_DATE));
					// 등록일시
					data.put(IConst.AdapterInstance.C_REG_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(20, data.get(IConst.AdapterInstance.C_REG_DATE));

					psmt.executeUpdate();

					resultList.add(data);
				}

				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 인스턴스 속성 등록
	 * 
	 * @param dataList
	 *            데이터리스트
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> insertAttribute(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;

		List<KeyValueEntity<String, String>> conditionList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> data : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();
					KeyValueEntity<String, String> entity1 = new KeyValueEntity<String, String>(
							IConst.InstanceAttribute.C_INS_ID, data.get(IConst.InstanceAttribute.C_INS_ID));
					KeyValueEntity<String, String> entity2 = new KeyValueEntity<String, String>(
							IConst.InstanceAttribute.C_KEY, data.get(IConst.InstanceAttribute.C_KEY));
					conditionList.add(entity1);
					conditionList.add(entity2);
					selectList = selectAttribute(con, conditionList);

					if (selectList.size() > 0) {
						throw new SQLException("PK중복");
					}

					psmt = con.prepareStatement("INSERT INTO T_ADT_INS_ATT VALUES " + "(?, ?, ?, ?, ?, ?, ?, ?);");
					// 인스턴스ID
					psmt.setString(1, data.get(IConst.InstanceAttribute.C_INS_ID));
					// 속성경로
					psmt.setString(2, data.get(IConst.InstanceAttribute.C_KEY));
					// 속성경로명
					psmt.setString(3, data.get(IConst.InstanceAttribute.C_DSCT));
					// 속성경로값
					psmt.setString(4, data.get(IConst.InstanceAttribute.C_VALUE));
					// 속성경로타입
					psmt.setString(5, data.get(IConst.InstanceAttribute.C_VALUE_TYPE));
					// 비고
					psmt.setString(6, data.get(IConst.InstanceAttribute.C_REMARK));
					// 변경일시
					data.put(IConst.InstanceAttribute.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(7, data.get(IConst.InstanceAttribute.C_ALTER_DATE));
					// 등록일시
					data.put(IConst.InstanceAttribute.C_REG_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(8, data.get(IConst.InstanceAttribute.C_REG_DATE));

					psmt.executeUpdate();

					resultList.add(data);
				}

				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 인스턴스 기능 등록
	 * 
	 * @param dataList
	 *            데이터리스트
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> insertFunction(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;

		List<KeyValueEntity<String, String>> conditionList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> data : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();
					KeyValueEntity<String, String> entity1 = new KeyValueEntity<String, String>(
							IConst.InstanceFunction.C_INS_ID, data.get(IConst.InstanceFunction.C_INS_ID));
					KeyValueEntity<String, String> entity2 = new KeyValueEntity<String, String>(
							IConst.InstanceFunction.C_KEY, data.get(IConst.InstanceFunction.C_KEY));
					conditionList.add(entity1);
					conditionList.add(entity2);
					selectList = selectFunction(con, conditionList);

					if (selectList.size() > 0) {
						throw new SQLException("PK중복");
					}

					psmt = con.prepareStatement("INSERT INTO T_ADT_INS_FUNC VALUES "
							+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
					psmt.setString(1, data.get(IConst.InstanceFunction.C_INS_ID));
					psmt.setString(2, data.get(IConst.InstanceFunction.C_KEY));
					psmt.setString(3, data.get(IConst.InstanceFunction.C_DSCT));
					psmt.setString(4, data.get(IConst.InstanceFunction.C_CONT_TYPE));
					psmt.setString(5, data.get(IConst.InstanceFunction.C_PARAM1));
					psmt.setString(6, data.get(IConst.InstanceFunction.C_PARAM2));
					psmt.setString(7, data.get(IConst.InstanceFunction.C_PARAM3));
					psmt.setString(8, data.get(IConst.InstanceFunction.C_PARAM4));
					psmt.setString(9, data.get(IConst.InstanceFunction.C_PARAM5));
					psmt.setString(10, data.get(IConst.InstanceFunction.C_PARAM_TYPE1));
					psmt.setString(11, data.get(IConst.InstanceFunction.C_PARAM_TYPE2));
					psmt.setString(12, data.get(IConst.InstanceFunction.C_PARAM_TYPE3));
					psmt.setString(13, data.get(IConst.InstanceFunction.C_PARAM_TYPE4));
					psmt.setString(14, data.get(IConst.InstanceFunction.C_PARAM_TYPE5));
					psmt.setString(15, data.get(IConst.InstanceFunction.C_REMARK));
					data.put(IConst.InstanceFunction.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(16, data.get(IConst.InstanceFunction.C_ALTER_DATE));
					data.put(IConst.InstanceFunction.C_REG_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(17, data.get(IConst.InstanceFunction.C_REG_DATE));

					psmt.executeUpdate();

					resultList.add(data);
				}

				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 아답터 인스턴스 삭제
	 * 
	 * @param conditionEntity
	 *            조건엔티티
	 * @return 결과리스트
	 * @throws Exception
	 */
	public List<Map<String, String>> deleteIns(List<KeyValueEntity<String, String>> conditionEntity, Map<String, String> config) throws Exception {
		Connection con = null;

		List<Map<String, String>> resultList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				resultList = selectIns(con, conditionEntity, config);

				if (resultList.size() != 1) {
					throw new SQLException("삭제 대상 부정합");
				}

//				//속성 삭제
//				deleteAttributeByIid(con, conditionEntity.get(0).getKey());
//				//기능 삭제
//				deleteFunctionByIid(con, conditionEntity.get(0).getKey());
				
				PreparedStatement psmt = con
						.prepareStatement("DELETE FROM T_ADT_INS WHERE " + conditionEntity.get(0).getKey() + " == ?;");
				psmt.setString(1, conditionEntity.get(0).getValue());

				psmt.executeUpdate();

				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * 아답터 속성 삭제(내부용)
	 * 
	 * @param conditionEntity
	 * @return
	 * @throws Exception
	 */
	private void deleteAttributeByIid(Connection con, String insId) throws Exception {
		try {
			PreparedStatement psmt = con.prepareStatement("DELETE FROM T_ADT_INS_ATT WHERE insId == ?;");
			psmt.setString(1, insId);
			psmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}

		return;
	}

	/**
	 * 아답터 속성 삭제
	 * 
	 * @param conditionEntity
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> deleteAttribute(List<KeyValueEntity<String, String>> conditionEntity)
			throws Exception {
		Connection con = null;

		List<Map<String, String>> resultList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				resultList = selectAttribute(con, conditionEntity);

				if (resultList.size() != 1) {
					throw new Exception("삭제 대상 부정합");
				}

				PreparedStatement psmt = con.prepareStatement("DELETE FROM T_ADT_INS_ATT WHERE "
						+ conditionEntity.get(0).getKey() + " == ? AND " + conditionEntity.get(1).getKey() + " == ?;");

				psmt.setString(1, conditionEntity.get(0).getValue());
				psmt.setString(2, conditionEntity.get(1).getValue());

				psmt.executeUpdate();

				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * 아답터 속성 삭제(내부용)
	 * 
	 * @param conditionEntity
	 * @return
	 * @throws Exception
	 */
	private void deleteFunctionByIid(Connection con, String insId) throws Exception {
		try {
			PreparedStatement psmt = con.prepareStatement("DELETE FROM T_ADT_INS_FUNC WHERE insId == ?;");
			psmt.setString(1, insId);
			psmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}

		return;
	}
	
	/**
	 * 아답터 기능 삭제
	 * 
	 * @param conditionEntity
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> deleteFunction(List<KeyValueEntity<String, String>> conditionEntity)
			throws Exception {
		Connection con = null;

		List<Map<String, String>> resultList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				resultList = selectFunction(con, conditionEntity);

				if (resultList.size() != 1) {
					throw new SQLException("삭제 대상 부정합");
				}

				PreparedStatement psmt = con.prepareStatement("DELETE FROM T_ADT_INS_FUNC WHERE "
						+ conditionEntity.get(0).getKey() + " == ? AND " + conditionEntity.get(1).getKey() + " == ?;");

				psmt.setString(1, conditionEntity.get(0).getValue());
				psmt.setString(2, conditionEntity.get(1).getValue());

				psmt.executeUpdate();

				con.commit();
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return resultList;
	}

	public void selectQuery(String query) throws Exception {
		Connection con = null;

		StringBuffer buf = new StringBuffer();
		synchronized (syncObj) {
			try {
				con = getConnection();
				ResultSet rs = null;
				PreparedStatement psmt = null;

				psmt = con.prepareStatement(query);

				// 쿼리 실행
				rs = psmt.executeQuery();

				while (rs.next()) {
					buf.setLength(0);
					buf.append("[");
					for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
						buf.append(rs.getMetaData().getColumnName(i)).append("=")
								.append(rs.getString(rs.getMetaData().getColumnName(i))).append(",");
					}
					buf.delete(buf.length() - 1, buf.length()).append("]");
					System.out.println(buf.toString());
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}
		return;
	}
}

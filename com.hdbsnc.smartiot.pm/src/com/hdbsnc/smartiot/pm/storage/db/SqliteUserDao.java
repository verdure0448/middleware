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

public class SqliteUserDao {

	final private Object syncObj = new Object();
	private static SqliteUserDao instance;
	private static String connectionString = null;

	static {
		instance = new SqliteUserDao();
		try {
			connectionString = SqliteDBManager.getConnectionString(instance, "UserDbFileName");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 클래스 인스턴스 취득
	 * 
	 * @return 유저DAO
	 * @throws IOException
	 */
	public static SqliteUserDao getInstance() throws IOException {
//		if (instance == null) {
//			instance = new SqliteUserDao();
//			connectionString = SqliteDBManager.getConnectionString(instance, "UserDbFileName");
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
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionString);
	}

	/**
	 * 유저 프로파일 마스터 조회
	 * 
	 * @param con
	 *            DB커넥션
	 * @param conditionEntity
	 *            조건 엔티티
	 * @return 결과리스트
	 * @throws SQLException
	 *             예외
	 */
	private List<Map<String, String>> selectUserPool(Connection con, List<KeyValueEntity<String, String>> conditionList)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> selectList = new ArrayList<Map<String, String>>();
		Map<String, String> selectMap = null;

		StringBuffer query = new StringBuffer();
		if (conditionList == null || conditionList.size() == 0) {
			query.append("SELECT * FROM T_USER_POOL;");
			psmt = con.prepareStatement(query.toString());
		} else {
			query.append("SELECT * FROM T_USER_POOL where ");
			int cnt = 0;
			for (KeyValueEntity<String, String> entity : conditionList) {
				if (cnt > 0) {
					query.append(" AND ");
				}

				query.append(entity.getKey()).append("==?");
				cnt++;
			}
			query.append(";");

			psmt = con.prepareStatement(query.toString());
			cnt = 1;
			for (KeyValueEntity<String, String> entity : conditionList) {
				psmt.setString(cnt, entity.getValue());
				cnt++;
			}
		}

		// 쿼리 실행
		rs = psmt.executeQuery();

		while (rs.next()) {
			selectMap = new HashMap<String, String>();

			// 유저풀ID
			selectMap.put(IConst.UserPool.C_USER_POOL_ID, rs.getString(IConst.UserPool.C_USER_POOL_ID));
			// 유저풀명
			selectMap.put(IConst.UserPool.C_USER_POOL_NAME, rs.getString(IConst.UserPool.C_USER_POOL_NAME));
			// 비고
			selectMap.put(IConst.UserPool.C_REMARK, rs.getString(IConst.UserPool.C_REMARK));
			// 변경일시
			selectMap.put(IConst.UserPool.C_ALTER_DATE, TimeUtil.changeFormat(rs.getString(IConst.UserPool.C_ALTER_DATE)));
			// 등록일시
			selectMap.put(IConst.UserPool.C_REG_DATE, TimeUtil.changeFormat(rs.getString(IConst.UserPool.C_REG_DATE)));

			selectList.add(selectMap);
		}
		return selectList;
	}

	/**
	 * 유저 프로파일 조회
	 * 
	 * @param con
	 *            DB커넥션
	 * @param conditionEntity
	 *            조건 엔티티
	 * @return 결과리스트
	 * @throws SQLException
	 *             예외
	 */
	private List<Map<String, String>> selectUser(Connection con, List<KeyValueEntity<String, String>> conditionList)
			throws Exception {

		ResultSet rs = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> selectList = new ArrayList<Map<String, String>>();
		Map<String, String> selectMap = null;

		StringBuffer query = new StringBuffer("SELECT * FROM T_USER where ");
		int cnt = 0;
		for (KeyValueEntity<String, String> entity : conditionList) {
			if (cnt > 0) {
				query.append(" AND ");
			}

			query.append(entity.getKey()).append("==?");
			cnt++;
		}
		query.append(";");

		psmt = con.prepareStatement(query.toString());
		cnt = 1;
		for (KeyValueEntity<String, String> entity : conditionList) {
			psmt.setString(cnt, entity.getValue());
			cnt++;
		}

		// 쿼리 실행
		rs = psmt.executeQuery();

		while (rs.next()) {
			selectMap = new HashMap<String, String>();

			// 사용자ID
			selectMap.put(IConst.User.C_USER_ID, rs.getString(IConst.User.C_USER_ID));
			// 사용자풀ID
			selectMap.put(IConst.User.C_USER_POOL_ID, rs.getString(IConst.User.C_USER_POOL_ID));

			// 사용자구분
			selectMap.put(IConst.User.C_USER_TYPE, rs.getString(IConst.User.C_USER_TYPE));
			// 사용자명
			selectMap.put(IConst.User.C_USER_NAME, rs.getString(IConst.User.C_USER_NAME));
			// 사용자암호
			selectMap.put(IConst.User.C_USER_PW, rs.getString(IConst.User.C_USER_PW));
			// 회사명
			selectMap.put(IConst.User.C_COMP_NAME, rs.getString(IConst.User.C_COMP_NAME));
			// 부서명
			selectMap.put(IConst.User.C_DEPT_NAME, rs.getString(IConst.User.C_DEPT_NAME));
			// 직책
			selectMap.put(IConst.User.C_TITLE_NAME, rs.getString(IConst.User.C_TITLE_NAME));
			// 비고
			selectMap.put(IConst.User.C_REMARK, rs.getString(IConst.User.C_REMARK));
			// 변경일시
			selectMap.put(IConst.User.C_ALTER_DATE, TimeUtil.changeFormat(rs.getString(IConst.User.C_ALTER_DATE)));
			// 등록일시
			selectMap.put(IConst.User.C_REG_DATE, TimeUtil.changeFormat(rs.getString(IConst.User.C_REG_DATE)));

			selectList.add(selectMap);
		}
		return selectList;
	}

	/**
	 * 유저 프로파일 필터 조회
	 * 
	 * @param con
	 *            DB커넥션
	 * @param conditionEntity
	 *            조건 엔티티
	 * @return 결과리스트
	 * @throws SQLException
	 *             예외
	 */
	private List<Map<String, String>> selectProfileFilter(Connection con,
			List<KeyValueEntity<String, String>> conditionList) throws Exception {
		ResultSet rs = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> selectList = new ArrayList<Map<String, String>>();
		Map<String, String> selectMap = null;

		StringBuffer query = new StringBuffer("SELECT * FROM T_USER_FILTER where ");
		int cnt = 0;
		for (KeyValueEntity<String, String> entity : conditionList) {
			if (cnt > 0) {
				query.append(" AND ");
			}

			query.append(entity.getKey()).append("==?");
			cnt++;
		}
		query.append(";");

		psmt = con.prepareStatement(query.toString());
		cnt = 1;
		for (KeyValueEntity<String, String> entity : conditionList) {
			psmt.setString(cnt, entity.getValue());
			cnt++;
		}

		// 쿼리 실행
		rs = psmt.executeQuery();

		while (rs.next()) {
			selectMap = new HashMap<String, String>();

			// 사용자ID
			selectMap.put(IConst.UserFilter.C_USER_ID, rs.getString(IConst.UserFilter.C_USER_ID));
			// 권한필터
			selectMap.put(IConst.UserFilter.C_AUTH_FILTER, rs.getString(IConst.UserFilter.C_AUTH_FILTER));
			// 비고
			selectMap.put(IConst.UserFilter.C_REMARK, rs.getString(IConst.UserFilter.C_REMARK));
			// 변경일시
			selectMap.put(IConst.UserFilter.C_ALTER_DATE, TimeUtil.changeFormat(rs.getString(IConst.UserFilter.C_ALTER_DATE)));
			// 등록일시
			selectMap.put(IConst.UserFilter.C_REG_DATE, TimeUtil.changeFormat(rs.getString(IConst.UserFilter.C_REG_DATE)));

			selectList.add(selectMap);
		}
		return selectList;
	}

	/**
	 * 유저 프로파일 마스터 조회
	 * 
	 * @param conditionEntity
	 *            조건 엔티티
	 * @return 결과리스트
	 * @throws SQLException
	 */
	public List<Map<String, String>> selectUserPool(List<KeyValueEntity<String, String>> conditionList)
			throws Exception {

		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				selectList = selectUserPool(con, conditionList);
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
		return selectList;
	}

	/**
	 * 유저 프로파일 조회
	 * 
	 * @param conditionEntity
	 *            조건 엔티티
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> selectUser(List<KeyValueEntity<String, String>> conditionList) throws Exception {
		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				selectList = selectUser(con, conditionList);
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
		return selectList;
	}

	/**
	 * 유저 프로파일 필터 조회
	 * 
	 * @param conditionEntity
	 *            조건 엔티티
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> selectUserFilter(List<KeyValueEntity<String, String>> conditionList) throws Exception {
		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				selectList = selectProfileFilter(con, conditionList);
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
		return selectList;
	}

	/**
	 * 유저 프로파일 마스터 변경
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 */
	public List<Map<String, String>> updateUserPool(List<Map<String, String>> dataList) throws Exception{
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;
		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> conditionEntity = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> dataMap : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();
					conditionEntity = new KeyValueEntity<String, String>(IConst.UserPool.C_USER_POOL_ID,
							dataMap.get(IConst.UserPool.C_USER_POOL_ID));
					conditionList.add(conditionEntity);

					// 대상데이터 존재 유무 체크
					selectList = selectUserPool(con, conditionList);

					if (selectList.size() != 1) {
						throw new SQLException("변경 대상 부정합");
					}

					// Update 실시
					psmt = con.prepareStatement("UPDATE T_USER_POOL SET " + "userPoolNm=?, remark=?, alterDate=? "
							+ "where userPoolId == ?;");

					// 사용자풀명
					psmt.setString(1, dataMap.get(IConst.UserPool.C_USER_POOL_NAME));

					// 비고
					psmt.setString(2, dataMap.get(IConst.UserPool.C_REMARK));

					// 변경일시
					dataMap.put(IConst.UserPool.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(3, dataMap.get(IConst.UserPool.C_ALTER_DATE));

					// 사용자풀ID(PK)
					psmt.setString(4, dataMap.get(IConst.UserPool.C_USER_POOL_ID));

					// Query 발행
					psmt.executeUpdate();

					// 등록일시를 DB값으로 설정
					dataMap.put(IConst.UserPool.C_REG_DATE, selectList.get(0).get(IConst.UserPool.C_REG_DATE));

					// 결과리스트에 데이터Map 설정
					resultList.add(dataMap);
				}

				// commit
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
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
	 * 유저 프로파일 변경
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> updateUser(List<Map<String, String>> dataList) throws Exception {

		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;
		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> conditionEntity = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> dataMap : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();
					conditionEntity = new KeyValueEntity<String, String>(IConst.User.C_USER_ID,
							dataMap.get(IConst.User.C_USER_ID));
					conditionList.add(conditionEntity);

					// 대상데이터 존재 유무 체크
					selectList = selectUser(con, conditionList);
					if (selectList.size() != 1) {
						throw new SQLException("변경 대상 부정합");
					}

					// Update 실시
					psmt = con.prepareStatement("UPDATE T_USER SET "
							+ "userPoolId=?, userPw=?, userType=?, userNm=?, compNm=?, deptNm=?, titleNm=?, remark=?, alterDate=? "
							+ "where userId == ?;");

					// 사용자풀ID
					psmt.setString(1, dataMap.get(IConst.User.C_USER_POOL_ID));

					// 사용자암호
					psmt.setString(2, dataMap.get(IConst.User.C_USER_PW));
					
					// 사용자구분
					psmt.setString(3, dataMap.get(IConst.User.C_USER_TYPE));

					// 사용자명
					psmt.setString(4, dataMap.get(IConst.User.C_USER_NAME));

					// 회사명
					psmt.setString(5, dataMap.get(IConst.User.C_COMP_NAME));

					// 부서명
					psmt.setString(6, dataMap.get(IConst.User.C_DEPT_NAME));

					// 직책
					psmt.setString(7, dataMap.get(IConst.User.C_TITLE_NAME));

					// 비고
					psmt.setString(8, dataMap.get(IConst.User.C_REMARK));

					// 변경일시
					dataMap.put(IConst.User.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(9, dataMap.get(IConst.User.C_ALTER_DATE));

					// 사용자ID(PK)
					psmt.setString(10, dataMap.get(IConst.User.C_USER_ID));

					// Query 발행
					psmt.executeUpdate();

					// 등록일시를 DB값으로 설정
					dataMap.put(IConst.User.C_REG_DATE, selectList.get(0).get(IConst.User.C_REG_DATE));

					// 결과리스트에 데이터Map 설정
					resultList.add(dataMap);
				}

				// commit
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
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
	 * 유저 프로파일 필터 변경
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> updateUserFilter(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;
		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> conditionEntity1 = null;
		KeyValueEntity<String, String> conditionEntity2 = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> dataMap : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();
					conditionEntity1 = new KeyValueEntity<String, String>(IConst.UserFilter.C_USER_ID,
							dataMap.get(IConst.UserFilter.C_USER_ID));
					conditionEntity2 = new KeyValueEntity<String, String>(IConst.UserFilter.C_AUTH_FILTER,
							dataMap.get(IConst.UserFilter.C_AUTH_FILTER));
					conditionList.add(conditionEntity1);
					conditionList.add(conditionEntity2);

					// 대상데이터 존재 유무 체크
					selectList = selectProfileFilter(con, conditionList);
					if (selectList.size() != 1) {
						throw new SQLException("변경 대상 부정합");
					}

					// Update 실시
					psmt = con.prepareStatement("UPDATE T_USER_FILTER SET " + "remark = ?, alterDate = ? "
							+ "where userId == ? AND authFilter == ?;");

					// 비고
					psmt.setString(1, dataMap.get(IConst.UserFilter.C_REMARK));

					// 변경일시
					dataMap.put(IConst.UserFilter.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(2, dataMap.get(IConst.UserFilter.C_ALTER_DATE));

					// 사용자ID(PK)
					psmt.setString(3, dataMap.get(IConst.UserFilter.C_USER_ID));

					// 권한필터(PK)
					psmt.setString(4, dataMap.get(IConst.UserFilter.C_AUTH_FILTER));

					// Query 발행
					psmt.executeUpdate();

					// 등록일시를 DB값으로 설정
					dataMap.put(IConst.UserFilter.C_REG_DATE, selectList.get(0).get(IConst.User.C_REG_DATE));

					// 결과리스트에 데이터Map 설정
					resultList.add(dataMap);
				}

				// commit
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
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
	 * 유저 프로파일 마스터 등록
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> insertUserPool(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;

		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> conditionEntity = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> dataMap : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();

					conditionEntity = new KeyValueEntity<String, String>(IConst.UserPool.C_USER_POOL_ID,
							dataMap.get(IConst.UserPool.C_USER_POOL_ID));

					conditionList.add(conditionEntity);

					// 중복 체크
					selectList = selectUserPool(con, conditionList);
					if (selectList.size() > 0) {
						throw new SQLException("등록 대상 중복");
					}

					// 등록 실시
					psmt = con.prepareStatement("INSERT INTO T_USER_POOL VALUES (?, ?, ?, ?, ?);");

					// 사용자풀ID
					psmt.setString(1, dataMap.get(IConst.UserPool.C_USER_POOL_ID));
					// 사용자풀명
					psmt.setString(2, dataMap.get(IConst.UserPool.C_USER_POOL_NAME));
					// 비고
					psmt.setString(3, dataMap.get(IConst.UserPool.C_REMARK));
					// 변경일시
					dataMap.put(IConst.UserPool.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(4, dataMap.get(IConst.UserPool.C_ALTER_DATE));
					// 등록일시
					dataMap.put(IConst.UserPool.C_REG_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(5, dataMap.get(IConst.UserPool.C_REG_DATE));

					// Query 발행
					psmt.executeUpdate();

					// 결과리스트에 데이터Map 설정
					resultList.add(dataMap);
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
	 * 유저 프로파일 등록
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> insertUser(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;

		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> conditionEntity = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> dataMap : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();

					conditionEntity = new KeyValueEntity<String, String>(IConst.User.C_USER_ID,
							dataMap.get(IConst.User.C_USER_ID));

					conditionList.add(conditionEntity);

					// 중복 체크
					selectList = selectUser(con, conditionList);
					if (selectList.size() > 0) {
						throw new SQLException("등록 대상 중복");
					}

					// 등록 실시
					psmt = con.prepareStatement("INSERT INTO T_USER VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

					// 사용자ID
					psmt.setString(1, dataMap.get(IConst.User.C_USER_ID));

					// 사용자풀ID
					psmt.setString(2, dataMap.get(IConst.User.C_USER_POOL_ID));

					// 사용자암호
					psmt.setString(3, dataMap.get(IConst.User.C_USER_PW));
					
					// 사용자구분
					psmt.setString(4, dataMap.get(IConst.User.C_USER_TYPE));

					// 사용자명
					psmt.setString(5, dataMap.get(IConst.User.C_USER_NAME));

					// 회사명
					psmt.setString(6, dataMap.get(IConst.User.C_COMP_NAME));

					// 부서명
					psmt.setString(7, dataMap.get(IConst.User.C_DEPT_NAME));

					// 직책
					psmt.setString(8, dataMap.get(IConst.User.C_TITLE_NAME));

					// 비고
					psmt.setString(9, dataMap.get(IConst.User.C_REMARK));

					// 변경일시
					dataMap.put(IConst.User.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(10, dataMap.get(IConst.User.C_ALTER_DATE));

					// 등록일시
					dataMap.put(IConst.User.C_REG_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(11, dataMap.get(IConst.User.C_REG_DATE));

					// Query 발행
					psmt.executeUpdate();

					// 결과리스트에 데이터Map 설정
					resultList.add(dataMap);
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
	 * 유저 프로파일 필터 등록
	 * 
	 * @param dataList
	 *            데이터 리스트
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> insertUserFilter(List<Map<String, String>> dataList) throws Exception {
		Connection con = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> selectList = null;

		List<KeyValueEntity<String, String>> conditionList = null;
		KeyValueEntity<String, String> conditionEntity1 = null;
		KeyValueEntity<String, String> conditionEntity2 = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				for (Map<String, String> dataMap : dataList) {
					conditionList = new ArrayList<KeyValueEntity<String, String>>();

					conditionEntity1 = new KeyValueEntity<String, String>(IConst.UserFilter.C_USER_ID,
							dataMap.get(IConst.UserFilter.C_USER_ID));
					conditionEntity2 = new KeyValueEntity<String, String>(IConst.UserFilter.C_AUTH_FILTER,
							dataMap.get(IConst.UserFilter.C_AUTH_FILTER));

					conditionList.add(conditionEntity1);
					conditionList.add(conditionEntity2);

					// 중복 체크
					selectList = selectProfileFilter(con, conditionList);
					if (selectList.size() > 0) {
						throw new SQLException("등록 대상 중복");
					}

					// 등록 실시
					psmt = con.prepareStatement("INSERT INTO T_USER_FILTER VALUES (?, ?, ?, ?, ?);");

					// 사용자ID
					psmt.setString(1, dataMap.get(IConst.UserFilter.C_USER_ID));

					// 권한필터
					psmt.setString(2, dataMap.get(IConst.UserFilter.C_AUTH_FILTER));

					// 비고
					psmt.setString(3, dataMap.get(IConst.UserFilter.C_REMARK));

					// 변경일시
					dataMap.put(IConst.UserFilter.C_ALTER_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(4, dataMap.get(IConst.UserFilter.C_ALTER_DATE));

					// 등록일시
					dataMap.put(IConst.UserFilter.C_REG_DATE, TimeUtil.getYYYYMMddHHss());
					psmt.setString(5, dataMap.get(IConst.UserFilter.C_REG_DATE));

					// Query 발행
					psmt.executeUpdate();

					// 결과리스트에 데이터Map 설정
					resultList.add(dataMap);
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
	 * 유저 프로파일 마스터 삭제
	 * 
	 * @param conditionEntity
	 *            조건엔티티
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> deleteUserPool(List<KeyValueEntity<String, String>> conditionList) throws Exception {

		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				// 삭제 대상 유무 체크
				selectList = selectUserPool(con, conditionList);
				if (selectList.size() != 1) {
					throw new SQLException("삭제 대상 부정합");
				}

				// 삭제 실시
				PreparedStatement psmt = con
						.prepareStatement("DELETE FROM T_USER_POOL WHERE " + conditionList.get(0).getKey() + " == ?;");
				psmt.setString(1, conditionList.get(0).getValue());

				// Query 발행
				psmt.executeUpdate();

				// commit
				con.commit();

			} catch (Exception e) {
				e.printStackTrace();
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

		return selectList;
	}

	/**
	 * 유저 프로파일 삭제
	 * 
	 * @param conditionEntity
	 *            조건엔티티
	 * @return 결과리스트
	 * @throws Exception 
	 */
	public List<Map<String, String>> deleteUser(List<KeyValueEntity<String, String>> conditionList) throws Exception {
		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				// 삭제 대상 유무 체크
				selectList = selectUser(con, conditionList);
				if (selectList.size() != 1) {
					throw new SQLException("삭제 대상 부정합");
				}

//				// 유저필터 삭제
//				deleteUserFilter(con, conditionList.get(0).getValue());
				
				// 삭제 실시
				PreparedStatement psmt = con
						.prepareStatement("DELETE FROM T_USER WHERE " + conditionList.get(0).getKey() + " == ?;");
				psmt.setString(1, conditionList.get(0).getValue());

				// Query 발행
				psmt.executeUpdate();

				// commit
				con.commit();

			} catch (Exception e) {
				e.printStackTrace();
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

		return selectList;
	}

	/**
	 * 유저ID로 유저필터 삭제
	 * 
	 * @param userId
	 * @throws Exception
	 */
	private void deleteUserFilter(Connection con, String userId) throws Exception {

		try {
			PreparedStatement psmt = con.prepareStatement("DELETE FROM T_USER_FILTER WHERE userId == ?;");
			psmt.setString(1, userId);
			// Query 발행
			psmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 유저 프로파일 필터 삭제
	 * 
	 * @param conditionEntity
	 *            조건엔티티
	 * @return 결과리스트
	 * @throws SQLException 
	 */
	public List<Map<String, String>> deleteUserFilter(List<KeyValueEntity<String, String>> conditionList) throws Exception {
		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				// 삭제 대상 유무 체크
//				selectList = selectUser(con, conditionList);
//				if (selectList.size() != 1) {
//					throw new SQLException("삭제 대상 부정합");
//				}

				// 삭제 실시
				PreparedStatement psmt = con.prepareStatement("DELETE FROM T_USER_FILTER WHERE "
						+ conditionList.get(0).getKey() + " == ? AND " + conditionList.get(1).getKey() + " == ?;");
				psmt.setString(1, conditionList.get(0).getValue());
				psmt.setString(2, conditionList.get(1).getValue());

				// Query 발행
				psmt.executeUpdate();

				// commit
				con.commit();

			} catch (Exception e) {
				e.printStackTrace();
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

		return selectList;
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

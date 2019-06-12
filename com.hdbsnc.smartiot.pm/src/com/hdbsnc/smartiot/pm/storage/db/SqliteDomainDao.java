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

public class SqliteDomainDao {

	final private Object syncObj = new Object();
	private static SqliteDomainDao instance;
	private static String connectionString = null;

	
	static {
		instance = new SqliteDomainDao();
		try {
			connectionString = SqliteDBManager.getConnectionString(instance,
					"DomainDbFileName");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 클래스 인스턴스 취득
	 * 
	 * @return 도메인DAO
	 * @throws IOException 
	 */
	public static SqliteDomainDao getInstance() throws IOException {
//		if (instance == null) {
//			instance = new SqliteDomainDao();
//			connectionString = SqliteDBManager.getConnectionString(instance,
//					"DomainDbFileName");
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
	 * 도메인 조회
	 * 
	 * @param con
	 *            DB커넥션
	 * @param conditionList
	 *            조건리스트
	 * @return 결과리스트
	 * @throws SQLException
	 *             예외
	 */
	private List<Map<String, String>> selectDomain(Connection con,
			List<KeyValueEntity<String, String>> conditionList)
			throws Exception {
		ResultSet rs = null;
		PreparedStatement psmt = null;

		List<Map<String, String>> selectList = new ArrayList<Map<String, String>>();
		Map<String, String> selectMap = null;

		StringBuffer query = new StringBuffer(
				"SELECT * FROM T_DOMAIN_ID_MAST where ");
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

		// Query 실행
		rs = psmt.executeQuery();

		while (rs.next()) {
			selectMap = new HashMap<String, String>();
			// 도메인ID
			selectMap.put(IConst.DomainIdMast.C_DOMAIN_ID,
					rs.getString(IConst.DomainIdMast.C_DOMAIN_ID));
			// 도메인명
			selectMap.put(IConst.DomainIdMast.C_DOMAIN_NAME,
					rs.getString(IConst.DomainIdMast.C_DOMAIN_NAME));
			// 도메인구분
			selectMap.put(IConst.DomainIdMast.C_DOMAIN_TYPE,
					rs.getString(IConst.DomainIdMast.C_DOMAIN_TYPE));
			// 비고
			selectMap.put(IConst.DomainIdMast.C_REMARK,
					rs.getString(IConst.DomainIdMast.C_REMARK));
			// 변경일시
			selectMap.put(IConst.DomainIdMast.C_ALTER_DATE,
					TimeUtil.changeFormat(rs.getString(IConst.DomainIdMast.C_ALTER_DATE)));
			// 등록일시
			selectMap.put(IConst.DomainIdMast.C_REG_DATE,
					TimeUtil.changeFormat(rs.getString(IConst.DomainIdMast.C_REG_DATE)));
			selectList.add(selectMap);
		}
		return selectList;
	}

	/**
	 * 도메인 조회
	 * 
	 * @param conditionList
	 *            조건리스트
	 * @return 결과리스트
	 * @throws Exception 
	 */
	public List<Map<String, String>> selectDomain(
			List<KeyValueEntity<String, String>> conditionList) throws Exception {
		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				selectList = selectDomain(con, conditionList);
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
	 * 도메인 변경
	 * 
	 * @param dataList
	 *            데이터리스트
	 * @return 결과리스트
	 * @throws Exception 
	 */
	public List<Map<String, String>> updateDomain(
			List<Map<String, String>> dataList) throws Exception {
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
					conditionEntity = new KeyValueEntity<String, String>(
							IConst.DomainIdMast.C_DOMAIN_ID,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_ID));
					conditionList.add(conditionEntity);

					// 대상데이터 존재 유무 체크
					selectList = selectDomain(con, conditionList);

					if (selectList.size() != 1) {
						throw new SQLException("변경 대상 부정합");
					}

					// Update 실시
					psmt = con
							.prepareStatement("UPDATE T_DOMAIN_ID_MAST SET "
									+ "domainNm=?, domainType=?, remark=?, alterDate=? "
									+ "where domainId == ?;");

					// 도메인명
					psmt.setString(1,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_NAME));

					// 도메인구분
					psmt.setString(2,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_TYPE));

					// 비고
					psmt.setString(3, dataMap.get(IConst.DomainIdMast.C_REMARK));

					// 변경일시
					dataMap.put(IConst.DomainIdMast.C_ALTER_DATE,
							TimeUtil.getYYYYMMddHHss());
					psmt.setString(4,
							dataMap.get(IConst.DomainIdMast.C_ALTER_DATE));

					// 도메인ID
					psmt.setString(5,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_ID));

					// Query 발행
					psmt.executeUpdate();

					// 등록일시를 DB값으로 설정
					dataMap.put(
							IConst.DomainIdMast.C_REG_DATE,
							selectList.get(0).get(
									IConst.DomainIdMast.C_REG_DATE));

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
	 * 도메인 등록
	 * 
	 * @param dataList
	 *            데이터리스트
	 * @return 결과리스트
	 * @throws Exception 
	 */
	public List<Map<String, String>> insertDomain(
			List<Map<String, String>> dataList) throws Exception {
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

					conditionEntity = new KeyValueEntity<String, String>(
							IConst.DomainIdMast.C_DOMAIN_ID,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_ID));

					conditionList.add(conditionEntity);

					// 중복 체크
					selectList = selectDomain(con, conditionList);
					if (selectList.size() > 0) {
						throw new SQLException("등록 대상 중복");
					}

					// 등록 실시
					psmt = con
							.prepareStatement("INSERT INTO T_DOMAIN_ID_MAST VALUES (?, ?, ?, ?, ?, ?);");

					// 도메인ID
					psmt.setString(1,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_ID));
					// 도메인명
					psmt.setString(2,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_NAME));
					// 도메인구분
					psmt.setString(3,
							dataMap.get(IConst.DomainIdMast.C_DOMAIN_TYPE));
					// 비고
					psmt.setString(4, dataMap.get(IConst.DomainIdMast.C_REMARK));
					// 변경일시
					dataMap.put(IConst.DomainIdMast.C_ALTER_DATE,
							TimeUtil.getYYYYMMddHHss());
					psmt.setString(5,
							dataMap.get(IConst.DomainIdMast.C_ALTER_DATE));
					// 등록일시
					dataMap.put(IConst.DomainIdMast.C_REG_DATE,
							TimeUtil.getYYYYMMddHHss());
					psmt.setString(6,
							dataMap.get(IConst.DomainIdMast.C_REG_DATE));

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
	 * 도메인삭제
	 * 
	 * @param conditionList
	 *            조건리스트
	 * @return 결과리스트
	 * @throws Exception 
	 */
	public List<Map<String, String>> deleteDomain(
			List<KeyValueEntity<String, String>> conditionList) throws Exception {
		Connection con = null;
		List<Map<String, String>> selectList = null;

		synchronized (syncObj) {
			try {
				con = getConnection();
				con.setAutoCommit(false);

				// 삭제 대상 유무 체크
				selectList = selectDomain(con, conditionList);
				if (selectList.size() != 1) {
					throw new SQLException("삭제 대상 부정합");
				}

				// 삭제 실시
				PreparedStatement psmt = con
						.prepareStatement("DELETE FROM T_DOMAIN_ID_MAST WHERE "
								+ conditionList.get(0).getKey() + " == ?;");
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
					for(int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++){
						buf.append(rs.getMetaData().getColumnName(i)).append("=").append(rs.getString(rs.getMetaData().getColumnName(i))).append(",");
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

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

public class SqliteMsgMastDao {

	final private Object syncObj = new Object();
	private static SqliteMsgMastDao instance;
	private static String connectionString = null;

	static {
		instance = new SqliteMsgMastDao();
		try {
			connectionString = SqliteDBManager.getConnectionString(instance, "MsgMastDbFileName");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 클래스 인스턴스 취득
	 * 
	 * @return 메세지 DAO
	 * @throws IOException
	 */
	public static SqliteMsgMastDao getInstance() throws IOException {
//		if (instance == null) {
//			instance = new SqliteMsgMastDao();
//			connectionString = SqliteDBManager.getConnectionString(instance, "MsgMastDbFileName");
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

	public List<Map<String, String>> selectMsgMast(List<KeyValueEntity<String, String>> conditionList) throws Exception {

		ResultSet rs = null;
		PreparedStatement psmt = null;
		Map<String, String> resultMap = null;
		Connection con = null;
		StringBuffer query = new StringBuffer("SELECT * FROM T_MSG_MAST where ");
		List<Map<String, String>> resultList =  new ArrayList<Map<String, String>>();
		synchronized (syncObj) {
			try {
				con = getConnection();
				
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
				
//				psmt = con.prepareStatement(query.toString());
//				psmt.setString(1, conditionList.get(0).getKey());
//				psmt.setString(2, conditionList.get(0).getValue());

				// 쿼리 실행
				rs = psmt.executeQuery();

				while (rs.next()) {
					resultMap = new HashMap<String, String>();
					resultMap.put(IConst.MsgMast.C_INNER_CODE, rs.getString(IConst.MsgMast.C_INNER_CODE));
					resultMap.put(IConst.MsgMast.C_OUTER_CODE, rs.getString(IConst.MsgMast.C_OUTER_CODE));
					resultMap.put(IConst.MsgMast.C_TYPE, rs.getString(IConst.MsgMast.C_TYPE));
					resultMap.put(IConst.MsgMast.C_MSG, rs.getString(IConst.MsgMast.C_MSG));
					resultMap.put(IConst.MsgMast.C_CAUSE_CONTEXT, rs.getString(IConst.MsgMast.C_CAUSE_CONTEXT));
					resultMap.put(IConst.MsgMast.C_SOLUTION_CONTEXT, rs.getString(IConst.MsgMast.C_SOLUTION_CONTEXT));
					resultList.add(resultMap);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
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
}

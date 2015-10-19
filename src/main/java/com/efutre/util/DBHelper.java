package com.efutre.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.efutre.config.DBConfig;


public class DBHelper {
	/**
	 * @param cf
	 * @return 返回数据库连接
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	static public Connection getConn(DBConfig cf) throws SQLException, ClassNotFoundException{
		Class.forName(cf.getDriver());
		Connection conn = DriverManager.getConnection(cf.getConnURL(),cf.getUsername(), cf.getPassword());
		return conn;
	}
	
	/**
	 * 关闭数据库连接
	 * @param conn
	 */
	static public void close(Connection conn){
		try {
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

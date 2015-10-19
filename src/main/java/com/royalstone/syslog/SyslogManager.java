package com.royalstone.syslog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlUtil;


/**
 * 此模块用于向操作日志(syslog)添加记录.
 * @author meng
 *
 */
public class SyslogManager
{

	/**
	 * Constructor
	 * @param conn
	 */
	public SyslogManager( Connection conn )
	{
		this.conn = conn;
	}
	

	/**
	 * @param token
	 * @param moduleid
	 * @param modulename
	 * @param client
	 * @param note
	 * @throws SQLException
	 */
	public void addInfo( Token token, int moduleid, String modulename, String client, String note ) throws SQLException
	{
		addLog( token, moduleid, modulename, INFO, 0, client, note );
	}

	/**
	 * @param token
	 * @param moduleid
	 * @param modulename
	 * @param client
	 * @param note
	 * @param errcode
	 * @throws SQLException
	 */
	public void addErr( Token token, int moduleid, String modulename, String client, String note, int errcode ) throws SQLException
	{
		addLog( token, moduleid, modulename, ERROR, errcode, client, note );
	}

	/**
	 * @param token
	 * @param moduleid
	 * @param modulename
	 * @param client
	 * @param note
	 * @param errcode
	 * @throws SQLException
	 */
	public void addWarning( Token token, int moduleid, String modulename, String client, String note, int errcode ) throws SQLException
	{
		addLog( token, moduleid, modulename, WARNING, errcode, client, note );
	}
	
	/**
	 * 向数据库中添加一条日志记录
	 * @param token		操作员的安全令牌
	 * @param moduleid	模块号
	 * @param level		事件级别: INFO, WARNING, ERROR.
	 * @param client	客户端地址
	 * @param note		事件说明
	 * @throws SQLException 
	 */
	public void addLog( Token token, int moduleid, String modulename, 
			int level, int errcode, String client, String note ) throws SQLException
	{
		String sql = "INSERT INTO syslog ( logid,userid, username, loginid, moduleid, modulename, " +
				" userlevel, errcode, client, note ) " +
				" VALUES ( syslog_id.NEXTVAL,?,?,?,?,?, ?,?,?,? )";
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		pstmt.setInt(1, token.userid);
		pstmt.setString(2, SqlUtil.toLocal( token.username) );
		pstmt.setString(3, token.loginid);
		pstmt.setInt(4, moduleid);
		pstmt.setString(5, SqlUtil.toLocal( modulename) );
		
		pstmt.setInt(6, level);
		pstmt.setInt(7, errcode);
		pstmt.setString(8, client);
		pstmt.setString(9, SqlUtil.toLocal( note ) );
		pstmt.executeUpdate();
	}
	
	
	/**
	 * 
	 */
	public static final int INFO 		= 100;
	/**
	 * 
	 */
	public static final int WARNING 	= 600;
	/**
	 * 
	 */
	public static final int ERROR 		= 900;

	static final int ROWS_LIMIT = 10000;

	/**
	 * 
	 */
	final private Connection conn ;
	
}

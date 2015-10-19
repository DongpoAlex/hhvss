/*
 * Created on 2005-10-13
 *
 */
package com.royalstone.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * 用于权限控制. 根据指定的用户编号和模块编号查询权限数据.
 * @author meng
 */
public class PermissionManager {

	
	public PermissionManager( Connection conn )
	{
		this.conn = conn;
	}

	/** 此方法用于查询环境变量.
	 * @param userid
	 * @param moduleid
	 * @param env_name
	 * @return
	 * @throws SQLException
	 */
	public String[] getEnv( int userid, int moduleid, String env_name ) throws SQLException
	{
		String sql = " SELECT DISTINCT trim(i.datavalue) "
			+ " FROM user_role u JOIN role_environment i ON ( i.roleid = u.roleid ) "
			+ " WHERE u.userid = ? AND i.dataFlag=? ";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt( 1, userid );
		pstmt.setString( 2, env_name );
		ResultSet rs = pstmt.executeQuery();
		Vector<String> vector = new Vector<String>(0);
		
		while( rs.next() ) {
			String s = rs.getString(1);
			if( s == null ) s = "";
			s = s.trim();
			vector.add( rs.getString(1) );
		}
		rs.close();
		pstmt.close();
		String[] values = new String[ vector.size() ];
		for( int i=0; i<vector.size(); i++ )values[i] = (String) vector.get(i);

		return values;
	}
	
	public String[] getEnv( int userid, String env_name ) throws SQLException
	{
		String sql = " SELECT DISTINCT trim(i.datavalue) "
			+ " FROM user_role u JOIN role_environment i ON ( i.roleid = u.roleid ) "
			+ " WHERE u.userid = ? AND i.dataFlag=? ";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt( 1, userid );
		pstmt.setString( 2, env_name );
		ResultSet rs = pstmt.executeQuery();
		Vector<String> vector = new Vector<String>(0);
		
		while( rs.next() ) {
			String s = rs.getString(1);
			if( s == null ) s = "";
			s = s.trim();
			vector.add( rs.getString(1) );
		}
		rs.close();
		pstmt.close();
		String[] values = new String[ vector.size() ];
		for( int i=0; i<vector.size(); i++ )values[i] = (String) vector.get(i);

		return values;
	}
	
	public boolean isValidValue( int userid, int moduleid, String env_name, String env_value ) throws SQLException 
	{
		String sql = " SELECT count(*) rows "
			+ " FROM user_role u JOIN role_module_item i ON ( i.roleid = u.roleid ) "
			+ " WHERE u.userid = ? AND i.moduleid=? AND i.dataFlag=? AND i.dataValue=? ";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt( 1, userid );
		pstmt.setInt( 2, moduleid );
		pstmt.setString( 3, env_name );
		pstmt.setString( 4, env_value );
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int rows = rs.getInt(1);
		
		rs.close();
		pstmt.close();

		return ( rows>0 );
		
	}
	
	public Permission getPermission( int userid, int moduleid ) throws SQLException
	{
		int perm = 0;
		String sql = " SELECT DISTINCT i.rightid "
			+ " FROM user_role u JOIN role_module i ON ( i.roleid = u.roleid ) "
			+ " WHERE u.userid = ? AND i.moduleid = ? " ;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, userid);
		pstmt.setInt(2, moduleid);
		ResultSet rs = pstmt.executeQuery();
		while( rs.next() ){
			int rightid = rs.getInt(1);
			perm |= rightid;
		}
		rs.close();
		pstmt.close();
		return new Permission(perm);
	}
	
	public boolean checkPermission(int userid, int moduleid, int action) throws SQLException{
		String sql =" select m.rightid from user_list l " +
				" inner join user_role r on(r.userid=l.userid) " +
				" inner join role_module m on (m.roleid=r.roleid) " +
				" where l.userid=? and moduleid=? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, userid);
		pstmt.setInt(2, moduleid);
		ResultSet rs = pstmt.executeQuery();
		int rightid=0;
		if(rs.next()) rightid=rs.getInt(1);
		rs.close();
		pstmt.close();
		
		return checkValue(action,rightid);
	}
	private boolean checkValue(int small, int big) throws SQLException{
		Permission p  = new Permission(big);
		return p.include(small);
	}
	
	
	final private Connection conn;
}


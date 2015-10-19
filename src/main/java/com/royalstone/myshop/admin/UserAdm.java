/*
 * Created on 2005-12-14
 *
 */
package com.royalstone.myshop.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.jdom.Element;

import com.jivesoftware.util.StringUtils;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块负责维护FAS系统中的用户帐户.
 * 目前提供以下功能: 添加, 修改(名称,机构,密码), 锁定. 为了避免误操作导致数据不一致, 暂不提供删除功能.
 * @author meng
 *
 */
public class UserAdm {

	public UserAdm( Connection conn )
	{
		this.conn = conn;
	}
	
	public Element addUser( int userid, String username, String loginid, String shopid, String password ) throws SQLException
	{
		int id = ( userid>=0 )? userid : ( getMaxUserid()+1 );
		String encryptPass = StringUtils.hash ( password );
		
		String sql = " INSERT INTO user_list( userid, loginid, shopid, username, password ) "
			+ " VALUES( ?, ?, ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt( 1, id );
		pstmt.setString( 2, SqlUtil.toLocal( loginid ) );
		pstmt.setString( 3, SqlUtil.toLocal( shopid ) );
		pstmt.setString( 4, SqlUtil.toLocal( username ) );
		pstmt.setString( 5, encryptPass );
		pstmt.executeUpdate();
		pstmt.close();
		return getUserInfo( id );
	}
	
	public int getUserid( String logindid ) throws SQLException
	{
		String sql = " SELECT userid FROM user_list WHERE loginid = ? " ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, logindid );
		ResultSet rs = pstmt.executeQuery();
		if( !rs.next() ) throw new SQLException( "没有这个用户:" + logindid, "", 100 );
		int id = rs.getInt(1);
		rs.close();
		pstmt.close();
		return id;
	}
	
	public Element getUserInfo( int userid ) throws SQLException
	{
		String sql = " SELECT u.userid, u.loginid, u.username, u.shopid, u.userstatus status, u.menuroot, "
			+ " b.shopname "
			+ " FROM user_list u JOIN shop b ON (b.shopid=u.shopid) " 
			+ " WHERE u.userid = ? " ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt( 1, userid );
		ResultSet rs = pstmt.executeQuery();
		if( !rs.next() ) throw new SQLException( "没有这个用户:" + userid, "", 100 );
		Element elm = new Element( "user" );
		
		String loginid 	= rs.getString( "loginid" );
		String username = rs.getString( "username" );
		String shopid 	= rs.getString( "shopid" );
		String shopname = rs.getString( "shopname" );
		String status 	= rs.getString( "status" );
		String menuroot = rs.getString( "menuroot" );
		
		loginid 	= ( loginid == null )? 	"" : SqlUtil.fromLocal( loginid ).trim();
		username 	= ( username == null )? "" : SqlUtil.fromLocal( username ).trim();
		shopid 		= ( shopid == null )? 	"" : SqlUtil.fromLocal( shopid ).trim();
		shopname 	= ( shopname == null )? "" : SqlUtil.fromLocal( shopname ).trim();
		
		elm.addContent( (new Element( "userid" )).addContent( ""+userid ) );
		elm.addContent( (new Element( "loginid" )).addContent(loginid) );
		elm.addContent( (new Element( "username" )).addContent(username) );
		elm.addContent( (new Element( "shopid" )).addContent(shopid) );
		elm.addContent( (new Element( "shopname" )).addContent(shopname) );
		elm.addContent( (new Element( "status" )).addContent(status) );
		elm.addContent( (new Element( "menuroot" )).addContent(menuroot) );

		pstmt.close();
		return elm;
	}
	
	public void updateUser( int userid, String username, String loginid, String shopid ) throws SQLException
	{
		String sql = " UPDATE user_list SET username = ?, loginid=?, shopid=? WHERE userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, SqlUtil.toLocal(username) );
		pstmt.setString( 2, SqlUtil.toLocal(loginid) );
		pstmt.setString( 3, SqlUtil.toLocal(shopid) );
		pstmt.executeUpdate();
		pstmt.close();
	}
		
	public void updateUser( int userid, String username, String loginid, String shopid, int menuroot ) throws SQLException
	{
		String sql = " UPDATE user_list SET username = ?, loginid=?, shopid=?, menuroot=? WHERE userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, SqlUtil.toLocal(username) );
		pstmt.setString( 2, SqlUtil.toLocal(loginid) );
		pstmt.setString( 3, SqlUtil.toLocal(shopid) );
		pstmt.setInt( 4, menuroot );
		pstmt.executeUpdate();
		pstmt.close();
	}
		
	public void setUserStatus( int userid, int status ) throws SQLException
	{
		String sql = " UPDATE user_list SET userstatus = ? WHERE userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt( 1, status );
		pstmt.executeUpdate();
		pstmt.close();
		
	}
	
	public boolean checkPassword ( int userid, String pass ) throws SQLException
	{
		if( pass==null ) pass = "";
		String encryptPass = StringUtils.hash ( pass );

		boolean ok = false;
		String sql = " SELECT userid, userclass, userstatus, username, password, shopid "
			+ " FROM user_list WHERE userid = ? ";
		PreparedStatement pstmt = conn.prepareStatement( sql ); 
		pstmt.setInt(1, userid );
		ResultSet rs = pstmt.executeQuery();
		if( rs.next() ) {
			String password = rs.getString( "password" );
			password = (password==null) ? "" : password.trim();
			ok = password.equals( encryptPass );
			
		}
		
		rs.close();
		pstmt.close();
		return ok ;		
	}

	public void setPassword( int userid, String passwd ) throws SQLException
	{
		if( passwd==null ) passwd = "";
		String encryptPass = StringUtils.hash ( passwd );

		String sql = " UPDATE user_list SET password = ? WHERE userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, encryptPass );
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	public void setPassword( String loginid, String new_pass ) throws SQLException
	{
		int userid = getUserid( loginid );
		setPassword( userid, new_pass );
	}

	
	public Element getUserList( Map map ) throws SQLException
	{
		Element elm_list = new Element( "user_list" );
		String sql = " SELECT u.userid, u.loginid, u.username, u.shopid, u.userstatus status, "
			+ " u.menuroot, m.menulabel, b.shopname "
			+ " FROM user_list u JOIN shop b ON (b.shopid=u.shopid) " 
			+ " LEFT OUTER JOIN menu_list m ON (m.menuid=u.menuroot) ";
		
		String parm_shopid 	= Filter.getParameter( map, "shopid" ); 
		String parm_loginid = Filter.getParameter( map, "loginid" ); 
		String parm_userid 	= Filter.getParameter( map, "userid" ); 
		String parm_username 	= Filter.getParameter( map, "username" ); 
		
		Filter filter = new Filter();
		if( parm_shopid != null ) 	filter.add( " u.shopid="  + new Values( parm_shopid ).toString4String() );
		if( parm_loginid != null ) 	filter.add( " u.loginid=" + new Values( parm_loginid ).toString4String() );
		if( parm_userid != null ) 	filter.add( " u.userid="  + parm_userid );
		if( parm_username != null ) filter.add( " u.username=" + new Values( parm_username ).toString4String() );
		
		if( filter.count() >0 ) sql += " WHERE " + filter.toString();
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		while( rs.next() ){
			Element elm = new Element( "user" );
			elm_list.addContent( elm );
			
			String userid 	= rs.getString( "userid" );
			String loginid 	= rs.getString( "loginid" );
			String username = rs.getString( "username" );
			String shopid 	= rs.getString( "shopid" );
			String shopname = rs.getString( "shopname" );
			String status 	= rs.getString( "status" );
			String menuroot = rs.getString( "menuroot" );
			String menulabel = rs.getString( "menulabel" );
			
			userid 		= ( userid == null )? 	"" : SqlUtil.fromLocal( userid ).trim();
			loginid 	= ( loginid == null )? 	"" : SqlUtil.fromLocal( loginid ).trim();
			username 	= ( username == null )? "" : SqlUtil.fromLocal( username ).trim();
			shopid 		= ( shopid == null )? 	"" : SqlUtil.fromLocal( shopid ).trim();
			shopname 	= ( shopname == null )? "" : SqlUtil.fromLocal( shopname ).trim();
			menulabel	= ( menulabel == null )? "" : SqlUtil.fromLocal( menulabel ).trim();
			
			elm.addContent( (new Element( "userid" )).addContent(userid) );
			elm.addContent( (new Element( "loginid" )).addContent(loginid) );
			elm.addContent( (new Element( "username" )).addContent(username) );
			elm.addContent( (new Element( "shopid" )).addContent(shopid) );
			elm.addContent( (new Element( "shopname" )).addContent(shopname) );
			elm.addContent( (new Element( "status" )).addContent(status) );
			elm.addContent( (new Element( "menuroot" )).addContent(menuroot) );
			elm.addContent( (new Element( "menulabel" )).addContent(menulabel) );
		}
		rs.close();
		pstmt.close();
		return elm_list;
	}
	
	private int getMaxUserid() throws SQLException
	{
		String sql = " SELECT MAX(userid) FROM user_list ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql );
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		stmt.close();
		return id;
	}
	
	final private Connection conn ;
}

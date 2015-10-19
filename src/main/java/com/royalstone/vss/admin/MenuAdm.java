/*
 * Created on 2005-12-13
 *
 */
package com.royalstone.vss.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;

import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于维护角色-用户之间的关系.
 * @author meng
 *
 */
public class MenuAdm {
	
	/**
	 * @param conn	权限控制用数据库连接.
	 */
	public MenuAdm( Connection conn )
	{
		this.conn = conn;
	}
	
	public Element getMenuDetail( int menuid ) throws SQLException
	{
		Element elm_detail = new Element( "menu_detail" );
		int rootid = this.getRootid( menuid );
		int parent = this.getHeadid( menuid );
		Element elm_self = this.getMenuInfo( menuid );
		Element elm_root = this.getMenuInfo( rootid );
		Element elm_parent = this.getMenuInfo( parent );
		Element elm_children = this.getChildren( menuid );
		
		elm_detail.addContent( new Element( "self" ).addContent( elm_self ) );
		elm_detail.addContent( new Element( "root" ).addContent( elm_root ) );
		elm_detail.addContent( new Element( "parent" ).addContent( elm_parent ) );
		elm_detail.addContent( new Element( "children" ).addContent( elm_children ) );
		
		if( rootid != menuid ){
			Element elm_sibling  = this.getChildren( parent );
			elm_detail.addContent( new Element( "sibling" ).addContent( elm_sibling ) );
		}
		return elm_detail;
	}
	
	
	public Element getMenuInfo( int id ) throws SQLException
	{
		Element elm = new Element( "menu" );

		String sql = " SELECT e.menuroot, e.headmenuid, e.menuid, e.menulabel, e.menulevel, "
			+ " o.moduleid, o.action, o.modulename "
			+ " FROM menu_list e LEFT OUTER JOIN module_list o ON (o.moduleid=e.moduleid) "
			+ " WHERE e.menuid = ? " ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt(1, id );
		
		ResultSet rs = pstmt.executeQuery();
		if( !rs.next() ) throw new SQLException( "NOT FOUND menuid: " + id, "", 100 );
		
		String menuroot 	= rs.getString( "menuroot" );
		String headmenuid 	= rs.getString( "headmenuid" );
		String menuid 		= rs.getString( "menuid" );
		String menulabel 	= rs.getString( "menulabel" );
		String menulevel 	= rs.getString( "menulevel" );
		String moduleid 	= rs.getString( "moduleid" );
		String action 		= rs.getString( "action" );
		String modulename 	= rs.getString( "modulename" );
		
		menulabel = ( menulabel==null )?	"" : SqlUtil.fromLocal(menulabel).trim();
		
		
		if( menuroot != null ) elm.setAttribute( "menuroot", menuroot );
		if( headmenuid != null ) elm.setAttribute( "headmenuid", headmenuid );
		if( menuid != null ) elm.setAttribute( "menuid", menuid );
		if( menulabel != null ) elm.setAttribute( "menulabel", menulabel );
		
		if( menulevel != null ) elm.setAttribute( "menulevel", menulevel );
		
		if( moduleid == null ) moduleid = "";
		modulename = ( modulename==null )?	"" : SqlUtil.fromLocal(modulename).trim();
		if( action != null && action.length()>0 ){
			elm.setAttribute( "action", 	action );
			elm.setAttribute( "moduleid", 	moduleid );
			elm.setAttribute( "modulename", modulename );
		}

		rs.close();
		pstmt.close();
		return elm;
	}
	
	/**
	 * @param menuid
	 * @throws SQLException
	 */
	public void removeMenu( int menuid ) throws SQLException
	{
		String sql = " DELETE FROM menu_list where menuid = " + menuid;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	public Element getChildren( int id ) throws SQLException
	{
		String sql = " SELECT e.menuroot, e.headmenuid, e.menuid, e.menulabel, "
			+ " o.moduleid, o.action, o.modulename,e.cmid,cm.note "
			+ " FROM menu_list e " +
			  " LEFT OUTER JOIN module_list o ON (o.moduleid=e.moduleid) "
			+ " left join CMDEFINITION cm on cm.cmid=e.cmid "
			+ " WHERE e.menuid <> e.headmenuid AND e.headmenuid = ? " ;
		
		ResultSet rs = SqlUtil.queryPS(conn, sql, id);
		Element elm_lst = new Element( "menu_list" );
		while( rs.next() ){
			String menuroot 	= rs.getString( "menuroot" );
			String headmenuid 	= rs.getString( "headmenuid" );
			String menuid 		= rs.getString( "menuid" );
			String menulabel 	= rs.getString( "menulabel" );
			String moduleid 	= rs.getString( "moduleid" );
			String action 		= rs.getString( "action" );
			String modulename 	= rs.getString( "modulename" );
			String cmid 		= rs.getString( "cmid" );
			String cmidnote 		= rs.getString( "note" );
			menulabel = ( menulabel==null )?	"" : SqlUtil.fromLocal(menulabel).trim();
			
			Element elm = new Element( "menu" );

			if( menuroot != null ) elm.setAttribute( "menuroot", menuroot );
			if( headmenuid != null ) elm.setAttribute( "headmenuid", headmenuid );
			if( menuid != null ) elm.setAttribute( "menuid", menuid );
			if( menulabel != null ) elm.setAttribute( "menulabel", menulabel );
						
			if( moduleid == null ) moduleid = "";
			modulename = ( modulename==null )?	"" : SqlUtil.fromLocal(modulename).trim();
			
			cmidnote = cmidnote==null?"":SqlUtil.fromLocal(cmidnote);
			
			if( action != null && action.length()>0 ){
				action = action.trim();
				elm.setAttribute( "action", 	action );
				elm.setAttribute( "moduleid", 	moduleid );
				elm.setAttribute( "modulename", modulename );
				elm.setAttribute( "cmid", cmid );
				elm.setAttribute( "cmidnote", cmidnote );
			}
			elm_lst.addContent( elm );
		}
		
		rs.close();
		return elm_lst;
	}
	
	
	
	public Element getRootList( ) throws SQLException
	{
		String sql = " SELECT m.menuid, m.headmenuid, m.menuroot, m.menulabel,m.roletype,r.roletypename "
			+ " FROM menu_list m " +
					" join roletypeconfig r on r.roletype=m.roletype"
			+ " WHERE m.menuid = m.headmenuid AND m.menuid = m.menuroot " ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		Element elm_lst = new Element( "menu_list" );
		while( rs.next() ){
			String menuroot 	= rs.getString( "menuroot" );
			String headmenuid 	= rs.getString( "headmenuid" );
			String menuid 		= rs.getString( "menuid" );
			String menulabel 	= rs.getString( "menulabel" );
			String roletypename = rs.getString( "roletypename" );
			menulabel = ( menulabel==null )?	"" : SqlUtil.fromLocal(menulabel).trim();
			
			Element elm = new Element( "menu" );

			if( menuroot != null ) elm.setAttribute( "menuroot", menuroot );
			if( headmenuid != null ) elm.setAttribute( "headmenuid", headmenuid );
			if( menuid != null ) elm.setAttribute( "menuid", menuid );
			if( menulabel != null ) elm.setAttribute( "menulabel", menulabel );
			if( roletypename != null ) elm.setAttribute( "roletypename", SqlUtil.fromLocal(roletypename) );
			
			elm.setAttribute( "action", 	"" );
			elm.setAttribute( "moduleid", 	"" );
			elm.setAttribute( "modulename", "" );
			elm_lst.addContent( elm );
		}
		
		rs.close();
		pstmt.close();
		return elm_lst;
	}
	
	public Element getRootListByRoletype( String roletype ) throws SQLException
	{
		String sql = " SELECT menuid, headmenuid, menuroot, menulabel "
			+ " FROM menu_list "
			+ " WHERE menuid = headmenuid AND menuid = menuroot and (roletype=0 or roletype="+roletype+" " +
					" or roletype in ( select roletype from roletypeconfig where roletype="+roletype+" or (headroletype<>roletype and headroletype="+roletype+")))" ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		Element elm_lst = new Element( "menu_list" );
		while( rs.next() ){
			String menuroot 	= rs.getString( "menuroot" );
			String headmenuid 	= rs.getString( "headmenuid" );
			String menuid 		= rs.getString( "menuid" );
			String menulabel 	= rs.getString( "menulabel" );
			
			menulabel = ( menulabel==null )?	"" : SqlUtil.fromLocal(menulabel).trim();
			
			Element elm = new Element( "menu" );

			if( menuroot != null ) elm.setAttribute( "menuroot", menuroot );
			if( headmenuid != null ) elm.setAttribute( "headmenuid", headmenuid );
			if( menuid != null ) elm.setAttribute( "menuid", menuid );
			if( menulabel != null ) elm.setAttribute( "menulabel", menulabel );
			
			elm.setAttribute( "action", 	"" );
			elm.setAttribute( "moduleid", 	"" );
			elm.setAttribute( "modulename", "" );
			elm_lst.addContent( elm );
		}
		
		rs.close();
		pstmt.close();
		return elm_lst;
	}
	
	public Element getUserRootList( String userid ) throws SQLException
	{
		String sql = " SELECT distinct ml.menuid, ml.headmenuid, ml.menuroot, ml.menulabel "
			+ " FROM menu_list ml " +
					" join role_list rl on (rl.roletype=ml.roletype or ml.roletype=0 or rl.roletype in ( select roletype from roletypeconfig where (headroletype<>roletype and headroletype=ml.roletype)))" +
					" join user_role ur on (ur.userid="+userid+" and ur.roleid=rl.roleid)"
			+ " WHERE menuid = headmenuid AND menuid = menuroot " ;
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		Element elm_lst = new Element( "menu_list" );
		while( rs.next() ){
			String menuroot 	= rs.getString( "menuroot" );
			String headmenuid 	= rs.getString( "headmenuid" );
			String menuid 		= rs.getString( "menuid" );
			String menulabel 	= rs.getString( "menulabel" );
			
			menulabel = ( menulabel==null )?	"" : SqlUtil.fromLocal(menulabel).trim();
			
			Element elm = new Element( "menu" );

			if( menuroot != null ) elm.setAttribute( "menuroot", menuroot );
			if( headmenuid != null ) elm.setAttribute( "headmenuid", headmenuid );
			if( menuid != null ) elm.setAttribute( "menuid", menuid );
			if( menulabel != null ) elm.setAttribute( "menulabel", menulabel );
			
			elm.setAttribute( "action", 	"" );
			elm.setAttribute( "moduleid", 	"" );
			elm.setAttribute( "modulename", "" );
			elm_lst.addContent( elm );
		}
		
		SqlUtil.close(rs);
		return elm_lst;
	}
	
	private int getRootid( int menuid ) throws SQLException
	{
		int rootid = -1;
		String sql = " SELECT menuroot FROM menu_list WHERE menuid = " + menuid;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql );
		if( rs.next() ) rootid = rs.getInt(1);
		rs.close();
		stmt.close();
		if( rootid == -1 )throw new SQLException( "NOT FOUND for menuid: " + menuid, "NOT FOUND", 100 );
		return rootid;
	}
		
	public Element addMenuRoot( String menulabel,int roletype ) throws SQLException
	{
		int id = getMaxMenuid();
		++ id;
		
		String sql = " INSERT INTO menu_list ( menuid, menuroot, headmenuid, menulevel, moduleid, menulabel,roletype ) "
			+ " VALUES ( ?, ?, ?, ?, ?, ? ,?) ";

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt(1, id );
		pstmt.setInt(2, id );
		pstmt.setInt(3, id );
		pstmt.setInt(4, 0 );
		pstmt.setInt(5, 0 );
		pstmt.setString( 6, SqlUtil.toLocal(menulabel) );
		pstmt.setInt(7, roletype );
		pstmt.executeUpdate();
		pstmt.close();

		return getMenuInfo(id);
	}
	
	
	public Element addMenu( int headmenuid, String menulabel, int moduleid ) throws SQLException
	{
		int rootid = getRootid( headmenuid );
		int id = getMaxMenuid();
		++ id;
		
		int level = getLevel( headmenuid );
		++ level;
		String sql = " INSERT INTO menu_list ( menuid, menuroot, headmenuid, menulevel, moduleid, menulabel,roletype ) "
			+ " VALUES ( ?, ?, ?, ?, ?, ?,(select roletype from menu_list where menuid=?) ) ";

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt(1, id );
		pstmt.setInt(2, rootid );
		pstmt.setInt(3, headmenuid );
		pstmt.setInt(4, level );
		pstmt.setInt(5, moduleid );
		pstmt.setString( 6, SqlUtil.toLocal(menulabel) );
		pstmt.setInt(7, headmenuid );

		pstmt.executeUpdate();
		pstmt.close();

		return getMenuInfo(id);
	}
	
	
	public Element updateMenu( int menuid, String menulabel, int moduleid ) throws SQLException
	{
		String sql = " UPDATE menu_list set menulabel = ?, moduleid = ? WHERE menuid = " + menuid ;

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, SqlUtil.toLocal(menulabel) );
		pstmt.setInt(2, moduleid );
		pstmt.executeUpdate();
		pstmt.close();

		return getMenuInfo(menuid);
	}
	
	/**
	 * @param menuid
	 * @param menulabel
	 * @param shopid
	 * @param note
	 * @return
	 * @throws SQLException
	 */
	public Element addMenu( int menuid, int rootid, int headid, int moduleid, String menulabel ) throws SQLException
	{
		int id = ( menuid >= 0 ) ? menuid : ( getMaxMenuid()+1 );
		
		String sql = " INSERT INTO role_list ( roleid, rolename, shopid, note ) "
			+ " VALUES ( ?, ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt(1, id );
		pstmt.setString( 2, SqlUtil.toLocal(menulabel) );
		pstmt.executeUpdate();
		pstmt.close();
		
		try {
			Element elm_role = getMenuInfo(id);
			return elm_role;
		} catch (SQLException e) {
			throw new SQLException( "操作数据库过程中出现异常,请查询.", "", e.getErrorCode() );
		}
	}

	
	public void updateCM(int menuid,long cmid) throws SQLException{
		String sql=" update menu_list set cmid=? where menuid=?";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setLong(1, cmid );
		pstmt.setInt( 2, menuid );
		pstmt.executeUpdate();
		pstmt.close();
	}
	/**
	 * 取上级菜单的ID
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private int getHeadid( int id ) throws SQLException
	{
		String sql = " SELECT headmenuid FROM menu_list WHERE menuid = " + id;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql );
		if( !rs.next() )throw new SQLException( "NOT FOUND menuid: " + id, "NOT FOUND", 100 );
		int headid = rs.getInt(1);
		rs.close();
		stmt.close();
		return headid;
	}

	private int getLevel( int id ) throws SQLException
	{
		String sql = " SELECT menulevel FROM menu_list WHERE menuid = " + id;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql );
		if( !rs.next() )throw new SQLException( "NOT FOUND menuid: " + id, "NOT FOUND", 100 );
		int level = rs.getInt(1);
		rs.close();
		stmt.close();
		return level;
	}
	
	/**
	 * 取菜单表 menu_list 内的最大角色编号.
	 * @return	当前所用最大角色编号
	 * @throws SQLException
	 */
	private int getMaxMenuid() throws SQLException
	{
		String sql = " SELECT MAX(menuid) FROM menu_list ";
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

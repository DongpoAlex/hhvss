package com.royalstone.vss.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.sql.SqlUtil;

public class ModuleAdm
{
	
    public ModuleAdm( Connection conn )
    {
        this.conn=conn;
    }
    
    public Element listAll() throws SQLException
    {
        String sql = " SELECT a.moduleid, a.modulename, a.rightid, a.action,a.roletype,b.roleTypeName,b.headroletype FROM module_list a" +
        		" join roleTypeConfig b on b.roletype=a.roletype order by a.moduleid ";
        
        return SqlUtil.getRowSetElement(conn, sql, "rowset");
    }
    
    public Element listRoleTypeModule(int headMenu) throws SQLException
    {
        String sql = " SELECT a.moduleid, a.modulename, a.rightid, a.action,a.roletype,b.roleTypeName,b.headroletype " +
        		" FROM module_list a" +
        		" join roleTypeConfig b on b.roletype=a.roletype " +
        		" where a.roletype=0 or a.roletype=(select roletype from menu_list where menuid="+headMenu+") " +
        				" or a.roletype in (select roletype from roletypeconfig where headroletype<>roletype and headroletype=(select roletype from menu_list where menuid="+headMenu+")) ";
        
        return SqlUtil.getRowSetElement(conn, sql, "rowset");
    }
    
//    public void getOneModule(String moduleid) throws SQLException
//    {
//    	elm_list = null;
//        String sql = " SELECT moduleid, modulename, rightid, action FROM module_list WHERE moduleid=? " ;
//        
//        PreparedStatement pstmt = conn.prepareStatement( sql );
//        pstmt.setInt(1,Integer.parseInt(moduleid));
//        ResultSet rs 	= pstmt.executeQuery();
//        XResultAdapter adapter = new XResultAdapter( rs );
//        if( rs.next() ) elm_list = adapter.getElement4CurrentRow( "module" );
//        rs.close();
//        pstmt.close();
//        if ( elm_list == null ) throw new SQLException( "没有这个模块: " + moduleid, "", 100 );
//    }
    
    public Module getModule( int moduleid ) throws SQLException
    {
        String sql = " SELECT moduleid, modulename, rightid, action FROM module_list WHERE moduleid=? " ;
        PreparedStatement pstmt = conn.prepareStatement( sql );
        pstmt.setInt(1,moduleid);
        ResultSet rs 	= pstmt.executeQuery();
        if( !rs.next() ) throw new SQLException( "没有这个模块: " + moduleid, "", 100 );
        String modulename = rs.getString( "modulename" );
        String action = rs.getString( "action" );
        int rightid = rs.getInt( "rightid" );
        modulename = ( modulename == null ) ? "" : SqlUtil.fromLocal( modulename );
        action = ( action == null ) ? "" : SqlUtil.fromLocal( action );
        
        Module module = new Module( moduleid, rightid, modulename, action );
        rs.close();
        pstmt.close();
    	return module;
    }
    
    public Element toElement()
    {
        return elm_list;
    }
    
    private Connection conn = null;
    private Element elm_list = null;
}

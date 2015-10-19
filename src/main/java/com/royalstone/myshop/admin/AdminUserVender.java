package com.royalstone.myshop.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XResultAdapter;

public class AdminUserVender 
{
	public AdminUserVender( Connection conn )
	{
		this.conn = conn;
	}
	
	
	/**
	 * 此方法将从数据库中取出满足条件的供应商用户.
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public Element getUserList( Map map ) throws SQLException
	{
		Filter filter = new Filter();
		{
			/**
			 * 如果未指定参数 venderid_min, 则venderid起始值为'';
			 */
			String venderid_min = "";
			String[] s_min = (String []) map.get( "venderid_min" );
			if( s_min != null && s_min.length >0 ) venderid_min = s_min[0].trim();
			filter.add( " ue.datavalue >= " + ValueAdapter.toString4String(venderid_min) );
		}
		
		{
			/**
			 * 如果未指定参数 venderid_max, 则SQL 中不指定venderid 最大值;
			 */
			String venderid_max = "";
			String[] s_max = (String []) map.get( "venderid_max" );
			if( s_max != null && s_max.length >0 ) venderid_max = s_max[0].trim();
			if( venderid_max != null && venderid_max.length()>0 ) 
				filter.add( " ue.datavalue <= " + ValueAdapter.toString4String(venderid_max) );
		}
		
		/**
		 * 最多取出1000条记录
		 */
		String sql_list = " SELECT "
			+ " u.userid, u.username, u.loginid, u.shopid, u.menuroot "
			+ " FROM user_list u  "
			+ " JOIN user_role ur ON (ur.userid=u.userid) "
			+ " JOIN role_list r  ON (r.roleid=ur.roleid AND rolename='VENDER' ) "
			+ " JOIN user_environment ue ON(ue.userid=u.userid AND ue.dataflag='venderid' ) ";
		
		if( filter.count() >0 ) sql_list += " WHERE ROWNUM>10000 and " + filter.toString();
		
		PreparedStatement pstmt = conn.prepareStatement( sql_list );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm = adapter.getRowSetElement( "user_list", "row" );
		elm.setAttribute( "rows", "" + adapter.rows() );
		rs.close();
		pstmt.close();
		return elm;
	}

	
	final private Connection conn;
}

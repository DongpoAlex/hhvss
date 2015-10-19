package com.royalstone.myshop.info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于供应商资料.
 * 可以支持以下过滤条件:  vendername, venderid.
 * 如果满足条件的记录较多, 则只返回前面1000条.
 * @author liu
 */
public class InfoVender 
{
	final static int ROWS_LIMIT_SOFT = 1000;
	
	public InfoVender ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
	
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		
		String sql_sel = " SELECT v.venderid, v.vendername, v.address, v.zipcode, v.website, v.email, " 
				+ " v.telno, v.faxno,venderpaylevel(v.paylevel) paylevel FROM vender v " ;

		String sql = sql_sel + sql_where;
	
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_info = adapter.getRowSetElement( "catalogue", "row", 1, ROWS_LIMIT_SOFT );
		elm_info.setAttribute( "rows", "" + adapter.rows() );
		rs.close();
		pstmt.close();
	}
	
	private Filter cookFilter( Map map )
	{
		Filter filter = new Filter();
		
		{
			String[] ss_min = (String [] ) map.get( "venderid_min" );
			String[] ss_max = (String [] ) map.get( "venderid_max" );
			if( ss_min != null && ss_min.length >0&&ss_max != null && ss_max.length >0 )
			{
				String  venderid_min = "";
				String  venderid_max = "";
				
				if( ss_min != null && ss_min.length >0 && ss_min[0] != null && ss_min[0].length() >0 ) {
					venderid_min = ss_min[0].trim();
					venderid_min = SqlUtil.toLocal( venderid_min );
				}
				if( ss_max != null && ss_max.length >0 && ss_max[0] != null && ss_max[0].length() >0 ) {
					venderid_max = ss_max[0].trim();		
					venderid_max = SqlUtil.toLocal( venderid_max );
				}
				
				filter.add( " v.venderid BETWEEN '"+ venderid_min +"' AND '" + venderid_max +"'" );
			}
		}
	
		{
			String[] ss = (String [] ) map.get( "venderid" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length() >0 ) {
				String name = ss[0].trim();		
				name = SqlUtil.toLocal( name );
				filter.add( " v.venderid = '" + name +"'" );
			}
		}
		
		/**
		 * 如果指定参数 vendername, 则应作模糊匹配.
		 */
		{
			String[] ss = (String [] ) map.get( "vendername" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length() >0 ) {
				String name = ss[0].trim();		
				name = SqlUtil.toLocal( name );
				name = "%" + name + "%" ;
				name = ValueAdapter.toString4String( name );
				filter.add( " v.vendername LIKE " + name );
			}
		}
		
		{
			String[] ss = (String [] ) map.get( "paylevel" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length() >0 ) {
				String name = ss[0].trim();		
				name = SqlUtil.toLocal( name );
				filter.add( " v.paylevel = '" + name +"'" );
			}
		}


		return filter;
	}
	
	
	public Element toElement()
	{
		return elm_info;
	}

	private Element elm_info = null;
}

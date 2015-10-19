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
 * 此模块用于查询商品资料.
 * 可以支持以下过滤条件: goodsid_min, goodsid_max, goodsname, deptid.
 * 如果满足条件的记录较多, 则只返回前面1000条.
 * @author meng
 *
 */
public class InfoGoods
{

	final static int ROWS_LIMIT_SOFT = 1000;
	
	public InfoGoods( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		
		String sql_sel = " SELECT g.goodsid, g.barcode, g.goodsname, g.shortname, " +
				" g.spec, g.unitname, g.deptid, c.categoryname deptname, g.status, g.hiredate, g.firedate,g.approvalnum,g.manufacturer " +
				" FROM goods g JOIN category c ON (g.deptid=c.categoryid) " ;

		String sql = sql_sel + sql_where;
		System.out.println(sql);
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
			String[] ss = (String [] ) map.get( "deptid" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length() >0 ) {
				int id = Integer.parseInt( ss[0].trim() );
				filter.add( " g.deptid = " + id );
			}
		}

		{
			String[] ss = (String [] ) map.get( "goodsid_min" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length() >0 ) {
				int id = Integer.parseInt( ss[0].trim() );
				filter.add( " g.goodsid >= " + id );
			}
		}

		{
			String[] ss = (String [] ) map.get( "goodsid_max" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length() >0 ) {
				int id = Integer.parseInt( ss[0].trim() );
				filter.add( " g.goodsid <= " + id );
			}
		}
		
		/**
		 * 如果指定参数 goodsname, 则应作模糊匹配.
		 */
		{
			String[] ss = (String [] ) map.get( "goodsname" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length() >0 ) {
				String name = ss[0].trim();			
				System.out.println( "goodsname:" + name );				
				name = SqlUtil.toLocal( name );
				System.out.println( "goodsname:" + name );		
				name = "%" + name + "%" ;
				name = ValueAdapter.toString4String( name );
				filter.add( " g.goodsname LIKE " + name );
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

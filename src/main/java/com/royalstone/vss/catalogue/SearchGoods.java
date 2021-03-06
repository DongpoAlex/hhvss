package com.royalstone.vss.catalogue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

public class SearchGoods {
	final static private String sql_sel =  " SELECT "
										+"g.goodsid,g.barcode,g.goodsname,g.shortname,"
										+"g.spec,g.unitname,d.deptname,g.status,g.hiredate,"
										+"g.firedate,g.touchtime,g.toucher";
	final static private String sql_join = " FROM goods g"
								+ " JOIN dept d ON ( g.deptid = d.deptid ) ";
	public SearchGoods ( Connection conn, Map map ) throws SQLException, InvalidDataException, IOException{
		Filter filter = cookFilter( map );
		this.conn = conn;
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		String sql_where = ( filter.count() == 0 )? " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft() :" where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft()+" and "+filter.toString() ;
		String sql = sql_sel + sql_join  +sql_where;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row" );
		
		int rows = adapter.rows();
		if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND", 100 );
		elm_cat.setAttribute( "rows", "" + rows );
	}
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) " + sql_join + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getting rows.", "", -1 );
		int rows = rs.getInt( 1 );

		return rows;
	}
	private Filter cookFilter( Map map )
	{
		Filter filter = new Filter();
		String[] ss = null;
		ss = (String [] ) map.get( "goodsid" );
		if( ss != null && ss.length >0 ) {
			Values val_goodsid = new Values ( ss );
			filter.add("goodsid IN (" + val_goodsid.toString4String() + ") " );
			return filter;
		}
		ss = (String [] ) map.get( "barcode" );
		if( ss != null && ss.length >0 ) {
			Values val_barcode = new Values ( ss );
			filter.add("barcode IN (" + val_barcode.toString4String() + ") " );
			return filter;
		}
		ss = (String [] ) map.get( "goodsname" );
		if( ss != null && ss.length >0 ) {
			Values val_goodsname = new Values ( ss );
			filter.add("goodsname IN (" + val_goodsname.toString4String() + ") " );
		}
		ss = (String [] ) map.get( "deptid" );
		if( ss != null && ss.length >0 ) {
			Values val_deptid = new Values ( ss );
			filter.add("deptid IN (" + val_deptid.toString4String() + ") " );
		}
		return filter;
	}
	public Element toElement() { return elm_cat; }
	
	private Element elm_cat = null;
	final private Connection conn;
}

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
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

public class SearchChargeshop {
	final static private String sql_sel =  " SELECT " 
		+ " ch.SheetID,s.shopname,ch.Flag,ch.Editor,ch.EditDate,ch.Checker,"
		+ " ch.CheckDate,ch.Verifier,ch.VerifyDate,ch.Note,ch.PrintCount" ;
	final static private String sql_join = " FROM "
		+ " ChargeShop0 ch "
		+ " JOIN shop s ON (ch.ShopID=s.ShopID) " ;
	public SearchChargeshop ( Connection conn, Map map ) throws SQLException, InvalidDataException, IOException
	{
		this.conn = conn;
		Filter filter = cookFilter( map );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft():" where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft()+" and "+filter.toString() ;
		String sql = sql_sel + sql_join + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row" );
		
		int rows = adapter.rows();
		if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND", 100 );
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "purchase" );
	}
	
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) " + sql_join + sql_where ;
//		System.out.println(sql);
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getCount.", "", -1 );
		int rows = rs.getInt( 1 );

		return rows;
	}
	

	private Filter cookFilter( Map map ) throws InvalidDataException
	{
		Filter filter = new Filter();
		String[] ss = null;
		
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String [] ) map.get( "sheetid" );
		if( ss != null && ss.length >0 ) {
			Values val_sheetid = new Values ( ss );
			filter.add( "ch.sheetid IN (" + val_sheetid.toString4String() + ") " );
			return filter;
		}
		
		/**
		 * 如果未指定sheetid, 则根据其他条件联合查找.
		 */
		
		ss = (String [] ) map.get( "shopid" );
		if( ss != null && ss.length >0 ) {
			Values val_editor = new Values ( ss );
			filter.add(  "ch.shopid = (" + val_editor.toString4String() + ") " );
		}
		ss = (String [] ) map.get( "Flag" );
		if( ss != null && ss.length >0 ) {
			Values val_editor = new Values ( ss );
			filter.add(  "ch.Flag = (" + val_editor.toString4String() + ") " );
		}
		/** parse filter on dates
		 */
		ss = ( String [] ) map.get( "editdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(ch.editdate) >= " + ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "editdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(ch.editdate) <= " + ValueAdapter.std2mdy(date) );
		}
		
		return filter;
	}
	
	public Element toElement() { return elm_cat; }
	
	final private Connection conn;
	private Element elm_cat = null;
}


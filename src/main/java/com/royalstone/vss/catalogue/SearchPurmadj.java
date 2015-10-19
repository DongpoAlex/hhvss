package com.royalstone.vss.catalogue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.DbAdm;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

public class SearchPurmadj 
{
	final private String tmp00 = DbAdm.getTmpName();
	
	private String sql_sel =  " SELECT "
	+ " '' as checkbox,"
	+ " h.sheetid, h.accmonth, h.BookNo, " 
	+ " h.totalamt, h.totalamt17, h.totaltaxamt17, h.totaltaxamt13, "
	+ " h.flag, "
	+ " h.editor, h.editdate, "
	+ " h.checker, h.checkdate, h.note "
	+ " FROM " +tmp00+ " t "
	+ " JOIN purmadj h ON (h.sheetid=t.sheetid) "
	;
	
	final static private String sql_join = " FROM "
		+ " purmadj h "
		+ " JOIN purmadjitem t ON ( t.SheetID = h.SheetID ) "
		+ " JOIN vender v ON ( v.venderid = t.venderid ) ";
	
	
	
	public SearchPurmadj ( Connection conn, Map map ) throws SQLException, InvalidDataException, IOException
	{
		Filter filter = cookFilter( map );
		this.conn = conn;
		
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );

		String sql_where = ( filter.count() == 0 )? " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft() : " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft()+" and " + filter.toString() ;
		String sql_charge =  
			" SELECT h.sheetid"
			+ " FROM purmadj h JOIN purmadjitem t ON (t.sheetid=h.sheetid) "
			+ sql_where
			+ " GROUP BY h.sheetid "
			+ " INTO TEMP " +tmp00+ "  ";
		
//		System.out.println(sql_charge);
		Statement stmt = conn.createStatement();
		stmt.execute( sql_charge );
		
		PreparedStatement pstmt = conn.prepareStatement( sql_sel );
//		System.out.println(sql_sel +sql_where);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row" );
		
		int rows = adapter.rows();
		if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND", 100 );
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "purcostadj" );
		
		rs.close();
		pstmt.close();
		stmt.close();
		
		DbAdm.dropTable(conn, tmp00);
	}
	
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(DISTINCT h.sheetid) " + sql_join + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getting rows.", "", -1 );
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
			filter.add( "h.sheetid IN (" + val_sheetid.toString4String() + ") " );
			return filter;
		}
		
		ss = (String [] ) map.get( "venderid" );
		if( ss != null && ss.length >0 ) {
			Values val_vender = new Values ( ss );
			filter.add(  "t.venderid IN (" + val_vender.toString4String() + ") " );
		}
		
		ss = (String [] ) map.get( "flag" );
		if( ss != null && ss.length >0 ) {
			Values val_flag = new Values ( ss );
			filter.add(  "h.flag IN (" + val_flag.toString4String() + ") " );
		}
		
		ss = (String [] ) map.get( "shopid" );
		if( ss != null && ss.length >0 ) {
			Values val_shopid = new Values ( ss );
			filter.add(  "t.ShopID IN (" + val_shopid.toString4String() + ") " );
		}
		
		ss = (String [] ) map.get( "editor" );
		if( ss != null && ss.length >0 ) {
			Values val_editor = new Values ( ss );
			filter.add(  "h.editor IN (" + val_editor.toString4String() + ") " );
		}
		
		ss = (String [] ) map.get( "checker" );
		if( ss != null && ss.length >0 ) {
			Values val_checker = new Values ( ss );
			filter.add( "h.checker IN (" + val_checker.toString4String() + ") " );
		}
		
		
		ss = ( String [] ) map.get( "editdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(h.editdate) >= " + ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "editdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(h.editdate) <= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(h.checkdate) >= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(h.checkdate) <= " + ValueAdapter.std2mdy(date)  );
		}
		
		return filter;
	}
	public Element toElement() { return elm_cat; }
	
	final private Connection conn;
	private Element elm_cat = null;
}

package com.royalstone.vss.catalogue;

import java.io.File;
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
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.excel.Workbook;
import com.royalstone.vss.VSSConfig;

/*
 * @author	liuwendong
 * @param 	total_max 订单总数最大值
 * @param 	total_min 订单总数最小值
 * @param 	total_max 新订单总数最大值
 * @param 	total_min 新订单总数最小值
 * @param 	total_max 确认订单总数最大值
 * @param 	total_min 确认订单总数最小值
 */
public class SearchFresh4retail
{
	
	final private static String tmp00 = "temp_SearchFresh4retail1";
	final private static String tmp01 = "temp_SearchFresh4retail2";
	
	final String sql_drp0 = " DROP TABLE " + tmp00 ;
	final String sql_drp1 = " DROP TABLE " + tmp01;
	
	final static String sql_tmp00 = " create global temporary table " + tmp00
		+ " ( " 
		+ " venderid CHAR(10),   " 
		+ " count_fresh0	INT DEFAULT 0 NOT NULL,  "
		+ " count_fresh1	INT DEFAULT 0 NOT NULL,  "
		+ " count_confirm	INT DEFAULT 0 NOT NULL,  " 
		+ " count_total		INT DEFAULT 0 NOT NULL   "
		+ " )  " ;
	
	final static String sql_tmp01 =  "   " 
		+ " create global temporary table "+ tmp01 
		+ " ( "
		+ " venderid CHAR(10),   " 
		+ " count_fresh0	INT DEFAULT 0 NOT NULL,  " 
		+ " count_fresh1	INT DEFAULT 0 NOT NULL,  " 
		+ " count_confirm	INT DEFAULT 0 NOT NULL,  " 
		+ " count_total		INT DEFAULT 0 NOT NULL   "
		+ " )  ";
	
	final static String sql_intmp00 = 	" INSERT INTO "+ tmp00 +" ( venderid, count_fresh0 )   "
		+ " SELECT venderid, COUNT(*) FROM cat_order "
		+ " WHERE status = 0 GROUP BY venderid ";
	
	final static String sql_intmp000 = 	" INSERT INTO "+ tmp00 +" ( venderid, count_fresh1 )   "
	+ " SELECT venderid, COUNT(*) FROM cat_order "
	+ " WHERE status = 1 GROUP BY venderid ";
	
	final static String sql_intmp01 = " INSERT INTO "+ tmp00 +" ( venderid, count_confirm )  "
		+ " SELECT venderid, COUNT(*) FROM cat_order "
		+ " WHERE status IN (10) GROUP BY venderid ";
	
	final static String sql_intmp = " INSERT INTO "+ tmp01 +" ( venderid, count_fresh0, count_fresh1, count_confirm ) "
		+ " SELECT venderid, SUM(count_fresh0), SUM(count_fresh1), SUM(count_confirm)       "
		+ " FROM " + tmp00 
		+ " GROUP BY venderid ";
	
	final String sql_update = " UPDATE "+ tmp01 +" SET count_total = count_fresh0 + count_fresh1 + count_confirm ";
	


	public SearchFresh4retail ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
	
		this.conn = conn;

		this.map = map;
	}
	
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) FROM "+ tmp01 +" t "  + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getting rows.", "", -1 );
		int rows = rs.getInt( 1 );
		rs.close();
		pstmt.close();
		
		return rows;
	}
	
	private Filter cookFilter( Map map )
	{
		Filter filter = new Filter();
		String[] ss = null;
		
		
		ss = (String [] ) map.get( "total_max" );
		if( ss != null && ss.length >0 ) {
			Values val_total_max = new Values ( ss );
			filter.add(  "t.count_total <= " + val_total_max.toString() );
		}
		
		ss = (String [] ) map.get( "total_min" );
		if( ss != null && ss.length >0 ) {
			Values val_total_min = new Values ( ss );
			filter.add(  "t.count_total >= " + val_total_min.toString() );
		}
		
		ss = (String [] ) map.get( "fresh_no_max" );
		if( ss != null && ss.length >0 ) {
			Values val_fresh_max = new Values ( ss );
			filter.add(  "t.count_fresh0 <= " + val_fresh_max.toString() );
		}
		
		ss = (String [] ) map.get( "fresh_no_min" );
		if( ss != null && ss.length >0 ) {
			Values val_fresh_min = new Values ( ss );
			filter.add(  "t.count_fresh0 >= " + val_fresh_min.toString() );
		}
		
		ss = (String [] ) map.get( "fresh_al_max" );
		if( ss != null && ss.length >0 ) {
			Values val_fresh_max = new Values ( ss );
			filter.add(  "t.count_fresh1 <= " + val_fresh_max.toString() );
		}
		
		ss = (String [] ) map.get( "fresh_al_min" );
		if( ss != null && ss.length >0 ) {
			Values val_fresh_min = new Values ( ss );
			filter.add(  "t.count_fresh1 >= " + val_fresh_min.toString() );
		}
		
		ss = (String [] ) map.get( "confirm_max" );
		if( ss != null && ss.length >0 ) {
			Values val_confirm_max = new Values ( ss );
			filter.add(  "t.count_confirm <= " + val_confirm_max.toString() );
		}
		
		ss = (String [] ) map.get( "confirm_min" );
		if( ss != null && ss.length >0 ) {
			Values val_confirm_min = new Values ( ss );
			filter.add(  "t.count_confirm >= " + val_confirm_min.toString() );
		}
		
		
		return filter;
	}
	
	public Element toElement() throws SQLException, InvalidDataException, IOException { 
		Element elm_cat = new Element("cat");;
		
		Filter filter = cookFilter( map );
		
//		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		Statement stmt = conn.createStatement();
		
		try{
			DbAdm.createTempTable(conn, sql_tmp00);
			DbAdm.createTempTable(conn, sql_tmp01);
			stmt.execute( sql_intmp00 );
			stmt.execute( sql_intmp000 );
			stmt.execute( sql_intmp01 );
			stmt.execute( sql_intmp );
			stmt.execute( sql_update );
		
			int count = this.getCount( filter );
			if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
			String sql_sel = " SELECT t.venderid, v.vendername, t.count_fresh0, t.count_fresh1, t.count_confirm, t.count_total  "
				+ " FROM "+ tmp01 +" t JOIN vender v ON (v.venderid=t.venderid)  ";
		
			String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
			String sql = sql_sel + sql_where ;
	
			PreparedStatement pstmt = conn.prepareStatement( sql );
			ResultSet rs = pstmt.executeQuery();
			XResultAdapter adapter = new XResultAdapter( rs );
			elm_cat = adapter.getRowSetElement( "sheet", "row" );
			int rows = adapter.rows();
			elm_cat.setAttribute( "rows", "" + rows );
			rs.close();
			pstmt.close();
			if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND", 100 );
			
			 
		}
		finally{
			stmt.execute( sql_drp0 );
			stmt.execute( sql_drp1 );
			stmt.close();
		}
		
		return elm_cat;
	}
	
	
	
	public Workbook toBook() throws SQLException, InvalidDataException{
		Workbook book = new Workbook();
		String[] title = { "供应商", "供应商名称", "未阅读单据", "已阅读单据", "已确认单据", "单据总数" };
		Statement stmt = conn.createStatement();
		Filter filter = cookFilter( map );
		try{
			DbAdm.createTempTable(conn, sql_tmp00);
			DbAdm.createTempTable(conn, sql_tmp01);
			stmt.execute( sql_intmp00 );
			stmt.execute( sql_intmp000 );
			stmt.execute( sql_intmp01 );
			stmt.execute( sql_intmp );
			stmt.execute( sql_update );
		
			
		
			String sql_sel = " SELECT t.venderid, v.vendername, t.count_fresh0, t.count_fresh1, t.count_confirm, t.count_total  "
				+ " FROM "+ tmp01 +" t JOIN vender v ON (v.venderid=t.venderid)  ";
			String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
			String sql = sql_sel + sql_where ;
			
			PreparedStatement pstmt = conn.prepareStatement( sql );
			ResultSet rs = pstmt.executeQuery();

			book.addSheet(rs, "cat",title);
			
			rs.close();
			pstmt.close();	
			 
		}
		finally{
			stmt.execute( sql_drp0 );
			stmt.execute( sql_drp1 );
			stmt.close();
		}
		
		
		return book;
	}
	public int cookExcelFile(File file) throws SQLException, InvalidDataException, IOException{
		int rows=0;
		String[] title = { "供应商", "供应商名称", "未阅读单据", "已阅读单据", "已确认单据", "单据总数" };
		Statement stmt = conn.createStatement();
		Filter filter = cookFilter( map );
		try{
			DbAdm.createTempTable(conn, sql_tmp00);
			DbAdm.createTempTable(conn, sql_tmp01);
			stmt.execute( sql_intmp00 );
			stmt.execute( sql_intmp000 );
			stmt.execute( sql_intmp01 );
			stmt.execute( sql_intmp );
			stmt.execute( sql_update );
		
			
		
			String sql_sel = " SELECT t.venderid, v.vendername, t.count_fresh0, t.count_fresh1, t.count_confirm, t.count_total  "
				+ " FROM "+ tmp01 +" t JOIN vender v ON (v.venderid=t.venderid)  ";
			String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
			String sql = sql_sel + sql_where ;
			
			PreparedStatement pstmt = conn.prepareStatement( sql );
			ResultSet rs = pstmt.executeQuery();

			SimpleExcelAdapter excel = new SimpleExcelAdapter( );
			rows = excel.cookExcelFile( file, rs, title, "cat" );
			
			rs.close();
			pstmt.close();	
			 
		}
		finally{
			stmt.execute( sql_drp0 );
			stmt.execute( sql_drp1 );
			stmt.close();
		}
		return rows;
	}
	private Map map = null;
	final private Connection conn;
}

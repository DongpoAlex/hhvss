package com.royalstone.vss.detail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.jdom.Element;

import com.royalstone.util.Day;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.vss.VSSConfig;

/**
 * 
 * 此模块用于下载订货单.
 * @author meng
 *
 */
public class DownloadPurchase 
{

	/**
	 * Constructor 
	 * @param conn		数据库连接
	 * @param venderid	供应商号
	 * @param releasedate	单据上传VSS系统的日期
	 * @throws SQLException	操作数据库时出现意外
	 * @throws InvalidDataException
	 */
	public DownloadPurchase ( Connection conn, String venderid, Day releasedate ) throws SQLException, InvalidDataException 
	{
		this.conn = conn;
		this.venderid = venderid;
		this.releasedate = releasedate;
		Filter filter = cookFilter( );
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );

		String[] arr_sheetid = getSheetid( filter );
		elm_list = new Element( "sheet_set" );
		for( int i=0; i<arr_sheetid.length; i++ ) {
			ShowPurchase sheet = new ShowPurchase( conn, arr_sheetid[i] ,"");
			elm_list.addContent( sheet.toElement() );
		}
		elm_list.setAttribute( "length", "" + arr_sheetid.length );
		elm_list.setAttribute( "releasedate", releasedate.toString() );
	}
	
	/**
	 * 此方法用于查询满足条件的单据号
	 * @param filter
	 * @return	一个字串数组, 包括满足条件的所有单据号.
	 * @throws SQLException
	 */
	private String[] getSheetid ( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT sheetid FROM cat_purchase h " + sql_where ;
//System.out.println( sql );		
//System.out.println();		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		Vector vec_sheet = new Vector();
		while( rs.next() ) {
			String id = rs.getString( 1 );
			id = ( id==null ) ? "" : id.trim();
			vec_sheet.add( id );
		}
		String[] sheets = ( String[] ) vec_sheet.toArray( new String[ vec_sheet.size() ] );
		rs.close();
		pstmt.close();
		return sheets;
	}
	
	/**
	 * 此方法用于统计满足条件的单据数量.
	 * @param filter
	 * @return	满足条件的单据数量
	 * @throws SQLException
	 */
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) FROM cat_purchase h " + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getting rows.", "", -1 );
		int rows = rs.getInt( 1 );

		return rows;
	}

	/**
	 * 根据主调模块提供的参数(供应商码和上传日期)准备过滤条件.
	 * @return
	 */
	private Filter cookFilter( )
	{
		Filter filter = new Filter();
		filter.add( "h.venderid = '" + this.venderid + "' " );
		filter.add( "h.releasedate = '" + releasedate + "' " );
		return filter;
	}
	
	public Element toElement() { return elm_list; }
	
	final private Connection conn;
	private Day releasedate;
	private String venderid;
	private Element elm_list = null;
}

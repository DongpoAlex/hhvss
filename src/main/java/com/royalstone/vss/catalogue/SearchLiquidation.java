package com.royalstone.vss.catalogue;

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

/**
 * 
 * 此模块用于查询对帐申请单目录.
 * @author baijian
 * @param  sheetid	单据号( 支持复选 )
 * @param  bookno	帐套号( 唯一值 )
 * @param  venderid	供应商编码	(唯一值)
 * @param  editor	制单人
 * @param  editdate_min		制单日期
 * @param  editdate_max		制单日期
 *
 */
public class SearchLiquidation 
{

	final static private String sql_sel =  " SELECT " 
        + " lq.sheetid,lq.bookno, b.bookname,lq.flag,lq.note,lq.venderid, "
		+ " lq.editor, to_char(lq.editdate,'yyyy-mm-dd') editdate,lq.checker,lq.checkdate,lq.toucher,lq.touchtime, " 
		+ " g.refsheetid, g.note lognote ";
	
	final static private String sql_join = " FROM "
		+ " liquidation lq "
		+ " JOIN book b ON (lq.bookno=b.bookno) "
		+ " LEFT OUTER JOIN liquidationlog g ON (g.sheetid=lq.sheetid) ";
	
	/**
	 * @param conn
	 * @param map
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public SearchLiquidation ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		Filter filter = cookFilter( map );
		
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = sql_sel + sql_join + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row" ,1, VSSConfig.getInstance().getRowsLimitSoft()  );
		int rows = adapter.rows();
		elm_cat.setAttribute( "count", "" + count );
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "purchase" );
	}
	
	/**
	 * @param filter
	 * @return
	 * @throws SQLException
	 */
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) " + sql_join + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getCount.", "", -1 );
		int rows = rs.getInt( 1 );

		return rows;
	}
	

	/**
	 * @param map
	 * @return
	 * @throws InvalidDataException 
	 */
	private Filter cookFilter( Map map ) throws InvalidDataException
	{
		Filter filter = new Filter();
		String[] ss = null;
        /**
         * 必须首先制定venderid
         */
        ss = (String [] ) map.get( "venderid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  "lq.venderid = "+Values.toString4String(ss[0]));
        }
        
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String [] ) map.get( "sheetid" );
		if( ss != null && ss.length >0 ) {
			Values val_sheetid = new Values ( ss );
			filter.add( "lq.sheetid IN (" + val_sheetid.toString4String() + ") " );
			return filter;
		}
		
		/**
		 * 如果未指定sheetid, 则根据其他条件联合查找.
		 */

		ss = (String [] ) map.get( "bookno" );
		if( ss != null && ss.length >0 ) {
			filter.add(  "lq.bookno = " + Values.toString4String(ss[0])  );
		}
		
		
		/** parse filter on dates
		 */
		ss = ( String [] ) map.get( "editdate_min" );
		if( ss != null && ss.length >0 ) {
			filter.add( "trunc(lq.editdate) >= " + ValueAdapter.std2mdy(ss[0]) );
		}
		
		ss = ( String [] ) map.get( "editdate_max" );
		if( ss != null && ss.length >0 ) {
			filter.add( "trunc(lq.editdate) <= " + ValueAdapter.std2mdy(ss[0]) );
		}
		
		return filter;
	}
	
	public Element toElement() { return elm_cat; }
	
	final private Connection conn;
	private Element elm_cat = null;
}

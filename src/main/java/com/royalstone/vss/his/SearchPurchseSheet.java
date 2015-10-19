package com.royalstone.vss.his;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;


public class SearchPurchseSheet 
{
	public SearchPurchseSheet ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		Filter filter = cookFilter( map );
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		String sql_from =  " SELECT " +
		" p.sheetid, p.refsheetid, sh.shopname destshopname , p.shopid, s.shopname, " +
		" name4code(p.logistics,'logistics') as logisticsname, c.categoryname, " +
		" p.orderdate, p.validdays, p.deadline, p.venderid, " +
		" p.deliverdate, p.delivertimeid, p.note, p.editor, p.checker " +
		" FROM purchase_cdept p " +
		" INNER JOIN shop s ON ( s.shopid=p.shopid  ) " +
		" INNER JOIN shop sh ON ( sh.shopid=p.destshopid ) " +
		" left JOIN category_cdept c ON ( c.categoryid=p.sgroupid ) " ;
		String sql =  sql_from + " WHERE " + filter.toString();
		
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft() );
		int rows = adapter.rows();
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "purchase" );
		rs.close();
		pstmt.close();
	}
	
	private int getCount( Filter filter ) throws SQLException, InvalidDataException
	{
		String sql_count = " SELECT count(*) from purchase_cdept p " 
			+ " WHERE " + filter.toString() ;
		PreparedStatement pstmt = conn.prepareStatement( sql_count );
		ResultSet rs = pstmt.executeQuery();
		int rows = 0;
		if( rs.next() ) rows = rs.getInt( 1 );
		rs.close();
		pstmt.close();
		return rows;
	}
	
	

	private Filter cookFilter( Map map ) throws InvalidDataException
	{
		Filter filter = new Filter();
		String[] ss = null;
		
		ss = (String [] ) map.get( "venderid" );
		if( ss != null && ss.length >0 && ss[0] != null && ss[0].length()>0 ) {
			Values val_vender = new Values ( ss );
			filter.add(  "p.venderid IN (" + val_vender.toString4String() + ") " );
		}
		
		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String [] ) map.get( "sheetid" );
		if( ss != null && ss.length >0 && ss[0] != null && ss[0].length()>0 ) {
			Values val_sheetid = new Values ( ss );
			filter.add( "p.sheetid IN (" + val_sheetid.toString4String() + ") " );
			return filter;
		}
		
		/**
		 * 订货审批单id
		 */
		ss = (String [] ) map.get( "refsheetid" );
		if( ss != null && ss.length >0 && ss[0] != null && ss[0].length()>0 ) {
			Values val_sheetid = new Values ( ss );
			filter.add( "p.refsheetid IN (" + val_sheetid.toString4String() + ") " );
		}
		
		/**
		 * 根据订货地过滤
		 */
		ss = (String [] ) map.get( "shopid" );
		if( ss != null && ss.length >0 ) {
			Values val_shopid = new Values ( ss );
			filter.add(  "p.shopid IN (" + val_shopid.toString4String() + ") " );
		}
		
		/**
		 * 根据收货地过滤
		 */
		ss = (String [] ) map.get( "destshopid" );
		if( ss != null && ss.length >0 ) {
			Values val_shopid = new Values ( ss );
			filter.add(  "p.destshopid IN (" + val_shopid.toString4String() + ") " );
		}
		
	
		ss = ( String [] ) map.get( "editdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.editdate) >= " + ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "editdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.editdate) <= " + ValueAdapter.std2mdy(date));
		}
		
		ss = ( String [] ) map.get( "checkdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.checkdate) >= " + ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "checkdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.checkdate) <= " + ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "deadline_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.deadline) >= " + ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "deadline_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.deadline) <= " + ValueAdapter.std2mdy(date) );
		}

		ss = ( String [] ) map.get( "orderdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.orderdate) >= " + ValueAdapter.std2mdy(date) );
		}
		
		ss = ( String [] ) map.get( "orderdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(p.orderdate) <= " + ValueAdapter.std2mdy(date) );
		}


		
		return filter;
	}
	
	public Element toElement() { return elm_cat; }

	final private Connection conn;
	private Element elm_cat = null;
}

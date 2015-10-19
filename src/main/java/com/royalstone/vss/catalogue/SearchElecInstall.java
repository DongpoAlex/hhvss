package com.royalstone.vss.catalogue;

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

/**
 * 
 * 此模块用于查询电器按装单目录.
 * @author bai
 *
 */
public class SearchElecInstall 
{
	final static private String sql_sel =  
		" SELECT ei.sheetid,ei.refsheetid,ei.flag,ei.shopid,s.shopname,ei.saletime,ei.customer, " +
		" ei.deliverdate,ei.venderid,ei.serverid,ei.isread,ei.note, " +
		" ei.editor,ei.operator,ei.editdate,ei.checker, " +
		" ei.checkdate,cat.status ";
	
	final static private String sql_join = " FROM cat_elecinstall cat " +
	" INNER JOIN elecinstall ei ON ( cat.sheetid=ei.sheetid ) " +
			" INNER JOIN shop s ON ( s.shopid=ei.shopid ) ";
	
	public SearchElecInstall ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		this.map = map;

	}
	
	private int getCount( Filter filter ) throws SQLException
	{
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = " SELECT count(*) " + sql_join + sql_where ;
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "Failed in getting rows.", "", -1 );
		int rows = rs.getInt( 1 );

		rs.close();
		pstmt.close();
		return rows;
	}
	

	private Filter cookFilter( Map map ) throws InvalidDataException
	{
		Filter filter = new Filter();
		String[] ss = null;
		
		ss = (String [] ) map.get( "venderid" );
		if( ss != null && ss.length >0 ) {
			Values val_vender = new Values ( ss );
			filter.add( "cat.serverid IN (" + val_vender.toString4String() + ") " );
		}
		
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String [] ) map.get( "sheetid" );
		if( ss != null && ss.length >0 ) {
			Values val_sheetid = new Values ( ss );
			filter.add( "cat.sheetid IN (" + val_sheetid.toString4String() + ") " );
			return filter;
		}
		

		
		ss = (String [] ) map.get( "shopid" );
		if( ss != null && ss.length >0 ) {
			Values val_house = new Values ( ss );
			filter.add( "ei.shopid IN (" + val_house.toString4String() + ") " );
		}
		
		
		ss = ( String [] ) map.get( "editdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "ei.editdate >= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "editdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "ei.editdate <= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "ei.checkdate >= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "ei.checkdate <= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "status" );
		if( ss != null && ss.length >0 ) {
			Values v = new Values ( ss );
			filter.add( "cat.status IN ( " + v.toString() + " ) "  );
		}
		
		return filter;
	}
	
	public Element toElement() throws InvalidDataException, SQLException {
		Filter filter = cookFilter( map );
		
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		String sql = sql_sel + sql_join + sql_where +" ORDER BY ei.sheetid DESC";
	
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft() );
		elm_cat.setAttribute( "count", "" + count );
		rs.close();
		pstmt.close();
		return elm_cat;
		
	}
	
	final private Connection conn;
	private Element elm_cat = null;
	final private Map map;
}
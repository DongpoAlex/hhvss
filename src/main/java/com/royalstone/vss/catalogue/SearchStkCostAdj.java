/*
 * Created on 2007-02-01
 *
 */
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
import com.royalstone.util.excel.Workbook;
import com.royalstone.vss.VSSConfig;

public class SearchStkCostAdj {

	private String sql_search =  " SELECT "
		+ " s.sheetid, s.shopid, sh.shopname, s.finalshopid,ss.shopname finalshopname,"
		+ " s.venderid, v.vendername, pay.paytypename ,s.TotalAmt, " 
		+ " (s.TotalAmt+s.TotalTaxAmt17+s.TotalTaxAmt13) as totaltaxamt, "
		+ " s.TotalAmt17, s.TotalTaxAmt17, s.TotalTaxAmt13, "
		+ " s.editor, s.editdate, s.checker, s.checkdate, s.note " 
		+ " FROM stkcostadj s "
		+ " JOIN vender v ON ( v.venderid = s.venderid ) "
		+ " JOIN paytype pay ON ( pay.paytypeid = s.paytypeid ) "
		+ " JOIN shop sh ON ( sh.shopid = s.shopid ) "
		+ " LEFT JOIN shop ss ON ( ss.shopid = s.finalshopid ) ";

	public SearchStkCostAdj ( Connection conn, Map map ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		this.map = map;
	}
	
	private int getCount( Filter filter ) throws SQLException, InvalidDataException
	{
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		String sql_count = " SELECT count( * ) "
			+ " FROM stkcostadj s  "
			+ " WHERE " + filter.toString() ;
		PreparedStatement pstmt = conn.prepareStatement( sql_count );
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
		if( ss != null && ss.length >0 && ss[0] != null && ss[0].length()>0 ) {
			Values val_vender = new Values ( ss );
			filter.add(  "s.venderid IN (" + val_vender.toString4String() + ") " );
		}
		
		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String [] ) map.get( "sheetid" );
		if( ss != null && ss.length >0 && ss[0] != null && ss[0].length()>0 ) {
			Values val_sheetid = new Values ( ss );
			filter.add( "s.sheetid IN (" + val_sheetid.toString4String() + ") " );
			return filter;
		}
		
		
		/**
		 * 根据订货地过滤
		 */
		ss = (String [] ) map.get( "shopid" );
		if( ss != null && ss.length >0 ) {
			Values val_shopid = new Values ( ss );
			filter.add(  "s.shopid IN (" + val_shopid.toString4String() + ") " );
		}
			
		ss = ( String [] ) map.get( "editdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "s.editdate >= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "editdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "s.editdate <= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_min" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(s.checkdate) >= " + ValueAdapter.std2mdy(date)  );
		}
		
		ss = ( String [] ) map.get( "checkdate_max" );
		if( ss != null && ss.length >0 ) {
			String date = ss[0];
			filter.add( "trunc(s.checkdate) <= " + ValueAdapter.std2mdy(date)  );
		}
				
		return filter;
	}
	
	
	
	public Element toElement() throws InvalidDataException, SQLException {
		Element elm_cat = null;
		Filter filter = cookFilter(map);
		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getRowsLimitHard())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = this.sql_search + " where " + filter.toString()+" and ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft();

		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement(sql);

		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft());

		int rows = adapter.rows();
		elm_cat.setAttribute( "count", "" + count );
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("sheetname", "stkcostadj");
		rs.close();
		pstmt.close();
		return elm_cat;
	}
	
	public Workbook toBook(){
		Workbook book = new Workbook();
		
		
		return book;
	}
	
	final private Connection conn;
	final Map map;
	
}

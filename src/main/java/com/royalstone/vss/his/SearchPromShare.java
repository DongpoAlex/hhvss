/*
 * Created on 2007-02-01
 *
 */
package com.royalstone.vss.his;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

/**
 * @author baibai
 * 促销明细查询
 */
public class SearchPromShare {

	private String sql_search;

	public SearchPromShare ( Connection conn, Map map,String month ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		this.map = map;
		this.month = month;
		
		sql_search =  " SELECT /*+ index(p) index(pi) index(g) index(cat)*/  " 
			+ " p.venderid, p.shopid, s.shopname, p.sdate, pi.categoryid, cat.categoryname, pay.paytypename, "
			+ " pi.goodsid, g.barcode, g.goodsname, pi.qty, pi.pricev, pi.discpricev, pi.costv, " 
			+ " pi.disccostv, pi.saletaxrate, pi.costtaxrate,status4prom(pi.disctype) disctype "
			+ " FROM promshare"+this.month+" p "
			+ " INNER JOIN shop s ON ( s.shopid=p.shopid ) "
			+ " INNER JOIN promshareitem"+this.month+" pi ON ( p.sheetid=pi.sheetid ) " 
			+ " INNER JOIN paytype pay ON ( pay.paytypeid=p.paytypeid ) "
			+ " INNER JOIN goods_cdept g ON ( g.goodsid=pi.goodsid ) "
			+ " left JOIN category_cdept cat ON ( cat.categoryid=pi.categoryid ) ";
	}
	
	private int getCount( Filter filter ) throws SQLException, InvalidDataException
	{
		if( filter.count() == 0 ) throw new InvalidDataException( "请设置查询过滤条件." );
		String sql = " SELECT /*+ index(p) index(pi) index(g) index(cat)*/ count( * ) "
			+ " FROM promshare"+this.month+" p  " +
			" INNER JOIN promshareitem"+this.month+" pi ON (p.sheetid = pi.sheetid) " +
			" left JOIN category_cdept cat ON (cat.categoryid = pi.categoryid) " +
			" WHERE " + filter.toString() ;
		
		Log.debug(this.getClass().getName(), sql);
		
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
		/**
		 * 过滤条件: venderid
		 */
		{
			String[] ss  = (String [] ) map.get( "venderid" );
			if( ss != null && ss.length >0 && ss[0] != null && ss[0].length()>0 ) {
				Values val_vender = new Values ( ss );
				filter.add(  "p.venderid IN (" + val_vender.toString4String() + ") " );
			}
			
			ss  = ( String [] ) map.get( "sdate_min" );
			if( ss != null && ss.length >0 ) {
				filter.add( " (p.sdate) >= " + ValueAdapter.std2mdy(ss[0]) );
			}
			
			ss = ( String [] ) map.get( "sdate_max" );
			if( ss != null && ss.length >0 ) {
				filter.add( " (p.sdate) <= " + ValueAdapter.std2mdy(ss[0]) );
			}
			
			ss  = (String [] ) map.get( "shopid" );
			if( ss != null && ss.length >0 ) {
				Values val_shopid = new Values ( ss );
				filter.add(  "p.shopid IN (" + val_shopid.toString4String() + ") " );
			}
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
		
		String sql = this.sql_search + " WHERE " + filter.toString()+" and ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft();
		
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("promshare", "row", 1, VSSConfig.getInstance().getRowsLimitSoft());

		int rows = adapter.rows();
		elm_cat.setAttribute("count", "" + count);
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("sheetname", "promshare");
		rs.close();
		pstmt.close();
		return elm_cat;
	}
	
	
	/**
	 * 此方法把查询结果输出到一个EXCEL兼容的XML文件中
	 * @param map
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException
	 */
	public int cookExcelFile( Map map, File file ) throws SQLException, InvalidDataException, IOException
	{
		String[] title ={"供应商","门店编码", "门店名称", "商品编码","商品条码","商品名称", "销售数量",
				"成本金额", "促销成本", "促销类型", "品类编码", "品类名称","销售日期"};
		
		String sql_sel = " SELECT /*+ index(p) index(pi) index(g) index(cat)*/ " 
			+ " p.venderid, p.shopid, s.shopname, pi.goodsid, g.barcode, g.goodsname, pi.qty, " 
			+ " pi.costv, pi.disccostv, status4prom(pi.disctype) disctype , pi.categoryid, cat.categoryname, p.sdate"
			+ " FROM promshare"+this.month+" p "
			+ " INNER JOIN promshareitem"+this.month+" pi ON ( p.sheetid=pi.sheetid ) " 
			+ " INNER JOIN paytype pay ON ( pay.paytypeid=p.paytypeid ) "
			+ " left JOIN category_cdept cat ON ( cat.categoryid=pi.categoryid ) "
			+ " INNER JOIN shop s ON ( s.shopid=p.shopid ) "
			+ " INNER JOIN goods_cdept g ON ( g.goodsid=pi.goodsid ) ";
		
		Filter filter = cookFilter(map);
		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");
		
		int count = this.getCount( filter );
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) {
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");
		}
		
		sql_sel += " WHERE " + filter.toString();
		
		PreparedStatement pstmt = conn.prepareStatement(sql_sel);
		ResultSet rs = pstmt.executeQuery();
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		int rows = excel.cookExcelFile( file, rs, title, "促销明细" );
		rs.close();
		pstmt.close();
		return rows;
	}
	
	final private Connection conn;
	final Map map;
	final private String month;//对应月表
}

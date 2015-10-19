package com.royalstone.vss.report;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.excel.Workbook;
import com.royalstone.vss.VSSConfig;

/**
 * 供应商供货商品清单-明细
 * @author bai
 *
 */
public class VenderGoodsShop {

	public VenderGoodsShop(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 获得符合查询条件的xml格式文档
	 * @param map 查询条件
	 * @return xml格式文档
	 * @throws SQLException
	 * @throws InvalidDataException 
	 */
	public Element getGoodsShop( Map map ) throws SQLException, InvalidDataException{
		Element elm_data = null;
		
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		
		int count = getCount(sql_where);
		if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		String sql = sql_sel + sql_where + sql_order;
		
		Log.debug(this.getClass().getName(), sql);
		
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_data = adapter.getRowSetElement( "report", "row" ,1, VSSConfig.getInstance().getRowsLimitSoft()  );
		rs.close();
		pstmt.close();
		
		int rows = adapter.rows();
		elm_data.setAttribute("count", "" + count);
		elm_data.setAttribute( "rows", "" + rows );
		elm_data.setAttribute( "reportname", "goods_shop" );
		
		return elm_data;
	}
	
	/**
	 * 查询符合查询条件的数目
	 * @param sql_where 查询条件
	 * @return 符合查询条件的数目
	 * @throws SQLException
	 */
	private int getCount(String sql_where ) throws SQLException{
		int count =0 ;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_count + sql_where);
		if( rs.next() ) count = rs.getInt(1);
		rs.close();
		stmt.close();

		return count;
	}
	
	/**
	 * @param map
	 * @return
	 * @throws InvalidDataException 
	 */
	private Filter cookFilter( Map map ) throws InvalidDataException{
		Filter filter = new Filter();
		String[] ss = null;

        ss = (String [] ) map.get( "venderid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " v.venderid = "+Values.toString4String(ss[0]));
        }
        
        ss = (String [] ) map.get( "goodsid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " v.goodsid IN ("+new Values(ss).toString4String()+") ");
        }
        
        ss = (String [] ) map.get( "shopid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " v.shopid IN ("+new Values(ss).toString4String()+ ") ");
        }
        
        //TODO 只显示默认供应商
        filter.add("v.venderid=v.venderid_first");
        
        return filter;
	}
	
	/**
	 * 将符合查询条件的结果转成Excel 可识别的xml格式
	 * @param map
	 * @return
	 * @throws SQLException 
	 * @throws InvalidDataException 
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public Workbook getGoodsShopBook( Map map ) throws SQLException, InvalidDataException, IOException, RowsExceededException, WriteException{

		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft() : " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft()+" and " + filter.toString() ;
//		int count = getCount(sql_where);
//		if( count > VSSConfig.getInstance().getExcelLimitSoft() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
//
		Workbook book = new Workbook();
		String sql_sel_book = 
			" SELECT " +
			" v.venderid, v.shopid,s.shopname, g.barcode,v.goodsid, g.goodsname, c.categoryid, c.categoryname, " +
			" g.unitname, g.spec, status4goods(v.GoodsStatus) goodsstatus, " +
			" name4code(v.logistics,'logistics') as logistics, v.CostTaxRate " +
			" FROM vender_goods_shop v " +
			" LEFT JOIN goods g ON (g.goodsid = v.goodsid ) " +
			" INNER JOIN shop s ON (s.shopid = v.shopid ) " +
			" INNER JOIN shop ss ON ( ss.shopid = v.dcshopid ) " +
			" left  JOIN category c ON ( c.categoryid = g.deptid) " ;
		
		String[] title = {"供应商","门店编码","门店名称", "商品条码","商品编码","商品名称","小类编码", "小类名称", 
				"单位", "规格", "商品状态","物流模式", "进项税率"};
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql_sel_book + sql_where + sql_order);
		book.addSheet(rs, "第一页", title);
		rs.close();
		stmt.close();
		
		return book;
	}
	
	/**
	 * 此方法把查询结果输出到一个EXCEL兼容的XML文件中
	 * @author mengluoyi	2008-03-29
	 * @param map
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public int cookExcelFile( Map map, File file ) throws SQLException, InvalidDataException, IOException, RowsExceededException, WriteException
	{
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		int count = getCount(sql_where);
		if( count > SimpleExcelAdapter.ROWS_LIMIT ) 
			throw new InvalidDataException( "满足条件的记录数"+count+"，已超过系统处理上限"+SimpleExcelAdapter.ROWS_LIMIT+"，请重新设置查询条件." );

		String sql_sel_book = 
			" SELECT " + 
			" v.venderid, v.shopid,s.shopname, g.barcode,v.goodsid, g.goodsname, c.categoryid, c.categoryname, " +
			" g.unitname, g.spec, status4goods(v.GoodsStatus) goodsstatus, " +
			" name4code(v.logistics,'logistics') as logistics, v.CostTaxRate " +
			" FROM vender_goods_shop v " +
			" INNER JOIN shop s ON (s.shopid = v.shopid ) " +
			" INNER JOIN shop ss ON ( ss.shopid = v.dcshopid ) " +
			" INNER  JOIN goods g ON (g.goodsid = v.goodsid ) " +
			" left  JOIN category c ON ( c.categoryid = g.deptid) " ;
		
		String[] title = {"供应商","门店编码","门店名称", "商品条码","商品编码","商品名称","小类编码", "小类名称", 
				"单位", "规格", "商品状态","物流模式", "进项税率"};
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql_sel_book + sql_where + sql_order);
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "供应商-门店-商品" );
		rs.close();
		stmt.close();
		return rows;
	}
	
	
	final private String sql_sel = 
		" SELECT  v.goodsid, v.shopid, v.venderid, v.venderid_first, status4goods(v.GoodsStatus) goodsstatus, " +
		" name4code(v.logistics,'logistics') as logistics, ss.shopname dcshop, v.GrossCost, v.ContractCost, " +
		" v.CostTaxRate, v.DeductRate, v.orderflag, v.retflag, v.touchtime, v.toucher, " +
		" g.goodsname, s.shopname, g.barcode, g.spec, g.unitname, c.categoryname, c.categoryid " +
		" FROM vender_goods_shop v " +
		" INNER JOIN shop s ON (s.shopid = v.shopid ) " +
		" INNER JOIN shop ss ON ( ss.shopid = v.dcshopid ) " +
		" INNER JOIN goods g ON (g.goodsid = v.goodsid ) " +
		" left  JOIN category c ON ( c.categoryid = g.deptid) " ;
		
	final private String sql_count = " SELECT COUNT(*) FROM vender_goods_shop v ";
	final private String sql_order = " order by v.shopid, v.goodsid ";
	final private Connection conn;
}

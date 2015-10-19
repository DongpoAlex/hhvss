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
 * 最新供应商门店商品库存
 * @author baibai
 *
 */
public class CurrtentInventory {

	public CurrtentInventory(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 获得符合查询条件的xml格式文档
	 * @param map 查询条件
	 * @return xml格式文档
	 * @throws SQLException
	 * @throws InvalidDataException 
	 */
	public Element getCurrtentInventory( Map map ) throws SQLException, InvalidDataException{
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
		elm_data.setAttribute( "reportname", "current_inventory" );
		
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
	 */
	private Filter cookFilter( Map map ){
		Filter filter = new Filter();
		String[] ss = null;

        ss = (String [] ) map.get( "venderid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " c.venderid = "+Values.toString4String(ss[0]));
        }
        
        ss = (String [] ) map.get( "goodsid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " c.goodsid IN ("+new Values(ss).toString4String()+") ");
        }
        
        ss = (String [] ) map.get( "shopid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " c.shopid IN ("+new Values(ss).toString4String()+ ") ");
        }
        
        return filter;
	}
	
	/**
	 * 将符合查询条件的结果转成Excel 可识别的xml格式
	 * @param map
	 * @return
	 * @throws SQLException 
	 * @throws InvalidDataException 
	 */
	public Workbook getCurrtentInventoryBook( Map map ) throws SQLException, InvalidDataException{
		
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft() : " where ROWNUM<="+VSSConfig.getInstance().getRowsLimitSoft()+" and " + filter.toString() ;
		//int count = getCount(sql_where);
		//if( count > VSSConfig.getInstance().getRowsLimitHard() ) throw new InvalidDataException( "满足条件的记录数已超过系统处理上限,请重新设置查询条件." );
		
		Workbook book = new Workbook();
		String[] title = {"供应商", "门店编码","门店名称", "商品编码","商品条码","商品名称","库存数量","发布日期" };
		final String sql_sel_book = 
			" SELECT "+
			" c.venderid, c.shopid, s.shopname, c.goodsid, g.barcode, g.goodsname, c.qty, c.releasedate " +
			" FROM current_inventory c " +
			" INNER JOIN shop s ON (s.shopid = c.shopid ) " +
			" INNER JOIN goods g ON (g.goodsid = c.goodsid  ) " ;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql_sel_book + sql_where + sql_order );
		book.addSheet(rs, "current_inventory", title);
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
	 */
	public int cookExcelFile( Map map, File file ) throws SQLException, InvalidDataException, IOException
	{
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		int count = getCount(sql_where);
		if( count > SimpleExcelAdapter.ROWS_LIMIT ) 
			throw new InvalidDataException( "满足条件的记录数"+count+"，已超过系统处理上限"+SimpleExcelAdapter.ROWS_LIMIT+"，请重新设置查询条件." );
		
		String[] title = { "供应商", "门店编码","门店名称", "商品编码","商品条码","商品名称","库存数量","发布日期" };
		final String sql_sel_book = 
			" SELECT " + 
			" c.venderid, c.shopid, s.shopname, c.goodsid, g.barcode, g.goodsname, c.qty, c.releasedate " +
			" FROM current_inventory c " +
			" INNER JOIN shop s ON (s.shopid = c.shopid ) " +
			" INNER JOIN goods g ON (g.goodsid = c.goodsid  ) " ;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql_sel_book + sql_where + sql_order );
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "最新库存" );
		rs.close();
		stmt.close();
		return rows;
	}
	
	public void getJxlExcel(Map map, File file) throws SQLException, InvalidDataException, RowsExceededException, WriteException, IOException{
		Filter filter = cookFilter( map );
		String sql_where = ( filter.count() == 0 )? "" : " WHERE " + filter.toString() ;
		int count = getCount(sql_where);
		if( count > SimpleExcelAdapter.ROWS_LIMIT ) 
			throw new InvalidDataException( "满足条件的记录数"+count+"，已超过系统处理上限"+SimpleExcelAdapter.ROWS_LIMIT+"，请重新设置查询条件." );
		
		String[] title = { "供应商", "门店编码","门店名称", "商品编码","商品条码","商品名称","库存数量","发布日期" };
		final String sql_sel_book = 
			" SELECT " + 
			" c.venderid, c.shopid, s.shopname, c.goodsid, g.barcode, g.goodsname, c.qty, c.releasedate " +
			" FROM current_inventory c " +
			" INNER JOIN shop s ON (s.shopid = c.shopid ) " +
			" INNER JOIN goods g ON (g.goodsid = c.goodsid  ) " ;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql_sel_book + sql_where + sql_order );
		com.royalstone.workbook.Workbook.writeToFile(file, rs, title, "门店商品清单");
		rs.close();
		stmt.close();
	}
	
	final private String sql_sel = 
		" SELECT c.venderid,c.venderid, c.goodsid, c.shopid, c.qty, c.grosscostvalue, " +
		" c.taxcostvalue, c.releasedate, " +
		" s.shopname, g.goodsname, g.barcode, g.spec, g.unitname " +
		" FROM current_inventory c " +
		" INNER JOIN shop s ON (s.shopid = c.shopid ) " +
		" INNER JOIN goods g ON (g.goodsid = c.goodsid  ) " ;
	
	final private String sql_order = " order by c.shopid, c.goodsid ";
	final private String sql_count = " SELECT COUNT(*) FROM current_inventory c ";
	final private Connection conn;
}

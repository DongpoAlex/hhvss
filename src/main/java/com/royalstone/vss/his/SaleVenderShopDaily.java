package com.royalstone.vss.his;

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
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

/**
 * 供应商商品销售日报
 * 
 * @author bai
 * 
 */
public class SaleVenderShopDaily {

	public SaleVenderShopDaily(Connection conn,Map map) throws InvalidDataException {
		this.conn = conn;
		this.map = map;
		//this.tableName = cookTableName();
		this.tableName="sale_vshop_daily_cdept";
	}

	/**
	 * 获得符合查询条件的xml格式文档
	 * 
	 * @param map
	 *            查询条件
	 * @return xml格式文档
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public Element getSaleDaily() throws SQLException, InvalidDataException {
		Element elm_data = null;

		String sql_sel = " SELECT d.venderid, d.sdate, d.goodsid, d.shopid, d.qty, d.categoryid, " 
				+ " d.grosscostvalue, d.netcostvalue, d.grosssalevalue,"
				+ " d.netsalevalue, d.SaleTaxRate, d.salecostrate, d.touchtime, d.toucher, "
				+ " g.goodsname, s.shopname, g.barcode, g.spec, g.unitname, c.categoryname " 
				+ " FROM " + tableName + " d "
				+ " INNER JOIN shop s ON (s.shopid = d.shopid ) " 
				+ " left  JOIN category_cdept c ON ( c.categoryid = d.categoryid) "
				+ " INNER JOIN goods_cdept g ON (g.goodsid = d.goodsid ) ";

		Filter filter = cookFilter();
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		int count = getCount(sql_where);
		if (count > VSSConfig.getInstance().getRowsLimitHard())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = sql_sel + sql_where + sql_order;

		Log.debug(this.getClass().getName(), sql);
		
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_data = adapter.getRowSetElement("report", "row", 1, VSSConfig.getInstance().getRowsLimitSoft());
		rs.close();
		pstmt.close();

		int rows = adapter.rows();
		elm_data.setAttribute("count", "" + count);
		elm_data.setAttribute("rows", "" + rows);
		elm_data.setAttribute("reportname", "sale_daily");

		return elm_data;
	}

	/**
	 * 查询符合查询条件的数目
	 * 
	 * @param sql_where
	 *            查询条件
	 * @return 符合查询条件的数目
	 * @throws SQLException
	 */
	private int getCount( String sql_where ) throws SQLException {
		int count = 0;
		String sql_count = " SELECT COUNT(*) FROM "+this.tableName+" d ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_count + sql_where);
		if (rs.next())
			count = rs.getInt(1);
		rs.close();
		stmt.close();

		return count;
	}

	/**
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 */
	private Filter cookFilter() throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" d.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("goodsid");
		if (ss != null && ss.length > 0) {
			filter.add(" d.goodsid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			filter.add(" d.shopid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("sdate");
		if (ss != null && ss.length > 0) {
			filter.add(" d.sdate = " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("min_sdate");
		if (ss != null && ss.length > 0) {
			filter.add(" d.sdate >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("max_sdate");
		if (ss != null && ss.length > 0) {
			filter.add(" d.sdate <= " + ValueAdapter.std2mdy(ss[0]));
		}

		return filter;
	}

	/**
	 * 此方法把查询结果输出到一个EXCEL兼容的XML文件中
	 * 
	 * @author mengluoyi 2008-03-29
	 * @param map
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException
	 */
	public int cookExcelFile(File file ) throws SQLException, InvalidDataException, IOException {
		Filter filter = cookFilter();
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		int count = getCount(sql_where);
		if (count > SimpleExcelAdapter.ROWS_LIMIT)
			throw new InvalidDataException("满足条件的记录数" + count + "，已超过系统处理上限" + SimpleExcelAdapter.ROWS_LIMIT + "，请重新设置查询条件.");

		String sql_sel_book = " SELECT d.venderid, d.shopid, s.shopname, d.goodsid, g.barcode, g.goodsname, d.qty,"
				+ " d.grosscostvalue, grosssalevalue,d.categoryid, c.categoryname, d.sdate " 
				+ " FROM " + tableName + " d "
				+ " INNER JOIN shop s ON (s.shopid = d.shopid ) " 
				+ " left  JOIN category_cdept c ON ( c.categoryid = d.categoryid) "
				+ " INNER JOIN goods_cdept g ON (g.goodsid = d.goodsid ) ";

		String[] title = { "供应商", "门店编码", "门店名称", "商品编码", "商品条码", "商品名称", "销售数量", "成本金额","销售金额", "小类编码", "小类名称", "销售日期" };

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_sel_book + sql_where + sql_order);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		int rows = excel.cookExcelFile(file, rs, title, "销售日报");
		rs.close();
		stmt.close();

		return rows;
	}

	public int cookExcelFile4OLE(File file ) throws SQLException, InvalidDataException, IOException {
		Filter filter = cookFilter();
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		int count = getCount(sql_where);
		if (count > SimpleExcelAdapter.ROWS_LIMIT)
			throw new InvalidDataException("满足条件的记录数" + count + "，已超过系统处理上限" + SimpleExcelAdapter.ROWS_LIMIT + "，请重新设置查询条件.");

		String sql_sel_book = " SELECT d.venderid, d.shopid, s.shopname, d.goodsid, g.barcode, g.goodsname, d.qty,"
				+ " d.grosscostvalue,d.categoryid, c.categoryname, d.sdate " 
				+ " FROM " + tableName + " d "
				+ " INNER JOIN shop s ON (s.shopid = d.shopid ) " 
				+ " left  JOIN category_cdept c ON ( c.categoryid = d.categoryid) "
				+ " INNER JOIN goods_cdept g ON (g.goodsid = d.goodsid ) ";

		String[] title = { "供应商", "门店编码", "门店名称", "商品编码", "商品条码", "商品名称", "销售数量", "成本金额", "小类编码", "小类名称", "销售日期" };

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_sel_book + sql_where + sql_order);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		int rows = excel.cookExcelFile(file, rs, title, "销售日报");
		rs.close();
		stmt.close();

		return rows;
	}
	static final private String sql_order = " order by d.shopid, d.goodsid ";
	final private String tableName;
	final private Connection conn;
	final Map map;
}

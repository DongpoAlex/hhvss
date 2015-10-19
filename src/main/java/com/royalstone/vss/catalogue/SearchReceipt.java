package com.royalstone.vss.catalogue;

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
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 
 * 此模块用于查询订货单目录.
 * 
 * @author baijian
 * 
 */
public class SearchReceipt {
	final static private String[] title ={"供应商编码","供应商","分公司编码","分公司", "门店编码", "门店名称", 
		"课类编码","课类名称","物流模式","结算方式","发生日期","单据编码","金额（含税）","状态","备注"};
	final static private String sql_sel = " SELECT "
			+ " v.venderid,v.vendername,u.bookno,b.bookname,u.shopid,s.shopname, " 
			+ " u.majorid,c.categoryname,name4code(u.logisticsid,'logistics') as logisticsid, " +
			" pt.paytypename,u.docdate,u.sheetid,(u.ounpaidamt+u.otaxamt17+u.otaxamt13) as unpaidamt, " +
			" CASE " +
			" WHEN (vs.bookno=u.bookno and vs.payflag<>0) or u.payflag=0 THEN '冻结' " +
			" WHEN u.payflag=1 THEN '正常' " +
			" END AS payflag, "
			+ " CASE "
			+ " WHEN vs.bookno=u.bookno and vs.payflag<>0 THEN '高库存,暂缓结算,具体请与相关采购沟通' " 
			+ " WHEN u.payflag=0 THEN '高库存,暂缓结算,具体请与相关采购沟通' "
			+ " WHEN u.payflag=1 THEN '待结算' " + " WHEN u.payflag=3 THEN '已进入付款流程，待付款' " 
			+ " ELSE '' " 
			+ " END AS note ";

	final static private String sql_join = " FROM unpaidsheet0 u "
			+ " INNER JOIN vender v ON ( v.venderid=u.venderid ) "
			+ " INNER JOIN book b ON ( b.bookno=u.bookno ) " 
			+ " INNER JOIN shop s ON ( s.shopid=u.shopid ) " 
			+ " INNER JOIN paytype pt ON ( pt.paytypeid=u.paytypeid)"
			+ " LEFT JOIN category c ON ( c.categoryid=u.majorid) " 
			+ " LEFT JOIN venderpaystatus vs on(vs.venderid=u.venderid and vs.bookno=u.bookno) ";

	public SearchReceipt(Connection conn, Map map) throws SQLException, InvalidDataException {
		this.conn = conn;
		this.map = map;
	}

	private int getCount(Filter filter) throws SQLException {
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();
		String sql = " SELECT count(*) " + sql_join + sql_where;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("Failed in getting rows.", "", -1);
		int rows = rs.getInt(1);
		rs.close();
		pstmt.close();

		return rows;
	}

	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		String[] venderid = null;
		venderid = (String[]) map.get("venderid");
		if (venderid != null && venderid.length > 0) {
			Values val_vender = new Values(venderid);
			filter.add("u.venderid IN (" + val_vender.toString4String() + ") ");
		}
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String[]) map.get("sheetid");
		if ((ss != null && ss.length > 0) && (venderid != null && venderid.length > 0)) {
			Values val_sheetid = new Values(ss);
			filter.add("u.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		ss = (String[]) map.get("bookno");
		if (ss != null && ss.length > 0) {
			Values val_house = new Values(ss);
			filter.add("u.bookno IN (" + val_house.toString4String() + ") ");
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_editor = new Values(ss);
			filter.add("u.shopid IN (" + val_editor.toString4String() + ") ");
		}
		
		ss = (String[]) map.get("majorid");
		if (ss != null && ss.length > 0) {
			Values val_editor = new Values(ss);
			filter.add("u.majorid IN (" + val_editor.toString4String() + ") ");
		}
		
		ss = (String[]) map.get("docdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(u.docdate) >= " + ValueAdapter.std2mdy(date));
		}
		
		ss = (String[]) map.get("docdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(u.docdate) <= " + ValueAdapter.std2mdy(date));
		}

		//TODO 华南限制，必须是08年3月1号前的数据
		
		//filter.add("u.docdate >= mdy('03','01','2008')" );
		
		ss = (String[]) map.get("checkdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(u.docdate) <= " + ValueAdapter.std2mdy(date));
		}

		filter.add("u.sheettype=2301");
		filter.add("u.payflag in (0,1)");
		
		return filter;
	}

	public Element toElement() throws SQLException, InvalidDataException, IOException {
		Element elm_cat = null;
		
		Filter filter = cookFilter(map);

		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getRowsLimitHard())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();
		String sql = SqlUtil.toLocal(sql_sel) + sql_join + sql_where;
		
		Log.debug(this.getClass().getName(), sql);
		
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("catalogue", "row");

		int rows = adapter.rows();
		elm_cat.setAttribute( "count", "" + count );
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("sheetname", "receipt");
		rs.close();
		pstmt.close();
		
		return elm_cat;
	}

	
	public int cookExcelFile(File file) throws SQLException, InvalidDataException, IOException{
		Filter filter = cookFilter(map);

		if (filter.count() == 0)
			throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getExcelLimitSoft())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();
		String sql = SqlUtil.toLocal(sql_sel) + sql_join + sql_where;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "验收单" );
		rs.close();
		pstmt.close();
		return rows;
	}
	final private Connection conn;
	final private Map map;
}

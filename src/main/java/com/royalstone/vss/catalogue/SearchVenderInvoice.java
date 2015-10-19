package com.royalstone.vss.catalogue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

/**
 * 
 * 此模块用于查询对帐申请单目录.
 * 
 * @author baijian
 * @param invoiceno
 *            发票号（支持复选）
 * @param venderid
 *            供应商编码(唯一值)
 * @param refsheetid
 *            付款单编码(唯一值)
 * @param editor
 *            制单人
 * @param editdate_min
 *            制单日期
 * @param editdate_max
 *            制单日期
 * 
 */
public class SearchVenderInvoice {

	final private String	sql_sel						= " SELECT   DISTINCT i.sheetid, i.refsheetid, i.venderid , i.editor, i.editdate , i.checkdate, b.bookname , i.flag ";

	private String			sql_join					= " FROM venderinvoice i "
																+ " INNER JOIN paymentnote0 p ON (p.sheetid=i.refsheetid )"
																+ " INNER JOIN book b ON ( b.bookno = p.bookno) ";
	private String			sql_join_venderinvoiceitem	= " FROM "
																+ " venderinvoice i INNER JOIN venderinvoiceitem it ON ( i.sheetid=it.sheetid ) "
																+ " INNER JOIN paymentnote0 p ON ( p.sheetid=i.refsheetid )"
																+ " INNER JOIN book b ON ( b.bookno = p.bookno ) ";

	/**
	 * 得到供应商发票信息列表
	 * 
	 * @param conn
	 * @param map
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public SearchVenderInvoice(Connection conn, Map map, Token token) throws SQLException,
			InvalidDataException {
		this.conn = conn;
		Filter filter = cookFilter(map);

		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException(
				"满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();
		String sql = "";

		if (token.site.getSid() == 11) {
			sql_join = " FROM venderinvoice i  INNER JOIN paymentsheet0 p ON (p.sheetid=i.refsheetid )"
					+ " left JOIN book b ON ( b.bookno = p.payshopid) ";
			sql_join_venderinvoiceitem = " FROM "
					+ " venderinvoice i INNER JOIN venderinvoiceitem it ON ( i.sheetid=it.sheetid ) "
					+ " INNER JOIN paymentsheet0 p ON ( p.sheetid=i.refsheetid )"
					+ " left JOIN book b ON ( b.bookno = p.payshopid ) ";
		}

		if (search_by_invoiceno) {
			sql = sql_sel + sql_join_venderinvoiceitem + sql_where;
		} else {
			sql = sql_sel + sql_join + sql_where;
		}

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft());
		int rows = adapter.rows();
		elm_cat.setAttribute("count", "" + count);
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("sheetname", "venderinvoice");
	}

	/**
	 * 得到符合条件的记录数目
	 * 
	 * @param filter
	 * @return
	 * @throws SQLException
	 */
	private int getCount(Filter filter) throws SQLException {
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		String sql = "";
		if (search_by_invoiceno) {
			sql = " SELECT count(*) " + sql_join_venderinvoiceitem + sql_where;
		} else {
			sql = " SELECT count(*) " + sql_join + sql_where;
		}

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next()) throw new SQLException("Failed in getCount.", "", -1);
		int rows = rs.getInt(1);

		return rows;
	}

	/**
	 * 根据map拼装sql条件
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 */
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		/**
		 * venderid 必须首先指定
		 */
		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			String venderid = ss[0];
			filter.add("i.venderid = '" + venderid + "' ");
		}

		/**
		 * 如果前台指定了invoiceno, 则以invoiceno为准, 忽略其他过滤条件.
		 */
		ss = (String[]) map.get("invoiceno");
		if (ss != null && ss.length > 0) {
			Values val_invoiceno = new Values(ss);
			filter.add("it.invoiceno IN (" + val_invoiceno.toString4String() + ") ");
			search_by_invoiceno = true;
			return filter;
		}

		/**
		 * 如果指定sheetid ，以sheetid为主.
		 */
		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" i.sheetid IN ( " + new Values(ss).toString4String() + " ) ");
			return filter;
		}

		/**
		 * 如果指定refsheetid ，以refsheetid为主.
		 */
		ss = (String[]) map.get("refsheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" i.refsheetid IN (" + new Values(ss).toString4String() + ") ");
			return filter;
		}

		ss = (String[]) map.get("bookno");
		if (ss != null && ss.length > 0) {
			filter.add(" b.bookno = " + new Values(ss[0]).toString4String());
		}

		ss = (String[]) map.get("payshopid");
		if (ss != null && ss.length > 0) {
			filter.add(" i.payshopid = " + new Values(ss[0]).toString4String());
		}

		ss = (String[]) map.get("editdate_min");
		if (ss != null && ss.length > 0) {
			filter.add(" trunc(i.editdate) >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("editdate_max");
		if (ss != null && ss.length > 0) {
			filter.add(" trunc(i.editdate) <= " + ValueAdapter.std2mdy(ss[0]));
		}

		return filter;
	}

	public Element toElement() {
		return elm_cat;
	}

	final private Connection	conn;
	private Element				elm_cat				= null;
	private boolean				search_by_invoiceno	= false;
}

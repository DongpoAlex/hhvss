package com.royalstone.vss.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;

/**
 * 
 * 此模块用于查询可付款建议. 一次可以查询12个月的可付款建议额. 供应商编码为必须提供的参数, 一次只可以查询一个供应商的付款建议.
 * 
 * @author meng
 * 
 * @param venderid
 *            供应商编码(必须提供)
 * @param accmonth_min
 *            会计期间-起始
 * @param accmonth_max
 *            会计期间-截止
 * @param bookno
 *            帐套-任选
 */
public class PayAdvice {

	/**
	 * 历史结算限额查询
	 * 
	 * @param conn
	 * @param map
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public PayAdvice(Connection conn, Map map) throws SQLException, InvalidDataException, IOException {
		String sql_sel = " SELECT  a.accmonth, a.venderid, v.vendername, "
				+ " a.bookno, b.bookname,b.booktitle,b.booktypeid,b.booklogofname, "
				+ " a.openadviceamt, a.opentaxamt, a.incadviceamt, a.inctaxamt,  "
				+ " a.decadviceamt, a.dectaxamt, a.adviceamt, a.taxamt ";
		String sql_join = " FROM VenderPayAdviceM a  JOIN book b ON ( a.bookno = b.bookno ) "
				+ " JOIN vender v ON ( v.venderid = a.venderid ) ";
		String sql_order = " ORDER BY 1 DESC, 2 ASC ";

		Filter filter = cookFilter(map);
		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");

		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();
		String sql = sql_sel + sql_join + sql_where + sql_order;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_rpt = adapter.getRowSetElement("report", "row");

		int rows = adapter.rows();
		// if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND",
		// 100 );
		elm_rpt.setAttribute("rows", "" + rows);
		elm_rpt.setAttribute("sheetname", "payadvice");
	}

	/**
	 * 此方法根据前台提供的参数设置查询过滤条件.
	 * 
	 * @param map
	 * @return Filter obj.
	 * @throws InvalidDataException
	 */
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss == null || ss.length == 0) throw new InvalidDataException("venderid is invalid!");
		String venderid = ss[0];
		if (venderid == null || venderid.length() == 0) throw new InvalidDataException("venderid is invalid!");
		filter.add("a.venderid = " + Values.toString4String(venderid));

		ss = (String[]) map.get("bookno");
		if (ss != null && ss.length > 0) {
			Values val_book = new Values(ss);
			filter.add("a.bookno IN (" + val_book.toString4String() + ") ");
		}

		ss = (String[]) map.get("accmonth");
		if (ss != null && ss.length > 0) {
			String accmonth = ss[0];
			filter.add("a.accmonth = " + accmonth);
		}

		ss = (String[]) map.get("accmonth_min");
		if (ss != null && ss.length > 0) {
			String accmonth = ss[0];
			filter.add("a.accmonth >= " + accmonth);
		}

		ss = (String[]) map.get("accmonth_max");
		if (ss != null && ss.length > 0) {
			String accmonth = ss[0];
			filter.add("a.accmonth <= " + accmonth);
		}

		return filter;
	}

	public Element toElement() {
		return elm_rpt;
	}

	private Element	elm_rpt	= null;
}

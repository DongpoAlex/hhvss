package com.royalstone.vss.catalogue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

/**
 * 此模块用于付款单目录查询
 * 
 * @param SheetID
 *            付款单号
 * @param DocNo
 *            业务单据号
 * @param InvoiceNo
 *            发票号
 * @param BookNo
 *            帐套
 * @parem EditDate 编辑日期
 * @param ApproveDate
 *            审核日期
 * @param ConfirmDate
 *            确认日期
 * @author liuwendong
 */
public class SearchPaySheet {
	final static private String	sql_sel			= " SELECT p.sheetid, status4paymentnote(p.flag) as flagname, "
														+ " p.venderid, v.vendername, (0-chargeamt) chargeamt, payamt, planpaydate, editor, p.flag, p.paydate, "
														+ " nvl(ask.reqflag,0) reqflag, ask.reqdate,ask.sjprintdate,nvl(ask.sjflag,0) sjflag ";
	final static private String	sql_from		= " FROM  V_PaymentSheet p  ";
	final static private String	sql_join		= " left join paymentnotevenderask ask on ask.sheetid=p.sheetid "
														+ " JOIN  vender v ON ( v.venderid=p.venderid ) "
														+ " LEFT JOIN  payshop b ON ( b.payshopid=p.payshopid ) ";
	final static private String	sql_constraint	= " AND p.flag<>1 ";

	/**
	 * Constructor, 在数据库中查询满足条件的付款单目录.
	 * 
	 * @param conn
	 *            数据库连接
	 * @param map
	 *            包含查询条件的Map对象
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public SearchPaySheet(Connection conn, Map map) throws SQLException, InvalidDataException {
		this.conn = conn;
		Filter filter = cookFilter(map);
		// if( filter.count() == 0 ) throw new InvalidDataException(
		// "请设置查询过滤条件." );
		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException(
				"满足条件的记录数已超过系统处理上限,请重新设置查询条件.");
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();
		String sql = sql_sel + sql_from + sql_join + sql_where + sql_constraint;
		System.out.println(sql);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft());
		int rows = adapter.rows();
		elm_cat.setAttribute("count", "" + count);
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("sheetname", "paymentnote");
		rs.close();
		pstmt.close();
	}

	/**
	 * 此方法返回满足查询条件的记录总数.
	 * 
	 * @param filter
	 *            a Filter obj.
	 * @return number of rows that satisfy filter.
	 * @throws SQLException
	 */
	private int getCount(Filter filter) throws SQLException {
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		String sql = " SELECT count(*) FROM V_PaymentSheet p left join paymentnotevenderask ask on ask.sheetid=p.sheetid "
				+ sql_where;
		PreparedStatement pstmt00 = conn.prepareStatement(sql);
		ResultSet rs00 = pstmt00.executeQuery();
		if (!rs00.next()) throw new SQLException("Failed in getting rows.", "", -1);
		int rows00 = rs00.getInt(1);
		rs00.close();
		pstmt00.close();

		return (rows00);
	}

	/**
	 * 根据前台提供的参数, 设置相应的过滤条件. 目前可支持以下参数: sheetid, bookno, venderid, flag,
	 * start_date, end_date.
	 * 
	 * @param map
	 *            包含查询条件的Map对象.
	 * @return a Filter obj.
	 * @throws InvalidDataException
	 */
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" p.venderid = " + Values.toString4String(ss[0]));
		}
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0) if (ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add(" p.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}
		ss = (String[]) map.get("payshopid");
		if (ss != null && ss.length > 0) {
			filter.add(" p.payshopid = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("editdate_min");
		if (ss != null && ss.length > 0) {
			String val_startdate = ss[0];
			filter.add(" trunc(p.editdate) >= " + ValueAdapter.std2mdy(val_startdate));
		}
		ss = (String[]) map.get("editdate_max");
		if (ss != null && ss.length > 0) {
			String val_enddate = ss[0];
			filter.add(" trunc(p.editdate) <= " + ValueAdapter.std2mdy(val_enddate));
		}
		ss = (String[]) map.get("parse_payamt");
		if (ss != null && ss.length > 0) {
			String val_payamt = ss[0];
			filter.add(" p.payamt >= " + Double.parseDouble(val_payamt.toString()));
		}
		ss = (String[]) map.get("parse_payamt2");
		if (ss != null && ss.length > 0) {
			String val_payamt2 = ss[0];
			filter.add(" p.payamt <= " + Double.parseDouble(val_payamt2.toString()));
		}
		ss = (String[]) map.get("flag");
		if (ss != null && ss.length > 0) {
			Values val_flag = new Values(ss);
			if (Integer.parseInt(val_flag.toString()) != 0) filter.add(" p.flag IN ("
					+ val_flag.toString4String() + ") ");
		}
		ss = (String[]) map.get("requestflag");
		if (ss != null && ss.length > 0) {
			Values val_flag = new Values(ss);
			filter.add(" nvl(ask.reqflag,0) IN (" + val_flag.toString4String() + ") ");
		}
		ss = (String[]) map.get("sjflag");
		if (ss != null && ss.length > 0) {
			Values val_flag = new Values(ss);
			filter.add(" nvl(ask.sjflag,0) IN (" + val_flag.toString4String() + ") ");
		}
		ss = (String[]) map.get("reqdate_min");
		if (ss != null && ss.length > 0) {
			String val_startdate = ss[0];
			filter.add(" trunc(ask.reqdate) >= " + ValueAdapter.std2mdy(val_startdate));
		}
		ss = (String[]) map.get("reqdate_max");
		if (ss != null && ss.length > 0) {
			String val_enddate = ss[0];
			filter.add(" trunc(ask.reqdate) <= " + ValueAdapter.std2mdy(val_enddate));
		}

		return filter;
	}

	/**
	 * 此方法返回一个包含付款单目录的XML元素.
	 * 
	 * @return XML element including paymentnote catalogue
	 */
	public Element toElement() {
		return elm_cat;
	}

	final private Connection	conn;
	private Element				elm_cat	= null;
}

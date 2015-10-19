package com.royalstone.vss.catalogue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;

/**
 * 已制单审核付款单查询(为供应商发票录入提供列表)
 * 
 * @author baijian
 */
public class SearchPaymentSheet4Vi {

	/**
	 * @param conn
	 * @param map
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public SearchPaymentSheet4Vi(Connection conn, Map map) throws SQLException, InvalidDataException, IOException {
		String sql = null;
		sql = " SELECT pn.sheetid, status4paymentnote(pn.flag) as flag, (0-pn.chargeamt) chargeamt, pn.payamt, " +
				" pn.planpaydate, pn.editor,nvl(vps.payflag,0) payflag "
				+ " FROM paymentsheet0  pn LEFT OUTER JOIN venderinvoice vi ON ( vi.refsheetid = pn.sheetid) "
				+ " LEFT OUTER JOIN venderpaystatus vps ON (vps.venderid = pn.venderid and vps.bookno = pn.payshopid ) "
				+ " WHERE ( "
				+ cookFilter(map)
				+ " AND vi.flag IS NULL ) OR ( vi.flag=0 AND "
				+ cookFilter(map) + " )";
		
		Log.debug(this.getClass().getName(), sql);
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("catalogue", "row");
		int rows = adapter.rows();
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("sheetname", "paymentnote");
		rs.close();
		stmt.close();
	}

	/**
	 * 设置相应的过滤条件
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 */
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		// venderid必须指定
		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			Values val_vender = new Values(ss);
			filter.add(" pn.venderid = " + val_vender.toString4String());
		}

		filter.add(" pn.flag=2 ");

		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0) if (ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add(" pn.sheetid IN ( " + val_sheetid.toString4String() + " ) ");
			return filter;
		}
		ss = (String[]) map.get("payshopid");
		if (ss != null && ss.length > 0) {
			Values val_bookno = new Values(ss);
			filter.add(" pn.payshopid = " + val_bookno.toString4String());
		}

		ss = (String[]) map.get("editdate_min");
		if (ss != null && ss.length > 0) {
			String val_startdate = ss[0];
			filter.add(" trunc(pn.editdate) >= " + ValueAdapter.std2mdy(val_startdate));
		}
		ss = (String[]) map.get("editdate_max");
		if (ss != null && ss.length > 0) {
			String val_enddate = ss[0];
			filter.add(" trunc(pn.editdate) <= " + ValueAdapter.std2mdy(val_enddate));
		}
		ss = (String[]) map.get("parse_payamt");
		if (ss != null && ss.length > 0) {
			String val_payamt = ss[0];
			filter.add(" pn.payamt >= " + Double.parseDouble(val_payamt));
		}
		ss = (String[]) map.get("parse_payamt2");
		if (ss != null && ss.length > 0) {
			String val_payamt2 = ss[0];
			filter.add(" pn.payamt <= " + Double.parseDouble(val_payamt2));
		}

		return filter;
	}

	public Element toElement() {
		return elm_cat;
	}

	private Element	elm_cat	= null;
}

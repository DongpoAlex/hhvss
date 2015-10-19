package com.royalstone.vss.holiday;

import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlMapUtil;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

public class HolidayDao {
	public HolidayDao(Connection conn, Token token) {
		this.conn = conn;
		this.token = token;
		site = token.site.getSid();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		year = sdf.format(new java.util.Date());
	}

	final private Connection	conn;
	final private Token			token;
	final int					site;
	final String				year;

	static final private long	sql4Detail			= 3050002001L;
	static final private long	sql4Search			= 3050002002L;
	static final private long	sql4delItem			= 3050002003L;
	static final private long	sql4delHead			= 3050002004L;
	static final private long	sql4addHead			= 3050002005L;
	static final private long	sql4addBody			= 3050002006L;

	static final private long	sql4getExpVender	= 3050002007L;
	static final private long	sql4getExp			= 3050002008L;
	static final private long	sql4SetExp			= 3050002009L;

	public Element getExp() {
		return SqlUtil.getRowSetElement(conn, SqlMapLoader.getInstance().getSql(site, sql4getExp).toString(), "list");
	}

	public void setExp(Element elm) {
		List list = elm.getChildren("row");

		for (Iterator it = list.iterator(); it.hasNext();) {
			Element elmItem = (Element) it.next();
			String isupdate = elmItem.getChildText("isupdate");
			if ("true".equals(isupdate)) {
				String htype = elmItem.getChildText("htype");
				String holiday = elmItem.getChildText("holiday");
				String startdate = elmItem.getChildText("startdate");
				String enddate = elmItem.getChildText("enddate");
				String startdate2 = elmItem.getChildText("startdate2");
				String enddate2 = elmItem.getChildText("enddate2");
				String note = elmItem.getChildText("note");
				Date sdate = Date.valueOf(startdate2);
				Date edate = Date.valueOf(enddate2);

				SqlUtil.executePS(conn, SqlMapLoader.getInstance().getSql(site, sql4SetExp).toString(), new Object[] {
						sdate, edate, note, htype });
			}
		}

	}

	public Element getExp4Vender() {
		return SqlUtil.getRowSetElement(conn, SqlMapLoader.getInstance().getSql(site, sql4getExpVender).toString(),
				token.getBusinessid(), "list");
	}

	/**
	 * 获得某供应商休假明细
	 * 
	 * @return
	 */
	public Element getDetail() {
		return SqlUtil.getRowSetElement(conn, SqlMapLoader.getInstance().getSql(site, sql4Detail).toString(), token
				.getBusinessid(), "list");
	}

	private void addHead(Element elm) {
		String vendercon = elm.getChildText("vendercon");
		String vendertel = elm.getChildText("vendertel");
		String venderid = token.getBusinessid();

		SqlUtil.executePS(conn, SqlMapLoader.getInstance().getSql(site, sql4addHead).toString(), new Object[] { year,
				venderid, vendercon, vendertel });
	}

	private void additem(Element elm) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		String venderid = token.getBusinessid();
		String htype = elm.getChildText("htype");
		String holiday = elm.getChildText("holiday");
		String startdate = elm.getChildText("startdate");
		String enddate = elm.getChildText("enddate");
		String startdate2 = elm.getChildText("startdate2");
		String enddate2 = elm.getChildText("enddate2");
		String note = elm.getChildText("note");

		Date ssd = new Date(df.parse(startdate).getTime());
		Date esd = new Date(df.parse(enddate).getTime());
		Object ssd2 = null;
		Object esd2 = null;
		// Date ssd2 = new Date(df.parse(startdate2).getTime());
		// Date esd2 = new Date(df.parse(enddate2).getTime());
		SqlUtil.executePS(conn, SqlMapLoader.getInstance().getSql(site, sql4addBody).toString(), new Object[] { year,
				venderid, htype, holiday, ssd, esd, ssd2, esd2, note });
	}

	private void delHead(String venderid) {
		String sql = SqlMapLoader.getInstance().getSql(site, sql4delHead).toString();
		SqlUtil.executePS(conn, sql, new Object[] { venderid, year });
	}

	private void delItem(String venderid, int htype) {
		String sql = SqlMapLoader.getInstance().getSql(site, sql4delItem).toString();
		SqlUtil.executePS(conn, sql, new Object[] { venderid, htype, year });
	}

	private void updateHead(Element elm) {
		delHead(token.getBusinessid());
		addHead(elm);
	}

	private void updateItem(Element elm) throws ParseException {
		List list = elm.getChildren("row");

		for (Iterator it = list.iterator(); it.hasNext();) {
			Element elmItem = (Element) it.next();
			String isupdate = elmItem.getChildText("isupdate");

			if ("true".equals(isupdate)) {
				String tmp = elmItem.getChildText("htype");
				int htype = Integer.parseInt(tmp);
				delItem(token.getBusinessid(), htype);
				additem(elmItem);
			}
		}
	}

	public void save(Element elmHead, Element elmBody) throws ParseException {
		updateHead(elmHead);
		updateItem(elmBody);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("maxvenderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid <= " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("minvenderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid >= " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("htype");
		if (ss != null && ss.length > 0) {
			filter.add(" b.htype = " + Values.toString4String(ss[0]));
		}
		ss = (String[]) map.get("year");
		if (ss != null && ss.length > 0) {
			filter.add(" a.year = " + Values.toString4String(ss[0]));
		}

		return filter;
	}

	public Element search(Map parms) {
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site, sql4Search);
		String sql_where = cookFilter(parms).toString();
		Element elm = null;
		int count = SqlMapUtil.getCount(conn, sqlUnit, sql_where);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) {
			throw new InvalidDataException("满足条件已超过系统处理上限,请重新设置查询条件.");
		}
		String sql = sqlUnit.toString(sql_where);

		elm = SqlUtil.getRowSetElement(conn, sql, "rowset", 1, VSSConfig.getInstance().getRowsLimitSoft());
		return elm;
	}

}

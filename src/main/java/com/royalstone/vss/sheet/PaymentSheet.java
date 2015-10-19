package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;

public class PaymentSheet extends SheetService {

	static String	sql4Search		= "3020107001";

	static String	sql4Head		= "3020107002";

	static String	sql4Body		= "3020107003";

	static String	tableName		= "";
	static String	catTableName	= "";
	static String	tt				= "付款单";

	private int	sid;

	public PaymentSheet(Connection conn, Token token, String cmid) {
		//super(conn, token, cmid, sql4Search, sql4Head, sql4Body, tableName, catTableName, tt);
		super(conn, token, cmid,3020107,sql4Head,sql4Body,tableName);
		sid = token.site.getSid();
	}

	@Override
	public Element search(Map parms) {
		Filter filter = cookFilter(parms);

//		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
//		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException(
//				"满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql_where = cookFilter(parms).toString();
		
		String sql = SqlMapLoader.getInstance().getSql(sid, "3020107001").toString();
		sql+=" WHERE " + sql_where;
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "rowset");
		elm_cat.setAttribute("row_total", "" + count);
		return elm_cat;
	}

	@Override
	public Element veiw(String sheetid) {
		setPrintInfo(sheetid);
		Element elm_sheet = new Element("sheet");
		elm_sheet.setAttribute("title", title);
		elm_sheet.setAttribute("logo", logo);

		elm_sheet.addContent(getHead(sheetid));
		elm_sheet.addContent(getBody(sheetid));
		return elm_sheet;
	}

	/* (non-Javadoc)
	 * @see com.royalstone.vss.sheet.SheetService#setPrintInfo(java.lang.String)
	 */
	public void setPrintInfo(String sheetid) {
		String company = "华润万家";
		this.title = company + this.title;
		this.logo = "../img/" + this.logo;
	}

	/*
	 * 表体
	 * @see com.royalstone.vss.sheet.SheetService#getBody(java.lang.String)
	 */
	@Override
	public Element getBody(String sheetid) {
		Element elmBody = new Element("bodydetail");
		//item
		String sql = SqlMapLoader.getInstance().getSql(sid, "3020107003").toString();
		elmBody.addContent(SqlUtil.getRowSetElement(conn, sql, sheetid, "item"));
		
		//dtl
		sql = SqlMapLoader.getInstance().getSql(sid, "3020107004").toString();
		elmBody.addContent(SqlUtil.getRowSetElement(conn, sql, sheetid, "dtl"));
		
		//fee
		sql = SqlMapLoader.getInstance().getSql(sid, "3020107005").toString();
		elmBody.addContent(SqlUtil.getRowSetElement(conn, sql, sheetid, "fee"));
		
		//goodsinfo
		sql = SqlMapLoader.getInstance().getSql(sid, "3020107005").toString();
		elmBody.addContent(SqlUtil.getRowSetElement(conn, sql, sheetid, "goodsinfo"));
		
		return elmBody;
	}

	@Override
	public int getCount(Filter filter) {
		String sql = " SELECT count( * ) FROM v_paymentsheet a WHERE " + filter.toString();

		String temp = SqlUtil.querySQL4SingleColumn(conn, sql).get(0);
		int rows = 0;
		if (temp != null) {
			rows = Integer.parseInt(temp);
		}

		return rows;
	}
	
	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;
		filter.add(" 1=1 ");
		ss = (String[]) parms.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("a.venderid IN (" + ValueAdapter.toString4String(ss) + ") ");
		}
		
		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("a.sheetid=" + ValueAdapter.toString4String(ss[0]) + " ");
			return filter;
		}
		
		
		ss = (String[]) parms.get("flag");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("a.flag=" + ValueAdapter.toString4String(ss[0]) + " ");
		}
		

		return filter;
	}
}

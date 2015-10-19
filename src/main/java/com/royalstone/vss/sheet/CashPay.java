package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;

public class CashPay extends SheetService {

	static String	sql4Search		= "3020219001";

	static String	sql4Head		= "3020219002";
	
	static String	sql4Body		= "3020219003";
	static String	sql4Body2		= "3020219004";
	static String	tableName		= "cashpay";
	public CashPay(Connection conn, Token token, String cmid) {
		super(conn, token,cmid,3020219,sql4Head, sql4Body, tableName);
		super.catTableName="";
	}
	
	public Element getBody(String sheetid) {
		int sid = token.site.getSid();
		Element body = new Element("body");
		Element item = SqlUtil.getRowSetElement(conn, SqlMapLoader.getInstance().getSql(sid, sql4Body).toString(), sheetid, "body1");
		Element item2 = SqlUtil.getRowSetElement(conn, SqlMapLoader.getInstance().getSql(sid, sql4Body2).toString(), sheetid, "body2");
		body.addContent(item);
		body.addContent(item2);
		return body;
	}
	
	public void setPrintInfo(String sheetid) {
		ColModel cm = ColModelLoader.getInstance().getCM(token.site.getSid(), cmid);
		this.title = cm.getTitle();
		this.logo = "../img/crv_logo.jpg";
	}
	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) parms.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_vender = new Values(ss);
			filter.add("a.venderid IN (" + val_vender.toString4String() + ") ");
		}

		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("a.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}


		ss = (String[]) parms.get("flag");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("a.flag IN (" + val_shopid.toString4String() + ") ");
		}
		
		/**
		 * 根据门店过滤
		 */
		ss = (String[]) parms.get("payshopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("a.payshopid IN (" + val_shopid.toString4String() + ") ");
		}
		
		ss = (String[]) parms.get("editdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.editdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("editdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.editdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("checkdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.checkdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("checkdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.checkdate) <= " + ValueAdapter.std2mdy(date));
		}

		return filter;
	}
}

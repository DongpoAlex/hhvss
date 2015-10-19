package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class AMSPurchase extends SheetService {

	static String	sql4Search		= "3010113001";

	static String	sql4Head		= "3010113002";
	
	static String	sql4Body		= "3010113003";
	static String	tableName		= "ams_purchase";
	public AMSPurchase(Connection conn, Token token, String cmid) {
		super(conn, token,cmid,3010113,sql4Head, sql4Body, tableName);
	}
	
	public void setPrintInfo(String sheetid) {
		ColModel cm = ColModelLoader.getInstance().getCM(token.site.getSid(), cmid);
		this.title = cm.getTitle();
		this.logo = "../img/crv_logo.jpg";
	}
	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;
		
		//99状态的不显示
		filter.add(" a.flag<>99");

		ss = (String[]) parms.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_vender = new Values(ss);
			filter.add("cat.venderid IN (" + val_vender.toString4String() + ") ");
		}

		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("cat.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		ss = (String[]) parms.get("refsheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("a.refsheetid IN (" + val_sheetid.toString4String() + ") ");
		}

		ss = (String[]) parms.get("status");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("cat.status IN (" + val_shopid.toString4String() + ") ");
		}
		
		/**
		 * 根据门店过滤
		 */
		ss = (String[]) parms.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("a.shopid IN (" + val_shopid.toString4String() + ") ");
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

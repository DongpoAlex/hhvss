package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;

public class NetInCar extends SheetService {

	static String	sql4Search		= "6000010001";
	static String	sql4Head		= "6000010002";
	static String	sql4Body		= "6000010003";
	static String	tableName		= "netincarhead";
	public NetInCar(Connection conn, Token token, String cmid) {
		super(conn, token,cmid,3020219,sql4Head, sql4Body, tableName);
		super.catTableName="";
	}
	
	public void setPrintInfo(String sheetid) {
		ColModel cm = ColModelLoader.getInstance().getCM(token.site.getSid(), cmid);
		this.title = cm.getTitle();
		this.logo = "../img/crv_logo.jpg";
	}
	
	public String getVenderid(String sheetid) {
		String sql = " select supplier_no from " + tableName + " a where a.incarno=? ";
		List<String> arr = SqlUtil.queryPS4SingleColumn(conn, sql, sheetid);
		String rel = "";
		if (arr.size() > 0) {
			rel = arr.get(0);
		}
		return rel;
	}
	
	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) parms.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_vender = new Values(ss);
			filter.add("a.supplier_no IN (" + val_vender.toString4String() + ") ");
		}

		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("a.order_serial IN (" + val_sheetid.toString4String() + ") ");
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
		
		ss = (String[]) parms.get("date_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("a.rgst_date >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("date_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("a.rgst_date <= " + ValueAdapter.std2mdy(date));
		}


		return filter;
	}
}

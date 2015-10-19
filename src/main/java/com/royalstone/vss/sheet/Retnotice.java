package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class Retnotice extends SheetService {

	static String	sql4Search		= " SELECT a.sheetid, status4cat( cat.status ) status, trunc(cat.readtime) readtime, "
											+ " a.venderid, v.vendername, a.shopid, s.shopname, "
											+ " a.paytypeid, p.paytypename, a.retdate, a.flag, "
											+ " a.editor, a.editdate, a.operator, a.checker, a.checkdate,a.askshopid ,ss.shopname as askshopname, a.rettype "
											+ " FROM  cat_retnotice cat "
											+ " JOIN retnotice a ON ( cat.sheetid = a.sheetid ) "
											+ " JOIN vender v ON (a.venderid=v.venderid) "
											+ " JOIN paytype p ON (a.paytypeid=p.paytypeid) "
											+ " JOIN shop s ON (a.shopid=s.shopid) "
											+ " LEFT JOIN shop ss ON (a.askshopid=ss.shopid) ";

	static String	sql4Head		= "SELECT 	a.sheetid, a.refsheetid, "
											+ " a.venderid, v.vendername, v.faxno venderfax, v.telno vendertel, "
											+ " a.shopid, sh.shopname, sh.address shopaddr, sh.telno shoptel, sh.shopstatus,"
											+ " p.paytypeid, p.paytypename, a.retdate, "
											+ " a.flag, a.editor, a.editdate, "
											+ " a.operator, a.checker, a.checkdate, "
											+ " a.placeid, pe.placename, a.majorid, c.categoryname majorname, "
											+ " a.note, cat.status, cat.warnstatus,cat.releasedate,cat.confirmtime,a.rettype, "
											+ " a.askshopid,ss.shopname as askshopname,sh.controltype, "
											+ " decode(nvl(gm.majorid, -1), -1, 'false', 'true') isMedica "
											+ " FROM retnotice a "
											+ " JOIN cat_retnotice cat ON ( cat.sheetid = a.sheetid ) "
											+ " JOIN vender v ON ( v.venderid = a.venderid ) "
											+ " JOIN shop sh ON ( a.shopid = sh.shopid ) "
											+ " LEFT JOIN shop ss ON ( a.askshopid = ss.shopid ) "
											+ " JOIN paytype p ON ( p.paytypeid = a.paytypeid ) "
											+ " LEFT JOIN place pe ON ( pe.shopid = a.askshopid AND pe.placeid = a.placeid ) "
											+ " LEFT JOIN category c ON ( categoryid = a.majorid ) "
											+ " LEFT JOIN gspmajor gm ON ( gm.majorid = a.majorid ) "
											+ " WHERE a.sheetid= ?  ";

	static String	sql4Body		= " SELECT i.sheetid, i.goodsid, g.barcode, g.goodsname, g.spec, g.unitname, "
											+ " i.vldqty, i.retqty, i.cost, (i.cost*i.vldqty) sumcost, i.reason, "
											+ " i.categoryid, c.categoryname, g.approvalnum,g.manufacturer,i.BatchNo,i.ProductDate, i.InureDate "
											+ " FROM retnoticeitem i "
											+ " JOIN goods g ON ( g.goodsid = i.goodsid ) "
											+ " join category c ON ( c.categoryid = i.categoryid ) "
											+ " WHERE i.sheetid = ? ";
	static String	tableName		= "retnotice";
	static String	catTableName	= "cat_retnotice";
	static String	tt			= "退货通知单";

	public Retnotice(Connection conn,Token token,String cmid) {
		super(conn, token,cmid, sql4Search,sql4Head, sql4Body, tableName, catTableName, tt);
		this.majorName = "majorid";
	}

	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;

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
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("cat.status IN (" + val_sheetid.toString4String() + ") ");
		}
		
		ss = (String[]) parms.get("warnstatus");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("cat.warnstatus IN (" + val_sheetid.toString4String() + ") ");
		}

		/**
		 * 根据门店过滤
		 */
		ss = (String[]) parms.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("a.shopid IN (" + val_shopid.toString4String() + ") ");
		}

		ss = (String[]) parms.get("retdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.retdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("retdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.retdate) <= " + ValueAdapter.std2mdy(date));
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

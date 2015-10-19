package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class Receipt extends SheetService {

	static String	sql4Search		= " SELECT "
											+ " cat.releasedate,cat.confirmtime,cat.readtime,cat.status, status4cat(cat.status) statusname, "
											+ " a.sheetid, a.refsheetid, a.venderid, v.vendername, a.shopid, sh.shopname,a.flag, "
											+ " a.logistics, a.note, name4code(a.logistics,'logistics') as logisticsname,a.paytypeid,"
											+ " a.editor, a.editdate, a.operator, a.checker, a.checkdate "
											+ " FROM cat_receipt cat "
											+ " JOIN receipt a ON ( cat.sheetid = a.sheetid ) "
											+ " JOIN vender v ON ( v.venderid = a.venderid ) "
											+ " JOIN shop sh ON ( sh.shopid = a.shopid ) ";

	static String	sql4Head		= " select tmp.* ,decode(nvl(gm.majorid, -1), -1, 'false', 'true') isMedica from "
											+ " (SELECT cat.releasedate,cat.confirmtime,cat.readtime,cat.status, status4cat(cat.status) statusname, "
											+ " a.sheetid, a.refsheetid, a.venderid, v.vendername, a.shopid, sh.shopname,a.flag, "
											+ " a.logistics, a.note,name4code(a.logistics,'logistics') as logisticsname,"
											+ " a.editor, a.editdate, a.operator, a.checker, a.checkdate, a.paytypeid,p.paytypename,"
											+ " v.telno vendertel,v.faxno venderfax,sh.telno shoptel ,sh.address shopaddr, "
											+ " (select trunc(bb.categoryid/10000) majorid from receiptitem bb where bb.sheetid=a.sheetid and rownum=1) majorid "
											+ " FROM receipt a "
											+ " JOIN cat_receipt cat ON ( cat.sheetid = a.sheetid ) "
											+ " JOIN vender v ON ( v.venderid = a.venderid ) "
											+ " JOIN shop sh ON ( sh.shopid = a.shopid ) "
											+ " JOIN paytype p ON (p.paytypeid=a.paytypeid) where a.sheetid=? ) tmp "
											+ " LEFT JOIN gspmajor gm ON ( gm.majorid = tmp.majorid ) ";

	static String	sql4Body		= " select a.sheetid,a.goodsid,a.orderqty,a.qty,a.stkqty,a.cost,a.costtaxrate, "
											+ "a.saletaxrate, a.pknum,(a.qty+a.giftqty)*a.cost sumcost,a.giftqty, "
											+ " g.barcode,g.goodsname,g.spec,g.unitname,g.deptid,g.approvalnum, "
											+ " g.manufacturer,a.BatchNo,a.ProductDate,a.InureDate,a.shopid,s.shopname,a.placeid,p.placename"
											+ " from receiptitem a " + " join goods g on (g.goodsid=a.goodsid) "
											+ " join shop s on (s.shopid=a.shopid) " 											
											+ "join place p on ( p.placeid=a.placeid and p.shopid=a.shopid)"
											+" where a.sheetid=? ";
	static String	tableName		= "receipt";
	static String	catTableName	= "cat_receipt";
	static String	tt				= "验收单";

	public Receipt(Connection conn, Token token, String cmid) {
		super(conn, token, cmid, sql4Search, sql4Head, sql4Body, tableName, catTableName, tt);
		this.majorName = "flag";
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
			filter.add("a.sheetid IN (" + val_sheetid.toString4String() + ") ");
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

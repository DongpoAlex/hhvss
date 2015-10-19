package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class Ret extends SheetService {

	static String sql4Search = " SELECT "
			+ " cat.releasedate,cat.confirmtime,cat.readtime,cat.status, status4cat(cat.status) statusname, "
			+ " a.sheetid,  a.venderid, v.vendername, a.shopid, sh.shopname, "
			+ " a.paytypeid, fgetcodename('RETMODE',a.retmode) rettype,sum(r.cost*r.retqty) cost,a.retdate,a.operator,"
			+ " a.editor, a.editdate,  a.retchecker, a.retcheckdate "
			+ " FROM cat_ret cat  "
			+ " JOIN ret a ON ( cat.sheetid = a.sheetid ) "
			+ " JOIN retitem r ON ( cat.sheetid = r.sheetid ) "
			+ " JOIN vender v ON ( v.venderid = a.venderid ) "
			+ " JOIN shop sh ON ( sh.shopid = a.shopid ) ";
	static String sqlGroupbyStr = " group by cat.releasedate,cat.confirmtime,cat.readtime,cat.status, status4cat(cat.status) ,  a.sheetid,  a.venderid, v.vendername, a.shopid, sh.shopname,  a.paytypeid, a.retmode,a.retdate,a.operator, a.editor, a.editdate,  a.retchecker, a.retcheckdate ";

	static String sql4Head = " SELECT "
			+ " cat.releasedate,cat.confirmtime,cat.readtime,cat.status, status4cat(cat.status) statusname, "
			+ " a.sheetid, a.refsheetid, a.venderid, v.vendername, a.shopid, sh.shopname,a.flag, "
			+ " a.majorid, ca.categoryname, a.paytypeid, e.paytypename,a.logistics, a.badflag, a.retmode, "
			+ " a.retdate, a.editor, a.editdate, a.operator, a.checker, a.checkdate, a.note,"
			+ " v.telno vendertel,v.faxno venderfax,sh.telno shoptel ,sh.address shopaddr,a.placeid,p.placename,a.retmode rettype, "
			+ " decode(nvl(gm.majorid, -1), -1, 'false', 'true') isMedica "
			+ " FROM ret a "
			+ " JOIN cat_ret cat ON ( cat.sheetid = a.sheetid ) "
			+ " left join place p on (p.shopid=a.shopid and p.placeid=a.placeid) "
			+ " JOIN vender v ON ( v.venderid = a.venderid ) "
			+ " JOIN category ca ON ( ca.categoryid = a.majorid ) "
			+ " JOIN paytype e ON (e.paytypeid=a.paytypeid)"
			+ " JOIN shop sh ON ( sh.shopid = a.shopid ) "
			+ " LEFT JOIN gspmajor gm ON ( gm.majorid = a.majorid ) "
			+ " where a.sheetid=?";

	static String sql4Body = " select a.sheetid,a.goodsid,a.categoryid,a.cost,a.askqty,a.retqty, a.retqty*a.cost sumcost, "
			+ " a.reasontypeid,a.reason,a.stkqty,g.barcode,g.goodsname,g.unitname,g.spec,g.approvalnum,g.manufacturer,a.BatchNo,a.ProductDate, a.InureDate,a.goodscostid "
			+ " FROM retitem a "
			+ " JOIN goods g on (g.goodsid=a.goodsid)"
			+ " JOIN category ca ON ( ca.categoryid = a.categoryid ) "
			+ " WHERE a.sheetid=? ";

	static String tableName = "ret";
	static String catTableName = "cat_ret";
	static String tt = "退货单";

	public Ret(Connection conn, Token token, String cmid) {

		super(conn, token, cmid, sql4Search, sql4Head, sql4Body, tableName,
				catTableName, tt,sqlGroupbyStr);
		this.majorName = "majorid";
	}

	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) parms.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_vender = new Values(ss);
			filter.add("cat.venderid IN (" + val_vender.toString4String()
					+ ") ");
		}

		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("cat.sheetid IN (" + val_sheetid.toString4String()
					+ ") ");
			return filter;
		}

		ss = (String[]) parms.get("refsheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("a.refsheetid IN (" + val_sheetid.toString4String()
					+ ") ");
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

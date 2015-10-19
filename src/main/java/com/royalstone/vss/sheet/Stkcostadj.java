package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

public class Stkcostadj extends SheetService {

	static String	sql4Search		= " SELECT "
											+ " s.sheetid, s.shopid, sh.shopname, "
											+ " s.venderid, v.vendername, pay.paytypename ,s.TotalAmt, "
											+ " (s.TotalAmt+s.TotalTaxAmt17+s.TotalTaxAmt13) as totaltaxamt, "
											+ " s.TotalAmt17, s.TotalTaxAmt17, s.TotalTaxAmt13, "
											+ " s.editor, s.editdate, s.checker, s.checkdate, s.note "
											+ " FROM stkcostadj s "
											+ " JOIN vender v ON ( v.venderid = s.venderid ) "
											+ " JOIN paytype pay ON ( pay.paytypeid = s.paytypeid ) "
											+ " JOIN shop sh ON ( sh.shopid = s.shopid ) ";

	static String	sql4Head		= " SELECT "
											+ " s.sheetid, sh.shopname, "
											+ " s.venderid, v.vendername, pay.paytypeid,pay.paytypename , s.TotalAmt, "
											+ " (s.TotalAmt+s.TotalTaxAmt17+s.TotalTaxAmt13) as totaltaxamt,  "
											+ " s.TotalAmt17, s.TotalTaxAmt17, s.TotalTaxAmt13, "
											+ " s.editor, s.editdate, s.checker, s.checkdate, s.note "
											+ " FROM stkcostadj s"
											+ " JOIN vender v ON ( v.venderid = s.venderid ) "
											+ " JOIN paytype pay ON ( pay.paytypeid = s.paytypeid ) "
											+ " JOIN shop sh ON ( sh.shopid = s.shopid ) "
											+ " WHERE s.sheetid = ? ";

	static String	sql4Body		= "SELECT "
											+ " s.shopid, sh.shopname ,"
											+ " i.goodsid, i.DeptID,cat.categoryname,g.barcode, g.goodsname, i.qty, i.OldCost,"
											+ " i.NewCost, i.AdjAmt, i.CostTaxRate,i.SaleTaxRate " + " FROM "
											+ " stkcostadjitem i"
											+ " JOIN stkcostadj s ON (s.sheetid=i.sheetid)"
											+ " left join category cat ON (cat.categoryid=i.deptid)"
											+ " JOIN goods g ON (i.goodsid=g.goodsid)"
											+ " JOIN shop sh ON (sh.shopid=s.shopid)"
											+ " WHERE i.sheetid= ? ";
	static String	tableName		= "stkcostadj";
	static String	catTableName	= null;
	static String	tt			= "库存进价调整";

	public Stkcostadj(Connection conn,Token token,String cmid) {
		super(conn, token,cmid, sql4Search,sql4Head, sql4Body, tableName, catTableName, tt);
	}

	public void setPrintInfo(String sheetid) {
		String company = null;
		int controltype = 0;
		// controltype 门店控制符合，MR公司用
		// 取得门店和课类,抬头公司
		String sql_sel_shop = "select a.shopid,s.controltype,b.booktitle,b.booklogofname from "
				+ tableName + " a " + " inner join shop s on s.shopid=a.shopid "
				+ " inner join book b on b.bookno=s.bookno " + " where sheetid=?";

		ResultSet rs = SqlUtil.queryPS(conn, sql_sel_shop, sheetid);
		try {
			if (rs.next()) {
				controltype = rs.getInt("controltype");
				company = SqlUtil.toLocal(rs.getString("booktitle"));
				this.logo = rs.getString("booklogofname");
			}

			SqlUtil.close(rs);

			if (controltype == 2) {
				company = "MR";
				this.logo = VSSConfig.getInstance().getPrintTobatoLogo();
			}

			super.title = company + super.title;
			this.logo = "../img/" + this.logo;

		}
		catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) parms.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_vender = new Values(ss);
			filter.add("s.venderid IN (" + val_vender.toString4String() + ") ");
		}

		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("s.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		/**
		 * 根据订货地过滤
		 */
		ss = (String[]) parms.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("s.shopid IN (" + val_shopid.toString4String() + ") ");
		}

		ss = (String[]) parms.get("editdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(s.editdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("editdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(s.editdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("checkdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(s.checkdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("checkdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(s.checkdate) <= " + ValueAdapter.std2mdy(date));
		}

		return filter;
	}
}

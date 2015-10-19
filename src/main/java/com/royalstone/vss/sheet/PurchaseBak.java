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

public class PurchaseBak extends SheetService {

	static String	sql4Search		= " SELECT "
		+ " a.sheetid, a.refsheetid, sh.shopname destshopname , a.shopid, s.shopname, "
		+ " name4code(a.logistics,'logistics') as logisticsname, c.categoryname, "
		+ " a.orderdate, a.validdays, a.deadline, a.venderid, "
		+ " a.deliverdate, a.delivertimeid, a.note, a.editor, a.checker FROM purchase_bak a "
		+ " INNER JOIN shop s ON ( s.shopid=a.shopid  ) "
		+ " INNER JOIN shop sh ON ( sh.shopid=a.destshopid ) "
		+ " left JOIN category c ON ( c.categoryid=a.sgroupid ) ";

static String	sql4Head		= " SELECT "
		+ " a.sheetid, a.refsheetid, a.destshopid,a.shopid, sh.shopname destshopname , s.shopname , s.address shopaddr ,s.telno shoptel ,a.sgroupid,s.shopstatus, a.discountrate, "
		+ " c.categoryname, a.paytypeid, pt.paytypename, a.logistics , name4code(a.logistics,'logistics') as logisticsname, "
		+ " a.orderdate, a.validdays, (a.orderdate + a.validdays - 1) deadline, a.venderid, v.vendername, v.faxno venderfax, v.address venderaddr,v.telno vendertel,"
		+ " a.deliverdate, a.delivertimeid, a.vdeliverdate,a.note, a.editor, a.editdate,a.checker, substr(pt.paytypename,1,1) paytypeflag, "
		+ " dt.startTime,dt.endTime, s.controltype,flag4sheet(a.flag) flagname,a.flag,cat.releasedate,cat.status,cat.readtime "
		+ " FROM purchase_bak a "
		+ " INNER JOIN cat_order_bak cat ON ( cat.sheetid=a.refsheetid )"
		+ " INNER JOIN shop s ON ( s.shopid=a.shopid  ) "
		+ " INNER JOIN shop sh ON ( sh.shopid=a.destshopid ) "
		+ " LEFT  JOIN category c ON ( c.categoryid=a.sgroupid ) "
		+ " INNER JOIN paytype pt ON ( pt.paytypeid=a.paytypeid ) "
		+ " INNER JOIN vender v ON ( v.venderid=a.venderid ) "
		+ " LEFT  OUTER JOIN DeliverTime dt ON ( dt.DeliverID = a.deliverTimeid ) "
		+ " WHERE a.sheetid=? ";

static String	sql4Body		= " SELECT "
		+ " s.shopid destshopid,s.shopname destshopname,sh.shopname shopname,sh.shopid shopid, "
		+ " i.goodsid, i.barcode, g.goodsname, g.spec, g.unitname, g.ycomp,g.qadays,"
		+ " i.qty, i.pkgqty, i.pkgvolume,trunc((i.qty / i.pkgvolume)) as pkqty, i.memo,i.presentqty, "
		+ " i.concost,i.cost,i.firstdisc,(i.qty*i.cost) sumcost,(i.qty*i.concost) sumconcost "
		+ " FROM purchase_bak a "
		+ " JOIN purchaseitem_bak i ON (a.sheetid=i.sheetid)  "
		+ " JOIN goods g ON (i.goodsid=g.goodsid) "
		+ " JOIN shop s ON (s.shopid=a.destshopid) "
		+ " JOIN shop sh ON (sh.shopid=a.shopid) "
		+ " WHERE a.sheetid=? order by a.shopid,g.deptid,i.goodsid";
	static String	tableName		= "purchase_bak";
	static String	catTableName	= null;
	static String	tt			= "订货通知单";

	public PurchaseBak(Connection conn,Token token,String cmid) {
		super(conn, token,cmid, sql4Search,sql4Head, sql4Body, tableName, catTableName, tt);
		this.majorName = "sgroupid";
	}
	public void setPrintInfo(String sheetid) {
		String company = null;
		String shopid = null;
		int logistics =0;
		int controltype = 0;
		int majorid = 0;
		// controltype 门店控制符合，MR公司用
		// 取得门店和课类,抬头公司
		String sql_sel_shop = "select a.logistics,a.shopid,a."+majorName+",s.controltype,s.shopname from "
				+ tableName + " a " + " inner join shop s on s.shopid=a.shopid "
				 + " where sheetid=?";

		ResultSet rs = SqlUtil.queryPS(conn, sql_sel_shop, sheetid);
		try {
			if (rs.next()) {
				shopid = rs.getString("shopid") + "X";
				majorid = rs.getInt(majorName);
				controltype = rs.getInt("controltype");
				company = SqlUtil.fromLocal(rs.getString("shopname"));
				//this.logo = rs.getString("booklogofname");
				logistics = rs.getInt("logistics");
			}

			SqlUtil.close(rs);

			// 如果是烟草课类，则修改打印抬头和logo
			//if (majorid == VSSConfig.getInstance().getTobatoMajorid() && !shopid.equals("")) {
				String sql_sel_headshopid = "select s.shopname from shop s "						
						+ " where s.shopid=? ";
				rs = SqlUtil.queryPS(conn, sql_sel_headshopid, shopid);
				if (rs.next()) {
					company = SqlUtil.fromLocal(rs.getString("shopname"));
					//this.logo = rs.getString("booklogofname");
				}
				SqlUtil.close(rs);
				// 民润店
			//} else if (controltype == 2) {
			//	company = "MR";
			//	this.logo = VSSConfig.getInstance().getPrintTobatoLogo();
			//}
			if(token.site.getSid()==6){
				this.title = "送货单";
			}
			if(token.site.getSid()==5){
				if(logistics==1){
					this.title = "直送订单";
				}else if(logistics==2){
					this.title="直通订单";
				}
			}
			this.title = company + this.title+"-已归档";;
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

		/**
		 * 订货审批单id
		 */
		ss = (String[]) parms.get("refsheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("a.refsheetid IN (" + val_sheetid.toString4String() + ") ");
		}

		/**
		 * 根据订货地过滤
		 */
		ss = (String[]) parms.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("a.shopid IN (" + val_shopid.toString4String() + ") ");
		}

		/**
		 * 根据收货地过滤
		 */
		ss = (String[]) parms.get("destshopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("a.destshopid IN (" + val_shopid.toString4String() + ") ");
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

		ss = (String[]) parms.get("deadline_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.deadline) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("deadline_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.deadline) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("orderdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.orderdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("orderdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.orderdate) <= " + ValueAdapter.std2mdy(date));
		}

		return filter;
	}
}

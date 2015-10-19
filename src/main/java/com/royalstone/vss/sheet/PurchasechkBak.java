package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

public class PurchasechkBak extends SheetService {

	static String	sql4Search		= "";

	static String	sql4Head		= " SELECT p.sheetid, "
											+ " p.venderid, v.vendername, v.address venderaddr,v.telno vendertel,v.faxno venderfax, "
											+ " p.paytypeid, pt.paytypename, substr(pt.paytypename,1,1) paytypeflag, "
											+ " p.sgroupid, sg.categoryname sgroupname, "
											+ " p.orderdate, p.validDays, p.deadline, p.vdeliverdate,p.discountrate,"
											+ " p.logistics, name4code(p.logistics, 'logistics') logisticsname, p.logistics, "
											+ " p.note, c.releasedate,c.readtime, status4cat(c.status) flagname,p.editor,p.editdate, p.checker, p.checkdate "
											+ " FROM purchase0_bak p"
											+ " JOIN cat_order_bak c ON ( c.sheetid=p.sheetid ) "
											+ " JOIN vender v ON ( v.venderid=p.venderid ) "
											+ " JOIN paytype pt ON ( pt.paytypeid=p.paytypeid ) "
											+ " left join category  sg ON ( sg.categoryid = p.sgroupid ) "
											+ " WHERE p.sheetid = ? ";

	static String	sql4Body		= "SELECT p.sheetid,p.destshopid, s.shopname destshopname,"
											+ " p.shopid, sh.shopname shopname,sh.shopstatus,"
											+ " i.goodsid, i.barcode, g.goodsname, g.spec, g.unitname,g.ycomp,g.qadays,"
											+ " i.qty, i.pkgqty, i.pkgvolume, trunc((i.qty / i.pkgvolume)) as pkqty, i.memo,nvl(i.presentqty,0) presentqty, "
											+ " i.concost,i.cost,i.firstdisc,(i.qty*i.cost) sumcost,(i.qty*i.concost) sumconcost  "
											+ " FROM purchase_bak p"
											+ " JOIN purchaseitem_bak i ON (p.sheetid=i.sheetid) "
											+ " JOIN goods g ON (i.goodsid=g.goodsid) "
											+ " JOIN shop s ON (s.shopid=p.destshopid) "
											+ " JOIN shop sh ON (sh.shopid=p.shopid) "
											+ " WHERE p.refsheetid= ? order by p.shopid,g.deptid,i.goodsid";
	static String	sql4cat			= " SELECT "
											+ " p.sheetid,"
											+ " p.destshopid, s.shopname destshopname,s.address,s.telno,"
											+ " p.shopid, sh.shopname shopname, p.deliverdate, p.checkdate, "
											+ " p.deliverTimeid,dt.startTime, dt.endTime,s.controltype,flag4sheet(p.flag) flagname,p.flag "
											+ " FROM purchase_bak p"
											+ " JOIN shop s ON (s.shopid=p.destshopid)"
											+ " JOIN shop sh ON (sh.shopid=p.shopid)"
											+ " LEFT OUTER JOIN DeliverTime dt ON ( dt.DeliverID = p.deliverTimeid )"
											+ " WHERE refsheetid = ? order by p.shopid ";
	static String	sql4group		= " select i.goodsid, Max(i.barcode) barcode, MAX(g.goodsname) goodsname,MAX(g.spec) spec, max(g.ycomp) ycomp,max(g.qadays) qadays,"
											+ " MAX(g.unitname) unitname,MAX(i.pkgqty) pkgqty, MAX(i.pkgvolume) pkgvolume, "
											+ " trunc(sum(i.qty / i.pkgvolume)) as pkqty,trunc(sum(i.qty*i.cost)/sum(i.qty),2) cost,MIN(i.concost) concost,MIN(i.firstdisc) firstdisc,"
											+ " sum(i.qty) qty, sum(i.qty*i.cost) sumcost, sum(presentqty) presentqty "
											+ " from purchaseitem_bak i "
											+ " JOIN goods g ON (i.goodsid=g.goodsid) "
											+ " WHERE i.sheetid in (select sheetid from purchase_bak where refsheetid=?) "
											+ " group by i.goodsid order by 1 ";

	static String	tableName		= "purchase0_bak";
	static String	catTableName	= "cat_order_bak";
	static String	tt				= "订货审批单";

	public PurchasechkBak(Connection conn,Token token,String cmid) {
		super(conn, token,cmid, sql4Search,sql4Head, sql4Body, tableName, catTableName, tt);
		this.majorName = "sgroupid";
	}

	@Override
	public Element getBody(String sheetid) {
		return SqlUtil.getRowSetElement(conn, sql4Body, sheetid, "bodydetail");
	}

	/**
	 * 订货审批单对应要货门店清单.
	 * 
	 * @return
	 */
	private Element getOderCatalogue(String sheetid) {
		return SqlUtil.getRowSetElement(conn, sql4cat, sheetid, "body");
	}

	/**
	 * 按单品汇总
	 * 
	 * @return
	 */
	private Element getGoodsGroup(String sheetid) {
		return SqlUtil.getRowSetElement(conn, sql4group, sheetid, "goodsgroup");
	}

	@Override
	public int getCount(Filter filter) {
		String sql = " SELECT count( DISTINCT p0.sheetid ) FROM purchase0_bak p0 "
				+ " JOIN purchase_bak p ON ( p0.sheetid = p.refsheetid ) "
				+ " JOIN cat_order_bak c ON ( c.sheetid = p0.sheetid ) WHERE " + filter.toString();

		String temp = SqlUtil.querySQL4SingleColumn(conn, sql).get(0);
		int rows = 0;
		if (temp != null) {
			rows = Integer.parseInt(temp);
		}

		return rows;
	}

	@Override
	public Element search(Map parms) {
		Filter filter = cookFilter(parms);

		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException(
				"满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = " SELECT "
				+ " t.sheetid, status4purchase( t.status ) status, t.validdays, "
				+ " t.orderdate, t.venderid, v.vendername, sg.categoryname majorname, pay.paytypename, "
				+ " t.deadline, name4code(t.logistics,'logistics') as logistics, t.note, "
				+ " t.editor, t.editdate, t.checker, t.checkdate, t.releasedate, t.readtime ,status4purchasetype(t.purchasetype) purchasetype "
				+ " FROM (SELECT DISTINCT p0.sheetid,p0.validdays,p0.orderdate,p0.venderid,p0.purchasetype, "
				+ " p0.deadline,p0.logistics,p0.note,p0.editor,p0.editdate,p0.checker,p0.checkdate, "
				+ " p0.paytypeid,p0.sgroupid,c.status,c.releasedate, c.readtime  FROM purchase0_bak  p0  "
				+ " JOIN purchase_bak  p ON ( p0.sheetid = p.refsheetid ) "
				+ " JOIN cat_order_bak c ON ( c.sheetid = p0.sheetid ) WHERE " + filter.toString() + ") t "
				+ " JOIN vender v ON ( v.venderid = t.venderid ) "
				+ " JOIN paytype pay ON ( pay.paytypeid = t.paytypeid ) "
				+ " left JOIN category sg ON ( sg.categoryid = t.sgroupid ) "
				+ " ORDER BY t.checkdate DESC, t.status";

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
		elm_sheet.addContent(getOderCatalogue(sheetid));
		elm_sheet.addContent(getBody(sheetid));
		elm_sheet.addContent(getGoodsGroup(sheetid));
		return elm_sheet;
	}

	public void setPrintInfo(String sheetid) {
		String company = null;
		String shopid = null;
		int controltype = 0;
		int majorid = 0;
		// controltype 门店控制符合，MR公司用
		// 取得门店和课类,抬头公司
		String sql_sel_shop = "select a.destshopid,a." + majorName + ",s.controltype,s.shopname from purchase_bak a "
				+ " inner join shop s on s.shopid=a.destshopid "
				+ " where refsheetid=? and rownum=1";

		ResultSet rs = SqlUtil.queryPS(conn, sql_sel_shop, sheetid);
		try {
			if (rs.next()) {
				shopid = rs.getString("destshopid") + "X";
				majorid = rs.getInt(majorName);
				controltype = rs.getInt("controltype");
				company = SqlUtil.fromLocal(rs.getString("shopname"));
				//this.logo = rs.getString("booklogofname");
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
			/*} else if (controltype == 2) {
				company = "MR";
				this.logo = VSSConfig.getInstance().getPrintTobatoLogo();
			}*/

			if(token.site.getSid()==6){
				this.title = "送货单";
			}
			
			this.title = company + this.title+"-已归档";
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
			filter.add("c.venderid IN (" + val_vender.toString4String() + ") ");
		}

		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("c.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		ss = (String[]) parms.get("sheetid_purchase");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("p.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		/**
		 * 根据订货地过滤
		 */
		ss = (String[]) parms.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("p.shopid IN (" + val_shopid.toString4String() + ") ");
		}

		/**
		 * 根据收货地过滤
		 */
		ss = (String[]) parms.get("destshopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("p.destshopid IN (" + val_shopid.toString4String() + ") ");
		}

		ss = (String[]) parms.get("status");
		if (ss != null && ss.length > 0) {
			Values val_status = new Values(ss);
			filter.add("c.status IN (" + val_status.toString4String() + ") ");
		}

		ss = (String[]) parms.get("logistics");
		if (ss != null && ss.length > 0) {
			Values val_logistics = new Values(ss);
			filter.add("p0.logistics IN (" + val_logistics.toString4String() + ") ");
		}

		ss = (String[]) parms.get("editdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.editdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("editdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.editdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("checkdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.checkdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("checkdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.checkdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("deadline_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.deadline) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("deadline_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.deadline) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("orderdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.orderdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("orderdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.orderdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("releasedate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(c.releasedate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("releasedate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(c.releasedate) <= " + ValueAdapter.std2mdy(date));
		}
		return filter;
	}

}

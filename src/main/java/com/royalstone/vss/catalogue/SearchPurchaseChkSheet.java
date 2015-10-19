package com.royalstone.vss.catalogue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 
 * 此模块用于查询订货审批单目录, 可以查询各种状态的单据.
 * 
 */
public class SearchPurchaseChkSheet {
	final private String	month;
	final private Connection	conn;
	private Map					parms;
	
	public SearchPurchaseChkSheet(Connection conn, Map parms, String month) throws SQLException, InvalidDataException {
		this.conn = conn;
		this.parms = parms;
		this.month = month;
	}

	private int getCount(Filter filter) throws SQLException, InvalidDataException {
		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");
		String sql = " SELECT count( DISTINCT p0.sheetid ) " + " FROM purchase0" + this.month + " p0  "
				+ " JOIN purchase" + this.month + " p ON ( p0.sheetid = p.refsheetid ) " + " JOIN cat_order"
				+ this.month + " c ON ( c.sheetid = p0.sheetid ) " + " WHERE " + filter.toString();
		
		
		String temp = SqlUtil.querySQL4SingleColumn(conn, sql).get(0);
		int rows = 0;
		if(temp!=null){
			rows = Integer.parseInt(temp);
		}
		
		return rows;
	}

	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_vender = new Values(ss);
			filter.add("c.venderid IN (" + val_vender.toString4String() + ") ");
		}

		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("c.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		ss = (String[]) map.get("sheetid_purchase");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("p.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}
		
		/**
		 * 根据订货地过滤
		 */
		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("p.shopid IN (" + val_shopid.toString4String() + ") ");
		}

		/**
		 * 根据收货地过滤
		 */
		ss = (String[]) map.get("destshopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("p.destshopid IN (" + val_shopid.toString4String() + ") ");
		}

		ss = (String[]) map.get("status");
		if (ss != null && ss.length > 0) {
			Values val_status = new Values(ss);
			filter.add("c.status IN (" + val_status.toString4String() + ") ");
		}

		ss = (String[]) map.get("logistics");
		if (ss != null && ss.length > 0) {
			Values val_logistics = new Values(ss);
			filter.add("p0.logistics IN (" + val_logistics.toString4String() + ") ");
		}
		
		ss = (String[]) map.get("editdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("(p0.editdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("editdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("(p0.editdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("checkdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.checkdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("checkdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.checkdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("deadline_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.deadline) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("deadline_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.deadline) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("orderdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.orderdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("orderdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(p0.orderdate) <= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("releasedate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(c.releasedate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) map.get("releasedate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(c.releasedate) <= " + ValueAdapter.std2mdy(date));
		}

		return filter;
	}

	public Element toElement() throws SQLException, InvalidDataException {
		Filter filter = cookFilter(parms);

		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");

		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = " SELECT "
				+ " t.sheetid, status4purchase( t.status ) status, t.validdays, "
				+ " t.orderdate, t.venderid, v.vendername, sg.categoryname majorname, pay.paytypename, "
				+ " t.deadline, name4code(t.logistics,'logistics') as logistics, t.note, "
				+ " t.editor, t.editdate, t.checker, t.checkdate, t.releasedate, t.readtime ,status4purchasetype(t.purchasetype) purchasetype "
				+ " FROM (SELECT DISTINCT p0.sheetid,p0.validdays,p0.orderdate,p0.venderid,p0.purchasetype, " +
				" p0.deadline,p0.logistics,p0.note,p0.editor,p0.editdate,p0.checker,p0.checkdate, " +
				" p0.paytypeid,p0.sgroupid,c.status,c.releasedate, c.readtime " 
				+ " FROM purchase0" + this.month + " p0  "
				+ " JOIN purchase" + this.month + " p ON ( p0.sheetid = p.refsheetid ) " + " JOIN cat_order"
				+ this.month + " c ON ( c.sheetid = p0.sheetid ) " + " WHERE " + filter.toString() + ") t "
				+ " JOIN vender v ON ( v.venderid = t.venderid ) "
				+ " JOIN paytype pay ON ( pay.paytypeid = t.paytypeid ) "
				+ " left JOIN category sg ON ( sg.categoryid = t.sgroupid ) "
				+ " ORDER BY t.checkdate DESC, t.status";

		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "rowset");
		elm_cat.setAttribute("row_total", "" + count);
		return elm_cat;
	}

	public void cookExcelFile(File file) throws SQLException, InvalidDataException, IOException {
		String[] title = { "供应商", "订货审批单号", "状态", "操作时间", "有效期（天）", "订货日期", "供应商编码", "供应商名称", "课类", "结算方式",
				"截止日期", "物流模式", "备注", "制单人", "制单日期", "审核人", "审核日期" };

		Filter filter = cookFilter(parms);
		if (filter.count() == 0) throw new InvalidDataException("请设置查询过滤条件.");
		int count = this.getCount(filter);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = " SELECT "
				+ " t.venderid,t.sheetid, status4purchase( t.status ) status, TO_CHAR(t.readtime,'YYYY-MM-DD HH24:MI:SS') , t.validdays, "
				+ " t.orderdate, t.venderid, v.vendername, sg.categoryname , pay.paytypename, "
				+ " t.deadline, name4code(t.logistics,'logistics') as logistics, t.note, "
				+ " t.editor, t.editdate, t.checker, t.checkdate "
				+ " FROM (SELECT DISTINCT p0.sheetid,p0.validdays,p0.orderdate,p0.venderid,p0.purchasetype, " +
				" p0.deadline,p0.logistics,p0.note,p0.editor,p0.editdate,p0.checker,p0.checkdate, " +
				" p0.paytypeid,p0.sgroupid,c.status,c.releasedate, c.readtime " 
				+ " FROM purchase0" + this.month + " p0  "
				+ " JOIN purchase" + this.month + " p ON ( p0.sheetid = p.refsheetid ) " + " JOIN cat_order"
				+ this.month + " c ON ( c.sheetid = p0.sheetid ) " + " WHERE " + filter.toString() + ") t "
				+ " JOIN vender v ON ( v.venderid = t.venderid ) "
				+ " JOIN paytype pay ON ( pay.paytypeid = t.paytypeid ) " 
				+ " left JOIN category sg ON ( sg.categoryid = t.sgroupid ) "
				+ " ORDER BY t.status, t.checkdate DESC";

		ResultSet rs = SqlUtil.querySQL(conn, sql);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		excel.cookExcelFile(file, rs, title, "订单");
		SqlUtil.close(rs);
		
	}
}

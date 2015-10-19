package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.fiscal.FiscalValue;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于查询付款单/付款申请单的内容, 包括单据头, 已经选业务单据, 扣项单, 发票等信息. sheetid 必须是一个已经存在的单据,
 * 如果指定的单据号在数据库中找不到对应记录, 则抛出例外.
 * 
 * @author liuwendong
 * @modefication baijian
 */
public class ShowPaymentSheet {

	public ShowPaymentSheet(Connection conn, String sheetid) throws SQLException {
		this.conn = conn;
		this.sheetid = sheetid;

		this.tab_head = "v_paymentsheet";
		this.tab_item = "v_paymentsheetitem";
		this.tab_invoice = "v_paymentsheetdlt";
		this.tab_chargesum = "v_chargesum";
	}

	/**
	 * 此方法用于查询付款单的头部信息 NOTE: real_pay: 表示实际应支付金额( 已扣除"补税差应付调整金额" 及 "电汇费" )
	 * 
	 * @return XML element.
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Element getHead() throws SQLException, IOException {
		String sql = " SELECT " + " p.sheetid,  b.bookname, b.booktitle,b.booklogofname,"
				+ " p.venderid, v.vendername, vb.bankname, vb.accno bankaccno, p.finamt,"
				+ " paymode.paymodename paymodename, p.payableamt, suspayamt,chequeamt,"
				+ " p.chargeamt, p.planpaydate,p.TaxAmt17,p.TaxAmt13, "
				+ " p.invtotalamt17,p.invtotalamt13,p.invtotalamt0,p.payamt, p.note, p.editor, p.editdate, '"
				+ SqlUtil.toLocal(PayamtToChinese()) + "' as real_pay FROM " + this.tab_head
				+ " p " + " JOIN book b ON (b.bookno=p.payshopid) "
				+ " LEFT JOIN paymode ON (p.paymodeid=paymode.paymodeid) "
				+ " JOIN vender v ON (v.venderid=p.venderid) "
				+ " LEFT JOIN vender_bank vb ON (vb.venderid=p.venderid) " + " WHERE sheetid = ? ";
		System.out.println(sql);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_sheethead = adapter.getRowSetElement("head", "rows");
		rs.close();
		pstmt.close();
		return elm_sheethead;
	}

	/**
	 * 此方法用于查询付款单内所选的业务单据清单
	 * 
	 * @return XML element.
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Element getSheetset() throws SQLException, IOException {
		String sql_sheet = " SELECT "
				+ " p.sheetid, p.docno, p.doctype, sn.sheettypename doctypename, "
				+ " p.shopid, s.shopname, "
				+ " name4code(p.logisticsid,'logistics') as logisticsid, "
				+ " p.noteremark, p.saleamt,p.paydocamt, p.paytaxamt17, p.paytaxamt13, "
				+ " p.docamt, p.docamt17, p.taxamt13, "
				+ " to_char(p.docdate,'YYYY-MM-DD') docdate,p.majorid,p.paytypeid,pt.paytypename "
				+ " FROM "
				+ this.tab_item
				+ " p "
				+ " JOIN  shop s on ( s.shopid=p.shopid ) "
				+ " JOIN  sheettype sn on ( sn.sheettype = p.doctype  and p.doctype<>5201 and p.doctype<>5258) "
				+ " left join paytype pt on (pt.paytypeid=p.paytypeid) "
				+ " WHERE p.sheetid= ? order by p.shopid";

		System.out.println(sql_sheet);
		PreparedStatement pstmt = conn.prepareStatement(sql_sheet);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_charge = adapter.getRowSetElement("unpaidsheet_list", "rows");
		rs.close();
		pstmt.close();
		return new Element("sheetset").addContent(elm_charge);
	}

	/**
	 * 此方法用于查询付款单内所选的业务单据清单，按门店分组
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Element getSheetsetGroupByShop() throws SQLException, IOException {
		String sql4unpaidsheet = " SELECT "
				+ " p.sheetid, p.docno, p.doctype, sn.sheettypename doctypename, "
				+ " p.shopid, s.shopname, "
				+ " name4code(p.logisticsid,'logistics') as logisticsid, "
				+ " p.noteremark, p.saleamt,p.paydocamt, p.paytaxamt17, p.paytaxamt13, (p.paydocamt+p.paytaxamt17+p.paytaxamt13) total_payamt, "
				+ " p.docamt, p.docamt17, p.taxamt13, "
				+ " to_char(p.docdate,'YYYY-MM-DD') docdate,p.majorid,p.paytypeid,pt.paytypename "
				+ " FROM "
				+ this.tab_item
				+ " p "
				+ " JOIN  shop s on ( s.shopid=p.shopid ) "
				+ " JOIN  sheettype sn on ( sn.sheettype = p.doctype  and p.doctype<>5201 and p.doctype<>5258 and p.doctype<>5205 ) "
				+ " left join paytype pt on (pt.paytypeid=p.paytypeid) "
				+ " WHERE p.sheetid= ? order by p.shopid";

		String sql4csalecost = "select sh.shopid,sh.shopname,s.name doctypename,to_char(p.docdate,'YYYYMM') docdate,sum(p.saleamt) saleamt,sum(p.paydocamt+p.paytaxamt17+p.paytaxamt13) total_payamt "
				+ "from "
				+ this.tab_item
				+ " p join shop sh on(p.shopid=sh.shopid) "
				+ "join serialnumber s on(p.doctype=s.serialid) "
				+ "where p.sheetid= ? and p.doctype=5205 "
				+ " group by sh.shopid,sh.shopname,s.name,to_char(p.docdate,'YYYYMM') order by sh.shopid";

		System.out.println(sql4unpaidsheet);
		PreparedStatement pstmt = conn.prepareStatement(sql4unpaidsheet);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elmSet = new Element("sheetset");
		Element elm_unpaid = adapter.getRowSetElement("unpaidsheet_list", "rows");
		elmSet.addContent(elm_unpaid);
		rs.close();
		pstmt.close();

		pstmt = conn.prepareStatement(sql4csalecost);
		pstmt.setString(1, this.sheetid);
		rs = pstmt.executeQuery();
		XResultAdapter adapter2 = new XResultAdapter(rs);
		Element elm_salecost = adapter2.getRowSetElement("salecost_list", "rows");
		elmSet.addContent(elm_salecost);
		rs.close();
		pstmt.close();

		return elmSet;
	}

	public Element getChargeGroupByShop(int with_tax) throws InvalidDataException, SQLException, IOException {

		String sql_charge = " SELECT  " + " c.noteno docno, c.shopid, s.shopname, "
				+ " c.chargecodeid, cc.chargename, c.chargeamt,  "
				+ " name4code( c.invoicemode,'invoicemode' ) as invoicemode, c.noteremark  " + " FROM "
				+ this.tab_chargesum + " c " + " JOIN shop s ON ( s.shopid = c.shopid ) "
				+ " JOIN chargecode cc ON ( cc.chargecodeid = c.chargecodeid ) " + " JOIN " + this.tab_item
				+ " p ON (p.docno = c.noteno) " + " WHERE  p.sheetid = ? " + " AND c.invoicemode =? "
				+ " union " + " select '' as docno, i.shopid, s.shopname,   "
				+ " i.chargecodeid, cc.chargename,sum(i.docamt+i.taxamt17+taxamt13)*-1 as chargeamt,  "
				+ " '' as invoicemode,'' noteremark  " + " from " + this.tab_item + " i "
				+ " JOIN shop s ON ( s.shopid = i.shopid ) "
				+ " JOIN chargecode cc ON ( cc.chargecodeid = i.chargecodeid ) "
				+ " WHERE i.sheetid=? and i.doctype=5258 and i.invoicemode=? "
				+ " group by i.shopid,s.shopname,i.chargecodeid, cc.chargename ";
		System.out.println(sql_charge);
		PreparedStatement pstmt = conn.prepareStatement(sql_charge);
		pstmt.setString(1, this.sheetid);
		pstmt.setInt(2, with_tax);
		pstmt.setString(3, this.sheetid);
		pstmt.setInt(4, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		String sheetName = (with_tax == 1) ? "chargesum_with_tax" : "chargesum_without_tax";
		Element elm_charge = adapter.getRowSetElement(sheetName, "rows");
		rs.close();
		pstmt.close();

		return elm_charge;

	}

	public Element getChargeGroupByCharge(int with_tax) throws InvalidDataException, SQLException, IOException {

		String sql_charge = " SELECT  " + " c.noteno docno, c.shopid, s.shopname, "
				+ " c.chargecodeid, cc.chargename, c.chargeamt,  "
				+ " name4code( c.invoicemode,'invoicemode' ) as invoicemode, c.noteremark  " + " FROM "
				+ this.tab_chargesum + " c " + " JOIN shop s ON ( s.shopid = c.shopid ) "
				+ " JOIN chargecode cc ON ( cc.chargecodeid = c.chargecodeid ) " + " JOIN " + this.tab_item
				+ " p ON (p.docno = c.noteno) " + " WHERE  p.sheetid = ? " + " AND c.invoicemode =? "
				+ " union " + " select '' as docno, '' as shopid, '' as shopname,   "
				+ " i.chargecodeid, cc.chargename,sum(i.docamt+i.taxamt17+taxamt13)*-1 as chargeamt,  "
				+ " '' as invoicemode,'' noteremark  " + " from " + this.tab_item + " i "
				+ " JOIN shop s ON ( s.shopid = i.shopid ) "
				+ " JOIN chargecode cc ON ( cc.chargecodeid = i.chargecodeid ) "
				+ " WHERE i.sheetid=? and i.doctype=5258 and i.invoicemode=? "
				+ " group by i.chargecodeid, cc.chargename ";
		System.out.println(sql_charge);
		PreparedStatement pstmt = conn.prepareStatement(sql_charge);
		pstmt.setString(1, this.sheetid);
		pstmt.setInt(2, with_tax);
		pstmt.setString(3, this.sheetid);
		pstmt.setInt(4, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		String sheetName = (with_tax == 1) ? "chargesum_with_tax" : "chargesum_without_tax";
		Element elm_charge = adapter.getRowSetElement(sheetName, "rows");
		rs.close();
		pstmt.close();

		return elm_charge;

	}

	/**
	 * 此方法用于查询付款单内的扣项信息. 对于状态为"审定"及以后的付款单, 包括固定扣项; 对于状态为"审定"之前的付款单, 不包括固定扣项;
	 * 
	 * @param with_tax
	 *            主调模块用此参数表达待查询的扣项是否含税. 不含税: {0} 含税: {1} 含税+不含税: {0,1} NOTE:
	 *            对参数值的合理性, 本方法不作检查.
	 * @return XML element.
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public Element getChargesum(int with_tax) throws SQLException, InvalidDataException, IOException {
		String sql_charge = " SELECT  "
				+ " c.noteno docno, c.shopid, s.shopname, "
				+ " c.chargecodeid, cc.chargename, c.chargeamt, '' ksheetid, "
				+ " name4code( c.invoicemode,'invoicemode' ) as invoicemode, c.noteremark  "
				+ " FROM "
				+ this.tab_chargesum
				+ " c "
				+ " JOIN shop s ON ( s.shopid = c.shopid ) "
				+ " JOIN chargecode cc ON ( cc.chargecodeid = c.chargecodeid ) "
				+ " JOIN "
				+ this.tab_item
				+ " p ON (p.docno = c.noteno) "
				+ " WHERE  p.sheetid = ? "
				+ " AND  c.invoicemode = ? "
				+ " union "
				+ " select i.docno, i.shopid, s.shopname, "
				+ " i.chargecodeid, cc.chargename,(i.docamt+i.taxamt17+taxamt13)*-1 as chargeamt, '' ksheetid, "
				+ " name4code( i.invoicemode,'invoicemode' ) as invoicemode,i.noteremark " + " from "
				+ this.tab_item + " i JOIN shop s ON ( s.shopid = i.shopid ) "
				+ " JOIN chargecode cc ON ( cc.chargecodeid = i.chargecodeid ) "
				+ " WHERE i.sheetid=? and i.doctype=5258 and i.invoicemode =? ";
		System.out.println(sql_charge);
		PreparedStatement pstmt = conn.prepareStatement(sql_charge);
		pstmt.setString(1, this.sheetid);
		pstmt.setInt(2, with_tax);
		pstmt.setString(3, this.sheetid);
		pstmt.setInt(4, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		String sheetName = (with_tax == 1) ? "chargesum_with_tax" : "chargesum_without_tax";
		Element elm_charge = adapter.getRowSetElement(sheetName, "rows");
		rs.close();
		pstmt.close();

		return elm_charge;
	}

	/**
	 * 查询没有付款确认前的付款单的固定扣项信息
	 * 
	 * @param with_tax
	 *            主调模块用此参数表达待查询的扣项是否含税. 不含税: {0} 含税: {1} 含税+不含税: {0,1}
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public Element getChargeGenerated(int with_tax) throws SQLException, InvalidDataException, IOException {
		String sql_charge = " SELECT p.chargecodeid docno, "
				+ " p.shopid, s.shopname, "
				+ " p.chargecodeid, c.chargename, "
				+ " -p.chargeamt chargeamt, "
				+ " name4code( p.invoicemode,'invoicemode' ) as invoicemode, p.noteremark,name4code(p.settleflag,'settlemode') as settleflag "
				+ " FROM paymentnotecharge p " + " JOIN chargecode c ON (p.chargecodeid=c.chargecodeid) "
				+ " JOIN shop s ON (s.shopid=p.shopid) " + " WHERE  p.sheetid = ? "
				+ " AND p.invoicemode =? and p.settleflag=1 ";
		System.out.println(sql_charge);
		PreparedStatement pstmt = conn.prepareStatement(sql_charge);
		pstmt.setString(1, this.sheetid);
		pstmt.setInt(2, with_tax);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		String sheetName = (with_tax == 1) ? "chargegenerated_with_tax" : "chargegenerated_without_tax";
		Element elm_charge = adapter.getRowSetElement(sheetName, "rows");
		rs.close();
		pstmt.close();
		return elm_charge;
	}

	/**
	 * 此方法用于查询付款单的发票信息
	 * 
	 * @return XML element.
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Element getInvoice() throws SQLException, IOException {
		String sql_inovice = " SELECT  " + " d.sheetid, d.invoiceno, d.invoicedate, d.goodsdesc, "
				+ " d.taxrate, d.taxableamt, d.taxamt FROM " + this.tab_invoice + " d "
				+ " WHERE d.sheetid = ?  ";
		System.out.println(sql_inovice);
		PreparedStatement pstmt = conn.prepareStatement(sql_inovice);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_invoice = adapter.getRowSetElement("paymentnotedtl", "rows");
		rs.close();
		pstmt.close();
		return elm_invoice;
	}

	/**
	 * 从数据库查询付款单的应付金额, 并转换成大写中文.
	 * @return 字符串, 内容为金额的中文大写表示
	 * @throws SQLException
	 */
	private String PayamtToChinese() throws SQLException {
		String payamt = "NAC";
		String sql_amt = " SELECT chequeamt FROM " + this.tab_head + " WHERE sheetid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql_amt);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			double get_payamt = rs.getDouble(1);
			FiscalValue f = new FiscalValue(get_payamt);
			payamt = f.toChinese();
		}
		rs.close();
		pstmt.close();
		return payamt;
	}

	/**
	 * 此方法用于查询指定付款单的供应商编码. 付款单状态可能为: 已经结算/待结算.
	 * 
	 * @author baijian
	 * @param sheetid
	 *            付款单号
	 * @return 供应商编码
	 * @throws SQLException
	 */
	public String getPaymentnoteVenderid(String sheetid) throws SQLException {
		String venderid = "";
		String sql = " SELECT venderid FROM " + tab_head + " WHERE sheetid=? ";
		System.out.println(sql);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) venderid = rs.getString(1);
		else
			throw new SQLException("sheet NOT_FOUND:" + sheetid, "NOT_FOUND", 100);

		return venderid;
	}

	final private Connection	conn;
	private String				sheetid;
	private String				tab_head;
	private String				tab_item;
	private String				tab_invoice;
	private String				tab_chargesum;

	public void sjPrint(String sheetid) {
		String sqlUp = "update paymentnotevenderask set sjprintdate=sysdate,sjflag=1 where sheetid=?";

		String sqlIns = "insert into paymentnotevenderask(sheetid,sjflag,sjprintdate) values(?,1,sysdate)";

		// 先更新，更新失败，直接插入

		int rows = SqlUtil.executePS(conn, sqlUp, new String[] { sheetid });

		if (rows == 0) {
			rows = SqlUtil.executePS(conn, sqlIns, new String[] { sheetid });
		}

	}
}
package com.royalstone.vss.net;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;

import com.royalstone.common.Sheetid;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XErr;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于实现VSS中装车单录入的后台处理. 包括:检查数据有效性, 生成装车录入单. 数据有效性检查包括以下方面: 1) 基本格式检查,
 * 金额是否有效数字. 2) 预约单据检查, 系统中是否存在此单据. 3)单据是否属于指定的供应商。
 * 
 * 
 * 
 */
public class NetInCarManager {
	public NetInCarManager(Connection conn, Token token, String venderid) {
		this.conn = conn;
		this.token = token;
	}

	/**
	 * 此方法用于检查前台上传的数据是否有效. 1) 基本格式检查, 金额是否有效数字. 2) 商品检查, 输入的预约单中是否存在此商品信息. 3)
	 * 单据状态检查
	 * 
	 * 
	 * @param elm_data
	 * 前台传入的数据
	 */

	public Element validate(String order_serial, String venderid, Element elm_data) throws Exception {

		List<Element> lst = elm_data.getChildren("row");
		if (lst.size() == 0) {
			throw new InvalidDataException("没有获得有效的数据，请重新上传");
		}

		Element elm_ret = checkValue(elm_data, order_serial, venderid);
		return elm_ret;
	}

	public XErr xerr() {
		XErr xerr;
		if (this.rows_err == 0) {
			xerr = new XErr(0, "OK");
		} else {
			xerr = new XErr(-1, "上传数据存在错误,请更正后重新上传.");
		}
		return xerr;
	}

	/**
	 * 检查金额格式. 如果格式有误,就不再作进一步的处理.
	 * 
	 * @param venderid
	 * @param order_serial
	 * 
	 */
	private Element checkValue(Element elm_data, String order_serial, String venderid) {
		String sql = "select count(*) from netorderhead a ,netorderdetail b,purchaseitem d,goods e "
				+ " where a.order_serial=b.order_serial and b.po_no=d.sheetid	and d.goodsid=e.goodsid "
				+ " and a.order_serial=? and a.supplier_no=? and e.barcode=?";
		Element elm_list = new Element("list");
		List lst = elm_data.getChildren("row");
		this.rows_err = 0;
		boolean isOK;
		HashSet<Integer> seqnoSet = new HashSet<Integer>();
		for (int i = 0; i < lst.size(); i++) {
			isOK=true;
			Element record = (Element) lst.get(i);
			String seqno = record.getAttributeValue("seqno");
			seqno = (seqno == null) ? "" : seqno.trim();
			String goodsid = record.getAttributeValue("goodsid");
			
			String spec = record.getAttributeValue("spec");
			String packageid = record.getAttributeValue("packageid");
			String qty = record.getAttributeValue("qty");
			qty = (qty == null) ? "" : qty.trim();

			Element elm_row = new Element("row");
			Element elm_seqno = new Element("seqno");
			Element elm_goodsid = new Element("goodsid");
			Element elm_spec = new Element("spec");
			Element elm_qty = new Element("qty");
			Element elm_message = new Element("message");
			Element elm_packageid = new Element("packageid");

			goodsid = s2i(goodsid);
			qty=s2i(qty);
			
			elm_seqno.setText(seqno);
			elm_goodsid.setText(goodsid);
			elm_spec.setText(spec);
			elm_packageid.setText(packageid);
			elm_qty.setText(qty);
			try {
				int ino= Integer.parseInt(seqno.trim());
				if(seqnoSet.contains(ino)){
					this.rows_err++;
					isOK=false;
					elm_message.setText("装车顺序号重复：" + seqno);
					elm_seqno.setAttribute("class", "warning");
				}else{
					seqnoSet.add(ino);
				}
			} catch (Exception e) {
				this.rows_err++;
				isOK=false;
				elm_message.setText("装车顺序号有误：" + seqno);
				elm_seqno.setAttribute("class", "warning");
			}
			try {
				Double.parseDouble(goodsid);
			} catch (Exception e) {
				e.printStackTrace();
				this.rows_err++;
				isOK=false;
				elm_message.setText("商品条码有误：" + goodsid);
				elm_seqno.setAttribute("class", "warning");
			}
			
			try {
				Double.parseDouble(qty);
			} catch (Exception e) {
				this.rows_err++;
				isOK=false;
				elm_qty.setAttribute("class", "warning");
				elm_message.setText("商品数量有误：" + qty);
				elm_message.setAttribute("class", "warning");
			}

			// 校验商品条码在预约单内
//			List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, order_serial, venderid, goodsid);
//			if (list.size() > 0) {
//				String s = list.get(0);
//				int rows = Integer.parseInt(s);
//				if (rows == 0) {
//					elm_goodsid.setAttribute("class", "warning");
//					elm_message.setText("此商品不在预约单内：" + goodsid);
//					elm_message.setAttribute("class", "warning");
//					this.rows_err++;
//					isOK=false;
//				}
//			}
//			if(isOK){
//				elm_message.setText("OK");
//			}

			elm_row.addContent(elm_seqno);
			elm_row.addContent(elm_goodsid);
			elm_row.addContent(elm_spec);
			elm_row.addContent(elm_qty);
			elm_row.addContent(elm_packageid);
			elm_row.addContent(elm_message);
			elm_list.addContent(elm_row);
		}
		return elm_list;
	}

	private String s2i(String s){
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			try {
				Integer.parseInt(String.valueOf(c));
				res.append(c);
			} catch (Exception e) {
			}
		}
		return res.toString();
	}


	/**
	 * 插入装车主表信息
	 * 
	 */
	private void insertHead(String incarno, String order_serial, String venderid) throws SQLException {
		String sql_ins = " insert into netincarhead(incarno, order_serial, supplier_no, rgst_name, rgst_date) "
				+ " VALUES ( ?, ?, ?, ?,trunc(sysdate) ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setString(1, incarno);
		pstmt.setString(2, order_serial);
		pstmt.setString(3, venderid);
		pstmt.setString(4, this.token.getBusinessid());
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 插入数据到装车明细
	 * 
	 */
	private void insertBody(String incarno, Element elm_data) throws SQLException {
		String sql_ins = "insert into netincardetail(incarno,goodsid,spec,qty,seqno,packageid) VALUES (?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(sql_ins);

		List lst = elm_data.getChildren("row");
		for (int i = 0; i < lst.size(); i++) {
			Element record = (Element) lst.get(i);
			String goodsid = record.getAttributeValue("goodsid");
			goodsid = (goodsid == null) ? "" : s2i(goodsid);;
			String spec = record.getAttributeValue("spec");
			String seqno = record.getAttributeValue("seqno");
			seqno = (seqno == null) ? "" : seqno.trim();
			String qty = record.getAttributeValue("qty");
			qty = (qty == null) ? "" : s2i(qty);
			String packageid = record.getAttributeValue("packageid");
			pstmt.setString(1, incarno);
			pstmt.setString(2, goodsid);
			pstmt.setString(3, spec);
			double value = ValueAdapter.parseDouble(qty);
			pstmt.setDouble(4, value);
			double vzcseqno = ValueAdapter.parseDouble(seqno);
			pstmt.setDouble(5, vzcseqno);
			pstmt.setString(6, packageid);
			pstmt.execute();
		}
		pstmt.close();
	}

	/**
	 * 写接口表
	 */
	private void toITF(String incarno) {
		String sql = "insert into ITF_NETINCARHEAD(incarno, order_serial, supplier_no, rgst_name, rgst_date) "
				+ " select incarno, order_serial, supplier_no, rgst_name, rgst_date from NETINCARHEAD where incarno=?";
		SqlUtil.executePS(conn, sql, incarno);

		sql = "insert into ITF_NETINCARDETAIL(incarno,goodsid,spec,qty,seqno,packageid) "
				+ " select incarno,goodsid,spec,qty,seqno,packageid from netincardetail where incarno=?";
		SqlUtil.executePS(conn, sql, incarno);
	}

	/**
	 * 检查输入的流水号是否存在，是否有效，是否是此供应商
	 * 
	 */
	public Element getCheckOrderserial(String order_serial, String venderid) {
		String sql = " select order_serial, dccode, logistics, supplier_no, request_date, start_time,end_time,flag from netorderhead "
				+ " WHERE order_serial='" + order_serial + "' and supplier_no='" + venderid + "'";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netcheckorderserial");
		return elm_cat;
	}

	/**
	 * 检查输入的流水号是否已经装车
	 * 
	 */
	public Element getCheckOrderserialzc(String order_serial, String venderid) {
		String sql = " select incarno,order_serial,supplier_no,rgst_name,rgst_date from netincarhead "
				+ " WHERE order_serial='" + order_serial + "' and supplier_no='" + venderid + "'";
		Element elm_cat = SqlUtil.getRowSetElement(conn, sql, "netcheckorderserialzc");
		return elm_cat;
	}

	/**
	 * 生成装车单数据
	 * 
	 */
	public String createNetOrderzc(String incarno, String order_serial, String venderid, Element elm_data)
			throws SQLException {
		try {
			conn.setAutoCommit(false);
			incarno = Sheetid.getSheetid(conn, 7006, "");

			// 生成装车单主表
			insertHead(incarno, order_serial, venderid);
			// 生成装车单从表
			insertBody(incarno, elm_data);

			// 写接口表
			toITF(incarno);
			conn.commit();

		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
		return incarno;
	}

	final private Connection	conn;
	private Token				token;
	private int					rows_err	= 0;
}

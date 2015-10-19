/**
 * 
 */
package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.Log;
import com.royalstone.util.daemon.XResultAdapter;

/**
 * @author BaiJian 电器安装单明细显示
 */
public class ShowElecInstall {
	final private Connection conn;
	private String sheetid;

	/**
	 * @param conn
	 * @param sheetid
	 */
	public ShowElecInstall(Connection conn, String sheetid) {
		super();
		this.conn = conn;
		this.sheetid = sheetid;
	}

	public Element getSheetHead() throws SQLException {
		String sql = " SELECT '0' sheettype,ei.sheetid,ei.refsheetid,ei.flag,ei.shopid,s.shopname,ei.saletime,ei.customer, "
				+ " ei.telephone,ei.cman,ei.address,ei.deliverdate,ei.btime,ei.saledate,ei.posid,ei.venderid,ei.serverid,ei.isread,ei.note, "
				+ " ei.editor,ei.operator,ei.editdate,ei.checker, "
				+ " ei.checkdate,ei.TDeliverdate,ei.TtimeID,ei.MPhone "
				+ " FROM elecinstall ei "
				+ " INNER JOIN shop s ON ( s.shopid=ei.shopid ) "
				+ " where ei.sheetid=?";

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_sheethead;

			elm_sheethead = adapter.getRowSetElement("head", "rows");

			rs.close();
			pstmt.close();

			return elm_sheethead;

	}

	public Element getSheetBody() throws SQLException, IOException {
		String sql = " SELECT eii.sheetid,eii.placeid,p.placename,eii.goodsid,g.barcode, "
				+ " g.goodsname,eii.price,eii.qty,eii.discpricev,eii.disccostv "
				+ " FROM elecinstallitem eii "
				+ " INNER JOIN elecinstall ei ON ( ei.sheetid=eii.sheetid ) "
				+ " INNER JOIN goods g ON ( g.goodsid=eii.goodsid ) "
				+ " LEFT OUTER JOIN place p ON ( p.placeid=eii.placeid AND ei.shopid=p.shopid ) "
				+ " where eii.sheetid=?";

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_body = adapter.getRowSetElement("body", "rows");
		rs.close();
		pstmt.close();

		return elm_body;
	}

	public Element getSheetCancelHead() throws SQLException{
		String sql = " SELECT '1' sheettype,ei.sheetid,ei.refsheetid,ei.flag,ei.shopid,s.shopname,ei.saletime,ei.customer, "
				+ " ei.telephone,ei.cman,ei.address,ei.deliverdate,ei.btime,ei.saledate,ei.posid,ei.venderid,ei.serverid,ei.isread,ei.note, "
				+ " ei.editor,ei.operator,ei.editdate,ei.checker, "
				+ " ei.checkdate "
				+ " FROM cancelinstall ei "
				+ " INNER JOIN shop s ON ( s.shopid=ei.shopid ) "
				+ " where ei.sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_sheethead = adapter.getRowSetElement("head", "rows");
		rs.close();
		pstmt.close();

		return elm_sheethead;
	}

	public Element getSheetCancelBody() throws SQLException, IOException {
		String sql = " SELECT eii.sheetid,eii.placeid,p.placename,eii.goodsid,g.barcode, "
				+ " g.goodsname,eii.price,eii.qty,eii.discpricev,eii.disccostv "
				+ " FROM cancelinstallitem eii "
				+ " INNER JOIN cancelinstall ei ON ( ei.sheetid=eii.sheetid ) "
				+ " INNER JOIN goods g ON ( g.goodsid=eii.goodsid ) "
				+ " LEFT OUTER JOIN place p ON ( p.placeid=eii.placeid AND ei.shopid=p.shopid ) "
				+ " where eii.sheetid=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_body = adapter.getRowSetElement("body", "rows");
		rs.close();
		pstmt.close();

		return elm_body;
	}

	public String getCancelSheetid(String sheetid) throws SQLException {
		String refsheetid = null;
		String sql = "select sheetid from cancelinstall where refsheetid=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			refsheetid = rs.getString(1);
		}
		rs.close();
		pstmt.close();

		return refsheetid;
	}

	public String getServerId() throws SQLException {
		String venderid = "";
		String sql = " select serverid from elecinstall where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			venderid = rs.getString(1);
		rs.close();
		pstmt.close();

		return venderid;
	}

	public Element getSheetDetail() throws SQLException, IOException {
		Element elm = new Element("sheet");

		// 如果有对应的取消单，则取对应的取消单信息
		String refsheetid = getCancelSheetid(this.sheetid);
		if (refsheetid == null) {
			elm.setAttribute("sheetname", "电器安装单");
			elm.addContent(getSheetHead());
			elm.addContent(getSheetBody());
		} else {
			elm.setAttribute("sheetname", "电器安装取消单");
			this.sheetid = refsheetid;
			elm.addContent(getSheetCancelHead());
			elm.addContent(getSheetCancelBody());
		}
		return elm;
	}

	public int updateStatus(int oldStatus, int newStatus, String sheetid)
			throws SQLException {

		String sql = "update cat_elecinstall set status=?,readdate=sysdate where sheetid=? and status=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, newStatus);
		pstmt.setString(2, sheetid);
		pstmt.setInt(3, oldStatus);
		int i = pstmt.executeUpdate();
		// System.out.println("电器通知单阅读:更新cat");
		sql = "update elecinstall set isread=1 where sheetid=?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		i = pstmt.executeUpdate();
		pstmt.close();
		// System.out.println("电器通知单阅读:更新elecinstall");
		return i;
	}

	public void insertLog(String sheetid) throws SQLException {
		String sql = "insert into ELECINSTALLREADLOG values(?,1,sysdate)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		pstmt.executeUpdate();
		pstmt.close();
		// System.out.println("电器通知单阅读:log");
	}

	public int updateCancelStatus(int oldStatus, int newStatus, String sheetid)
			throws SQLException {
		String sql = "update cat_elecinstall set status=?,readdate=sysdate where sheetid=? and status=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, newStatus);
		pstmt.setString(2, sheetid);
		pstmt.setInt(3, oldStatus);
		int i = pstmt.executeUpdate();
		// System.out.println("电器取消单阅读:更新cat");
		sql = "update cancelinstall set isread=1 where sheetid=?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		i = pstmt.executeUpdate();
		pstmt.close();
		// System.out.println("电器取消单阅读:更新cancelinstall");
		return i;
	}

	public void insertCancelLog(String sheetid) throws SQLException {
		String sql = "insert into canceleireadlog values(?,1,sysdate)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		pstmt.executeUpdate();
		pstmt.close();
		// System.out.println("电器取消单阅读:log");
	}

}

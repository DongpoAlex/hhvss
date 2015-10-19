package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.XResultAdapter;

/**
 * 此模块用于返回发票申请单信息
 * 
 * @author baijian
 * @param sheetid
 */
public class ShowVenderInvoice {
	private Token	token;

	/**
	 * 构造函数
	 * 
	 * @param conn
	 * @param sheetid
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public ShowVenderInvoice(Connection conn, String sheetid,Token token) throws InvalidDataException {
		if (conn == null)
			throw new InvalidDataException("conn is null.");
		if (sheetid == null || sheetid.length() == 0)
			throw new InvalidDataException("sheetid is invalid.");
		this.conn = conn;
		this.sheetid = sheetid;
		this.token = token;
	}

	/**
	 * 返回发票表头信息
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 *             modify:yzn 07/05/25 关联paymentnote0表取帐套号
	 * @throws IOException 
	 */
	private Element getHead() throws SQLException, InvalidDataException, IOException {
		String sql_head = " SELECT vi.sheetid, "
				+ " vi.refsheetid, vi.venderid, v.vendername, vi.editor, vi.checker, vi.editdate, vi.checkdate, vi.flag,"
				+ " vi.contact, vi.contacttel, b.booktitle,b.booktypeid,b.booklogofname "
				+ " FROM venderinvoice vi INNER JOIN vender v ON (vi.venderid=v.venderid)"
				+ " JOIN paymentnote0 p on (vi.refsheetid=p.sheetid) JOIN book b on (p.bookno=b.bookno) "
				+ " WHERE vi.sheetid = ? ";

		if(token.site.getSid()==11){
			sql_head = " SELECT vi.sheetid, "
				+ " vi.refsheetid, vi.venderid, v.vendername, vi.editor, vi.checker, vi.editdate, vi.checkdate, vi.flag,"
				+ " vi.contact, vi.contacttel, b.booktitle,b.booktypeid,b.booklogofname "
				+ " FROM venderinvoice vi INNER JOIN vender v ON (vi.venderid=v.venderid)"
				+ " JOIN paymentsheet0 p on (vi.refsheetid=p.sheetid) JOIN book b on (p.payshopid=b.bookno) "
				+ " WHERE vi.sheetid = ? ";
		}
		Log.debug("venderingoice getHead", sql_head);
		PreparedStatement pstmt = conn.prepareStatement(sql_head);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_head = adapter.getRowSetElement("head", "row");
		int rows = adapter.rows();
		elm_head.setAttribute("rows", "" + rows);
		elm_head.setAttribute("name", "venderinvoice");
		elm_head.setAttribute("title", title);
		return elm_head;
	}

	/**
	 * 返回发票表体信息
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	private Element getBody() throws SQLException, InvalidDataException, IOException {
		String sql = " SELECT sheetid, seqno, invoiceno, invoicetype, to_char(invoicedate,'YYYY-MM-DD') invoicedate, "
				+ " taxrate, taxamt, taxableamt, taxableamt+taxamt amttax,goodsdesc FROM venderinvoiceitem "
				+ " WHERE sheetid = ? ORDER BY seqno ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);

		Element elm_body = adapter.getRowSetElement("body", "row");
		int rows = adapter.rows();
		elm_body.setAttribute("rows", "" + rows);
		elm_body.setAttribute("name", "venderinvoiceitem");
		return elm_body;
	}

	/**
	 * 返回XML格式发票信息
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public Element toElement() throws SQLException, InvalidDataException, IOException {
		Element elm_sheet = new Element("sheet");
		elm_sheet.setAttribute("name", "venderinvoice");
		elm_sheet.setAttribute("title", title);
		elm_sheet.setAttribute("sheetid", sheetid);

		elm_sheet.addContent(this.getHead());
		elm_sheet.addContent(this.getBody());
		return elm_sheet;
	}

	/**
	 * 得到vendrid ，主要用于权限验证
	 * 
	 * @param sheetid
	 * @return
	 * @throws SQLException
	 */
	public String getVenderId() throws SQLException {
		String rel_venderid = "";
		String sql = " SELECT venderid FROM venderinvoice WHERE sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			rel_venderid = rs.getString(1);

		rs.close();
		pstmt.close();
		return rel_venderid;
	}

	final private static String	title	= "发票接收单";

	final private String		sheetid;

	final private Connection	conn;
}

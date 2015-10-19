package com.royalstone.vss.vender;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.workbook.Workbook;

/**
 * @note 供应商维护自身资料 此处供应商资料独立，不与业务MISC等系统中的供应商资料同步
 */
public class VenderInformationDIY {

	/**
	 * 获得供应商信息
	 * 
	 * @param conn
	 * @param token
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public Element getVenderInfoList(Connection conn, Map map) throws SQLException, UnsupportedEncodingException {
		String sql = " SELECT vi.venderid,v.vendername,"
				+ " vi.postaddress,vi.postcode,vi.contactperson,vi.telno,vi.mobileno, vi.note,vi.updatedate,decode(vi.taxtype,0,'一般纳税人','小规模或自然人') taxtype,vi.taxname,vi.taxno,vi.taxaddrtel,vi.taxbank "
				+ " FROM vender_diy vi join vender v on vi.venderid=v.venderid"
				+cookFilter(map).toWhereString();

		return SqlUtil.getRowSetElement(conn, sql, "rowset");
	}

	/**
	 * 供应商列表excel
	 * 
	 * @param file
	 * @param map
	 * @throws InvalidDataException
	 * @throws SQLException
	 * @throws IOException
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public void makeVenderListExcel(Connection conn, File file, Map map) throws InvalidDataException, SQLException,
			IOException, RowsExceededException, WriteException {
		String[] title = { "供应商编码", "供应商名称", "邮寄地址", "邮政编码", "联系人", "办公电话", "手机", "备注","更新时间" , "纳税人类型","开票名称","纳税识别号","税票地址电话","税票开户行及帐号"};
		String sql = " SELECT vi.venderid,v.vendername,"
				+ " vi.postaddress,vi.postcode,vi.contactperson,vi.telno,vi.mobileno, vi.note,vi.updatedate,decode(vi.taxtype,0,'一般纳税人','小规模或自然人') taxtype,vi.taxname,vi.taxno,vi.taxaddrtel,vi.taxbank "
				+ " FROM vender_diy vi join vender v on vi.venderid=v.venderid"
				+cookFilter(map).toWhereString();

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		
		Workbook.writeToFile(file, rs, title, "供应商信息列表");
		
		rs.close();
		pstmt.close();
	}
	
	public SqlFilter cookFilter(Map<String, String[]> map) {
		return new SqlFilter(map).addFilter("v_min", "vi.venderid", ">=", true).
				addFilter("v_max","vi.venderid", "<=", true);
	}

	/**
	 * 获得供应商信息
	 * 
	 * @param conn
	 * @param token
	 * @return
	 * @throws SQLException
	 */
	public Element getVenderInfo(Connection conn, Token token) throws SQLException {
		String sql = " SELECT vi.venderid,"
				+ " vi.postaddress,vi.postcode,vi.contactperson,vi.telno,vi.mobileno,"
				+ " vi.note,vi.updatedate,vi.taxtype,vi.taxname,vi.taxno,vi.taxaddrtel,vi.taxbank FROM vender_diy vi   WHERE vi.venderid='"
				+ token.getBusinessid() + "' ";
		return SqlUtil.getRowSetElement(conn, sql, "rowset");
	}

	/**
	 * 更新供应商信息
	 * 
	 * @param conn
	 * @param elmVender
	 * @param token
	 * @throws Exception
	 */
	public void setVenderInfo1(Connection conn, Element elmVender, Token token) throws Exception {
		String sql = " INSERT INTO vender_diy(venderid, postaddress, postcode, contactperson, telno, mobileno, note) "
				+ " values( ?, ?, ?, ?, ?, ?, ?) ";

		try {
			conn.setAutoCommit(false);
			delVenderInfo(conn, token.getBusinessid());

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, token.getBusinessid());
			pstmt.setString(2, elmVender.getChildTextTrim("postaddress"));
			pstmt.setString(3, elmVender.getChildTextTrim("postcode"));
			pstmt.setString(4, elmVender.getChildTextTrim("contactperson"));
			pstmt.setString(5, elmVender.getChildTextTrim("telno"));
			pstmt.setString(6, elmVender.getChildTextTrim("mobileno"));
			pstmt.setString(7, elmVender.getChildTextTrim("note"));

			pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	public void setVenderInfo(Connection conn, Token token, HttpServletRequest request) throws Exception {
		String sql = " INSERT INTO vender_diy(venderid, postaddress, postcode, contactperson, telno, mobileno, note,taxtype,taxname,taxno,taxaddrtel,taxbank,updatedate) "
				+ " values( ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,sysdate) ";

		String postaddress = request.getParameter("postaddress");
		String postcode = request.getParameter("postcode");
		String contactperson = request.getParameter("contactperson");
		String telno = request.getParameter("telno");
		String mobileno = request.getParameter("mobileno");
		String note = request.getParameter("note");

		String taxtype = request.getParameter("taxtype");
		String taxname = request.getParameter("taxname");
		String taxno = request.getParameter("taxno");
		String taxaddrtel = request.getParameter("taxaddrtel");
		String taxbank = request.getParameter("taxbank");

		try {
			conn.setAutoCommit(false);
			delVenderInfo(conn, token.getBusinessid());
			SqlUtil.executePS(conn, sql, token.getBusinessid(), postaddress, postcode, contactperson, telno, mobileno,
					note, taxtype, taxname, taxno, taxaddrtel, taxbank);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 删除供应商信息
	 * 
	 * @param conn
	 * @param venderid
	 * @throws SQLException
	 */
	public void delVenderInfo(Connection conn, String venderid) throws SQLException {
		String sql = " DELETE FROM vender_diy WHERE venderid='" + venderid + "' ";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	public void agreeCrbLic(Connection conn, String venderid){
		String sql="update vender_diy set crblicstatus='Y',crblicdate=sysdate where venderid=?";
		SqlUtil.executePS(conn, sql, venderid);
	}
	
	public String getCrbLicStatus(Connection conn, String venderid){
		String sql ="select crblicstatus from vender_diy where venderid=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, venderid);
		return list.size()>0?list.get(0):"N";
	}
}

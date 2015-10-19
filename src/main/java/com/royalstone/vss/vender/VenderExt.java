/**
 * 
 */
package com.royalstone.vss.vender;

import java.sql.Connection;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.sql.SqlUtil;

/**
 * @author Baijian 供应商扩展信息维护
 */
public class VenderExt {
	/*
	 * 获得供应商扩展信息
	 */
	public Element getVenderExt(Connection conn, String venderid) throws SQLException {
		String sql = " SELECT venderid, contact, contacttel, contactmotel FROM venderext "
				+ " WHERE venderid=? ";
		return SqlUtil.getRowSetElement(conn, sql, venderid, "vender");
	}

	public void insertNewRow(Connection conn, Element elmVender,String venderid) throws Exception {
		String sql = " INSERT INTO venderext(venderid, contact, contacttel, contactmotel ) "
				+ " values(  ?, ?, ?, ? ) ";
			SqlUtil.executePS(conn, sql, venderid,elmVender.getChildTextTrim("contact"),elmVender.getChildTextTrim("contacttel"),elmVender.getChildTextTrim("contactmotel"));
	}

	public void delRow(Connection conn, String venderid) throws SQLException {
		String sql = " DELETE venderext WHERE venderid=? ";
		SqlUtil.executePS(conn, sql, venderid);
	}
}

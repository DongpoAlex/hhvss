/**
 * 
 */
package com.royalstone.certificate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author BaiJian 证照品类
 */
public class CertificateCategoryDAO {
	final private Connection			conn;
	/**
	 * @param conn
	 */
	public CertificateCategoryDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	public Element getList() throws SQLException {
		String sql = "select ccid,ccname,note from certificateCategory";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}

	public Element getDetail( int id ) throws SQLException {
		String sql = "select ccid,ccname,note from certificateCategory where ccid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("detail", "row");
		rs.close();
		ps.close();
		return elm;
	}
	
	public boolean update(String ccname,String note,int ccid) throws SQLException{
		String sql = "update certificateCategory set ccname=?,note=? where ccid=?";
		boolean rel =false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, SqlUtil.toLocal(ccname));
		ps.setString(2, SqlUtil.toLocal(note));
		ps.setInt(3, ccid);
		if(ps.executeUpdate()==1){
			rel=true;
		}
		return rel;
	}
	
	public boolean insert(String ccname,String note) throws SQLException{
		String sql="insert into certificateCategory(ccid,ccname,note) values(certificatecategory_id.nextval, ?,?)";
		boolean rel =false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, SqlUtil.toLocal(ccname));
		ps.setString(2, SqlUtil.toLocal(note));
		if(ps.executeUpdate()==1){
			rel=true;
		}
		return rel;
	}
}

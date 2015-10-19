/**
 * 
 */
package com.royalstone.certificate.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author BaiJian
 *
 * ֤�յ�������������
 */
public class CertificateTypeDAO {

	final private Connection			conn;

	/**
	 * @param conn
	 */
	public CertificateTypeDAO(Connection conn) {
		super();
		this.conn = conn;
	}
	
	public Element getList() throws SQLException, IOException {
		String sql = "select ctid,ctname,note,flag,yearFlag,appflag,whFlag from certificateType";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}

	public Element getList(int flag) throws SQLException, IOException {
		String sql = "select ctid,ctname,note,flag,yearFlag,appflag,whFlag from certificateType where flag=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, flag);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}
	
	public Element getDetail( int id ) throws SQLException, IOException {
		String sql = "select ctid,ctname,note,flag,yearFlag,appflag,whFlag from certificateType where ctid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("detail", "row");
		rs.close();
		ps.close();
		return elm;
	}
	
	public boolean update(String ctname,String note,int flag,int yearFlag ,int appFlag ,int whFlag,int ctid) throws SQLException{
		String sql = "update certificateType set ctname=?,note=?,flag=?,yearFlag=?,appFlag=?,whFlag=? where ctid=?";
		boolean rel =false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, SqlUtil.toLocal(ctname));
		ps.setString(2, SqlUtil.toLocal(note));
		ps.setInt(3, flag);
		ps.setInt(4, yearFlag);
		ps.setInt(5, appFlag);
		ps.setInt(6, whFlag);
		ps.setInt(7, ctid);
		if(ps.executeUpdate()==1){
			rel=true;
		}
		return rel;
	}
	
	public boolean insert(String ctname,String note,int flag,int yearFlag,int appFlag,int whFlag) throws SQLException{
		String sql="insert into certificateType(ctid,ctname,note,flag,yearFlag,appFlag,whFlag) values(certificatetype_id.nextval, ?,?,?,?,?,?)";
		boolean rel =false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, SqlUtil.toLocal(ctname));
		ps.setString(2, SqlUtil.toLocal(note));
		ps.setInt(3, flag);
		ps.setInt(4, yearFlag);
		ps.setInt(5, appFlag);
		ps.setInt(6, whFlag);
		if(ps.executeUpdate()==1){
			rel=true;
		}
		return rel;
	}
}

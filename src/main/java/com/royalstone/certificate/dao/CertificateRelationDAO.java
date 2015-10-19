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
 * @author BaiJian
 * 证照品类，类型对应关系
 */
public class CertificateRelationDAO {
	final private Connection			conn;


	/**
	 * @param conn
	 */
	public CertificateRelationDAO(Connection conn) {
		super();
		this.conn = conn;
	}
	
	public Element getCTCByCCID(int ccid) throws SQLException {
		String sql = "select ct.ctid,ct.ctname,cr.flag from certificatetype ct " +
				" left join certificaterelation cr  on cr.ctid=ct.ctid and ccid=? where ct.flag=1";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ccid);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}
	public Element getListByCtid(int ctid) throws SQLException {
		String sql = "select ct.ctid,ct.ctname,ct.flag ctflag,ct.yearFlag,cc.ccname,cc.ccid,cr.flag,cr.note from certificateRelation cr " +
				" join certificateType ct on ct.ctid=cr.ctid " +
				" join certificateCategory cc on cc.ccid=cr.ccid " +
				" where cr.ctid=? ";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ctid);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}
	public Element getListByCcid(int ccid) throws SQLException {
		String sql = "select ct.ctid,ct.ctname,ct.flag ctflag,ct.yearFlag,cc.ccname,cc.ccid,cr.flag,cr.note from certificateRelation cr " +
				" join certificateType ct on ct.ctid=cr.ctid " +
				" join certificateCategory cc on cc.ccid=cr.ccid " +
				" where cr.ccid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ccid);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}
	
	public Element getDetail( int ctid,int ccid ) throws SQLException {
		String sql = "select ct.ctid,ct.ctname,ct.flag ctflag,ct.yearFlag,cc.ccname,cc.ccid,cr.flag,cr.note from certificateRelation cr " +
		" join certificateType ct on ct.ctid=cr.ctid " +
		" join certificateCategory cc on cc.ccid=cr.ccid " +
		" where cr.ccid=? and cr.ctid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ctid);
		ps.setInt(2, ccid);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("detail", "row");
		rs.close();
		ps.close();
		return elm;
	}
	
	public boolean update(int ctid, int ccid, int flag, String note) throws SQLException{
		String sql = "update certificateRelation set ctid=?,ccid=?,flag=?,note=? where ctid=? and ccid=?";
		boolean rel =false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ctid);
		ps.setInt(2, ccid);
		ps.setInt(3, flag);
		ps.setString(4, SqlUtil.toLocal(note));
		ps.setInt(5, ctid);
		ps.setInt(6, ccid);
		if(ps.executeUpdate()==1){
			rel=true;
		}
		return rel;
	}
	
	public boolean insert(int ctid,int ccid,int flag,String note) throws SQLException{
		String sql="insert into certificateRelation(ctid,ccid,flag,note) values( ?,?,?,?)";
		boolean rel =false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ctid);
		ps.setInt(2, ccid);
		ps.setInt(3, flag);
		ps.setString(4, SqlUtil.toLocal(note));
		if(ps.executeUpdate()==1){
			rel=true;
		}
		return rel;
	}
	
	public boolean del(int ctid,int ccid) throws SQLException{
		String sql="delete from certificateRelation where ctid=? and ccid=?";
		boolean rel =false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ctid);
		ps.setInt(2, ccid);
		if(ps.executeUpdate()==1){
			rel=true;
		}
		return rel;
	}
}

/**
 * 
 */
package com.royalstone.certificate.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author BaiJian
 * 
 *         ��Ӧ����֤�������ϵ
 */
public class VenderCategoryRelationDAO {

	final private Connection	conn;

	/**
	 * @param conn
	 */
	public VenderCategoryRelationDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	/**
	 * ȡƷ���б�,û�н�����ϵ��Ʒ�࣬venderid��=0
	 * 
	 * @param venderid
	 * @return
	 * @throws SQLException
	 */
	public Element getCCList(String venderid) throws SQLException{
		String sql = " select distinct cc.ccid,cc.ccname,nvl(vr.venderid,0) venderid from certificateCategory cc "
				+ " left join vendercategoryrelation vr on (vr.ccid=cc.ccid and vr.venderid=?) ";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}

	/**
	 * ��ù�Ӧ�̶�Ӧ��ϵ�б�
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public Element getList(Map map) throws SQLException {
		String sql = "select vc.venderid,v.vendername,ct.ctid,ct.ctname,vc.ccid,cr.ccname,  "
				+ " ( select MAX(a.sheetid) from certificate a join certificateitem b on (a.sheetid=b.sheetid and a.venderid=vc.venderid and b.ctid=vc.ctid and a.ccid=vc.ccid ) ) as sheetid, "
				+ " ( select count(*)       from certificate a join certificateitem b on (a.sheetid=b.sheetid and a.venderid=vc.venderid and b.ctid=vc.ctid and a.ccid=vc.ccid ) ) as sheetcount "
				+ " from venderCategoryRelation vc " + " join vender v on (v.venderid=vc.venderid) "
				+ " join certificateType ct on (ct.ctid=vc.ctid) "
				+ " left join certificatecategory cr on (cr.ccid=vc.ccid) ";

		Filter filter = cookFilter(map);
		if (filter.count() > 0) {
			sql += " where " + filter.toString();
		}
		return SqlUtil.getRowSetElement(conn, sql, "row");
	}

	/**
	 * @param venderid
	 * @param ctid
	 * @param ccid
	 * @return
	 * @throws SQLException
	 */
	public Element getDetail(String venderid, int ctid, int ccid) throws SQLException, IOException {
		String sql = "select vc.venderid,v.vendername,vc.ctid,ct.ctname from "
				+ " venderCategoryRelation vc  join vender v on (v.venderid=vc.venderid) "
				+ " join certificateType ct on (ct.ctid=vc.ctid) "
				+ " where vc.venderid=? AND vc.ctid=? AND vc.ccid=? ";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		ps.setInt(2, ctid);
		ps.setInt(3, ccid);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("detail", "row");
		rs.close();
		ps.close();
		return elm;
	}

	public Element getVendersByCCID(int ccid) throws SQLException {
		String sql = "select distinct vc.venderid,v.vendername from "
				+ " venderCategoryRelation vc join vender v on (v.venderid=vc.venderid) "
				+ " where vc.ccid=? ";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ccid);
		ResultSet rs = ps.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm = adapter.getRowSetElement("list", "row");
		rs.close();
		ps.close();
		return elm;
	}

	public boolean insert(String venderid, int ctid, int ccid) throws SQLException {
		String sql = "insert into venderCategoryRelation(venderid,ctid,ccid) values(?,?,?)";
		boolean rel = false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		ps.setInt(2, ctid);
		ps.setInt(3, ccid);
		if (ps.executeUpdate() == 1) {
			rel = true;
		}
		return rel;
	}

	public boolean insert(String venderid, int ccid) throws SQLException {
		String sql = "insert into venderCategoryRelation(venderid,ctid,ccid) values(?,-1,?)";
		boolean rel = false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		ps.setInt(2, ccid);
		if (ps.executeUpdate() == 1) {
			rel = true;
		}
		return rel;
	}

	public boolean delete(String venderid, int ctid, int ccid) throws SQLException {
		String sql = "delete from venderCategoryRelation where venderid=? and ctid=? and ccid=? ";
		boolean rel = false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		ps.setInt(2, ctid);
		ps.setInt(3, ccid);
		if (ps.executeUpdate() == 1) {
			rel = true;
		}
		return rel;
	}

	public boolean delete(String venderid, int ccid) throws SQLException {
		String sql = "delete from venderCategoryRelation where venderid=? and ccid=?";
		boolean rel = false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		ps.setInt(2, ccid);
		if (ps.executeUpdate() == 1) {
			rel = true;
		}
		return rel;
	}

	public boolean delete(String venderid) throws SQLException {
		String sql = "delete from venderCategoryRelation where venderid=? and ccid>0";
		boolean rel = false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, venderid);
		if (ps.executeUpdate() == 1) {
			rel = true;
		}
		return rel;
	}

	private Filter cookFilter(Map map) {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("vc.venderid=" + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("ctid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_tmp = new Values(ss);
			filter.add("vc.ctid IN (" + val_tmp.toString4String() + ") ");
		}

		ss = (String[]) map.get("ccid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_tmp = new Values(ss);
			filter.add("vc.ccid IN (" + val_tmp.toString4String() + ") ");
		}

		ss = (String[]) map.get("ctflag");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_tmp = new Values(ss);
			filter.add("ct.flag IN (" + val_tmp.toString4String() + ") ");
		}

		return filter;
	}

}

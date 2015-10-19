/*
 * Created on 2005-12-9
 *
 */
package com.royalstone.vss.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.royalstone.security.Permission;
import com.royalstone.util.daemon.DbAdm;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author meng
 * 
 */
public class AuthorityAdm {

	public AuthorityAdm(Connection conn) {
		this.conn = conn;
	}

	public Authority[] getPermission4Role(int roleid) throws SQLException {
		Authority[] lst;
		try {
			conn.setAutoCommit(false);
			clearWorkTable();
			makeWorkTable();
			cook4Role(roleid);
			lst = fetch();
			clearWorkTable();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
		return lst;
	}

	public Authority[] getPermission4Module(int moduleid) throws SQLException {
		Authority[] lst;
		try {
			conn.setAutoCommit(false);
			clearWorkTable();
			makeWorkTable();
			cook4Module(moduleid);
			lst = fetch();
			clearWorkTable();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
		return lst;
	}

	public void save4Role(int roleid, Authority[] auth) throws SQLException {
		try {
			conn.setAutoCommit(false);
			delete4Role(roleid);
			insert(auth);
			conn.commit();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
	}

	public void save4Module(int moduleid, Authority[] auth) throws SQLException {
		try {
			conn.setAutoCommit(false);
			delete4Module(moduleid);
			insert(auth);
			conn.commit();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
	}

	public void cook4Module(int moduleid) throws SQLException {
		String s01 = "" + " INSERT INTO " + tmp00 + " SELECT r.roleid, m.moduleid, m.rightid, 0 "
				+ " FROM role_list r JOIN module_list m  " + " ON (m.moduleid=" + moduleid + ") ";

		String s02 = "" + " INSERT INTO " + tmp00 + " SELECT rm.roleid, rm.moduleid, m.rightid, rm.rightid "
				+ " FROM role_module rm  " + " JOIN module_list m ON(m.moduleid=rm.moduleid) "
				+ " JOIN role_list r ON (r.roleid=rm.roleid) " + " WHERE rm.moduleid= " + moduleid;

		String s03 = "" + " INSERT INTO " + tmp01
				+ " SELECT roleid, moduleid, MAX(right_module), MAX(right_role) " + " FROM " + tmp00 + " "
				+ " GROUP BY roleid,moduleid ";

		Statement stmt = conn.createStatement();
		stmt.execute(s01);
		stmt.execute(s02);
		stmt.execute(s03);
		stmt.close();
	}

	public void cook4Role(int roleid) throws SQLException {
		String s01 = ""
				+ " INSERT INTO "
				+ tmp00
				+ " SELECT r.roleid, m.moduleid, m.rightid, 0 "
				+ " FROM role_list r "
				+ " JOIN module_list m  "
				+ " ON (r.roleid= "
				+ roleid
				+ " ) "
				+ "  join roletypeconfig c on c.roletype=m.roletype "
				+ "  where m.roletype=r.roletype  or m.roletype=0  "
				+ "  or m.roletype in (select cc.roletype from roletypeconfig cc where cc.headroletype=r.roletype) ";

		String s02 = "" + " INSERT INTO " + tmp00 + " SELECT rm.roleid, rm.moduleid, m.rightid, rm.rightid "
				+ " FROM role_module rm  " + " JOIN module_list m ON(m.moduleid=rm.moduleid) "
				+ " JOIN role_list r ON (r.roleid=rm.roleid) " + " WHERE rm.roleid = " + roleid;

		String s03 = "" + " INSERT INTO " + tmp01
				+ " SELECT roleid, moduleid, MAX(right_module), MAX(right_role) " + " FROM " + tmp00
				+ " GROUP BY roleid,moduleid ";
		Statement stmt = conn.createStatement();
		stmt.execute(s01);
		stmt.execute(s02);
		stmt.execute(s03);
		stmt.close();
	}

	private Authority[] fetch() throws SQLException {
		String sql = ""
				+ " SELECT t.roleid, t.moduleid, t.right_module, t.right_role, m.modulename, r.rolename "
				+ " FROM " + tmp01 + " t  " + " JOIN module_list m ON (m.moduleid=t.moduleid) "
				+ " JOIN role_list r ON (r.roleid=t.roleid) ";
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		Vector<Authority> vec = new Vector<Authority>(0);
		while (rs.next()) {
			int roleid = rs.getInt("roleid");
			int moduleid = rs.getInt("moduleid");
			int right_role = rs.getInt("right_role");
			int right_module = rs.getInt("right_module");
			String modulename = rs.getString("modulename");
			String rolename = rs.getString("rolename");
			modulename = (modulename == null) ? "" : SqlUtil.fromLocal(modulename).trim();
			rolename = (rolename == null) ? "" : SqlUtil.fromLocal(rolename).trim();
			Authority au = new Authority(roleid, moduleid);
			au.perm_module = new Permission(right_module);
			au.perm_role = new Permission(right_role);
			au.modulename = modulename;
			au.rolename = rolename;
			vec.add(au);
		}
		rs.close();
		pstmt.close();

		Authority[] lst = new Authority[vec.size()];
		for (int i = 0; i < lst.length; i++)
			lst[i] = (Authority) vec.get(i);
		return lst;

	}

	private void delete4Role(int roleid) throws SQLException {
		String sql = " DELETE FROM role_module WHERE roleid = " + roleid;
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}

	private void delete4Module(int moduleid) throws SQLException {
		String sql = " DELETE FROM role_module WHERE moduleid = " + moduleid;
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}

	private void insert(Authority[] lst) throws SQLException {
		String sql = " INSERT INTO role_module ( roleid, moduleid, rightid ) VALUES ( ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		for (int i = 0; i < lst.length; i++) {
			if (lst[i].perm_role != null) {
				Permission perm = lst[i].perm_role;
				if (perm.getRight() != 0) {
					pstmt.setInt(1, lst[i].roleid);
					pstmt.setInt(2, lst[i].moduleid);
					pstmt.setInt(3, perm.getRight());
					pstmt.executeUpdate();
				}
			}
		}
		pstmt.close();
	}

	private void makeWorkTable() throws SQLException {
		String s00 = "" + " create global temporary table " + tmp00 + " ( " + " roleid			INT NOT NULL, "
				+ " moduleid    	INT NOT NULL, " + " right_module 	INT DEFAULT 0 NOT NULL, "
				+ " right_role  	INT DEFAULT 0 NOT NULL " + " ) on commit delete rows ";

		String s01 = "" + " create global temporary table " + tmp01 + " ( " + " roleid			INT NOT NULL, "
				+ " moduleid    	INT NOT NULL, " + " right_module 	INT DEFAULT 0 NOT NULL, "
				+ " right_role  	INT DEFAULT 0 NOT NULL " + " ) on commit delete rows ";
		Statement stmt = conn.createStatement();
		DbAdm.createTempTable(conn, s00);
		DbAdm.createTempTable(conn, s01);
		stmt.close();
	}

	private void clearWorkTable() {
		DbAdm.dropTable(conn, tmp00);
		DbAdm.dropTable(conn, tmp01);
	}

	final private Connection	conn;
	private String				tmp00	= "temp_AuthorityAdm1";
	private String				tmp01	= "temp_AuthorityAdm2";

	
}

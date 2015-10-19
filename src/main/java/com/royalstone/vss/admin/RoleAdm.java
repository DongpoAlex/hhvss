/*
 * Created on 2005-12-13
 */
package com.royalstone.vss.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;

import com.royalstone.util.daemon.XErr;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于维护角色信息及角色-用户之间的关系.
 * 
 * @author meng
 * 
 */
public class RoleAdm {

	/**
	 * @param conn
	 *            权限控制用数据库连接.
	 */
	public RoleAdm(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 此方法向系统中添加角色.
	 * 
	 * @param roleid
	 * @param rolename
	 * @param shopid
	 * @param note
	 * @return
	 * @throws SQLException
	 */
	public Element addRole(int roleid, String rolename, String shopid, String note) throws SQLException {
		int id = (roleid > 0) ? roleid : (getMaxRoleid() + 1);

		String sql = " INSERT INTO role_list ( roleid, rolename, shopid, note ) " + " VALUES ( ?, ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.setString(2, SqlUtil.toLocal(rolename));
		pstmt.setString(3, SqlUtil.toLocal(shopid));
		pstmt.setString(4, SqlUtil.toLocal(note));
		pstmt.executeUpdate();
		pstmt.close();

		try {
			Element elm_role = getRoleElement(id);
			return elm_role;
		} catch (SQLException e) {
			throw new SQLException("操作数据库过程中出现异常,请查询.", "", e.getErrorCode());
		}
	}

	/**
	 * 调用此方法, 可在数据库中添加一个角色记录.
	 * 
	 * @param rolename
	 * @param shopid
	 * @param note
	 * @return
	 * @throws SQLException
	 */
	public int addRole(String rolename, String shopid, String note, int roletype) throws SQLException {
		int id = getMaxRoleid() + 1;
		id = (id > 0) ? id : 1;

		String sql = " INSERT INTO role_list ( roleid, rolename, shopid, note,roletype ) "
				+ " VALUES ( ?, ?, ?, ? ,?) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.setString(2, SqlUtil.toLocal(rolename));
		pstmt.setString(3, SqlUtil.toLocal(shopid));
		pstmt.setString(4, SqlUtil.toLocal(note));
		pstmt.setInt(5, roletype);
		pstmt.executeUpdate();
		pstmt.close();
		return id;
	}

	/**
	 * 此方法向系统中添加角色.
	 * 
	 * @param roleid
	 * @param rolename
	 * @param shopid
	 * @param note
	 * @return
	 * @throws SQLException
	 */
	public int newRole(int roleid, String rolename, String shopid, String note) throws SQLException {
		int id = (roleid > 0) ? roleid : (getMaxRoleid() + 1);
		id = (id > 0) ? id : 1;

		String sql = " INSERT INTO role_list ( roleid, rolename, shopid, note ) " + " VALUES ( ?, ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.setString(2, SqlUtil.toLocal(rolename));
		pstmt.setString(3, SqlUtil.toLocal(shopid));
		pstmt.setString(4, SqlUtil.toLocal(note));
		pstmt.executeUpdate();
		pstmt.close();
		return id;

	}

	/**
	 * 此方法用于修改角色信息.
	 * 
	 * @param roleid
	 * @param rolename
	 * @param shopid
	 * @param note
	 * @return
	 * @throws SQLException
	 */
	public void updateRole(int roleid, String rolename, String shopid, String note, int roletype)
			throws SQLException {
		String sql = " UPDATE role_list SET rolename = ?, shopid = ?, note = ?,roletype=? " + " WHERE roleid = "
				+ roleid;

		SqlUtil.executePS(conn, sql, new Object[] { rolename, shopid, note, roletype });
	}

	/**
	 * 此方法用于查询指定角色的信息.
	 * 
	 * @param roleid
	 * @return
	 * @throws SQLException
	 */
	public Role getRole(int roleid) throws SQLException {
		String sql = " SELECT rolename, shopid, note, status,roletype FROM role_list WHERE roleid = ? ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, roleid);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("没有这个角色:" + roleid, "", 100);
		String rolename = rs.getString(1);
		String shopid = rs.getString(2);
		String note = rs.getString(3);
		int roletype = rs.getInt(5);
		rolename = (rolename == null) ? "" : SqlUtil.fromLocal(rolename).trim();
		shopid = (shopid == null) ? "" : SqlUtil.fromLocal(shopid).trim();
		note = (note == null) ? "" : SqlUtil.fromLocal(note).trim();

		Role role = new Role(roleid, rolename, shopid, note,roletype);
		rs.close();
		pstmt.close();
		return role;
	}

	/**
	 * 此方法用于查询指定角色的信息.
	 * 
	 * @param roleid
	 * @return
	 * @throws SQLException
	 */
	public Element getRoleElement(int roleid) throws SQLException {
		String sql = " SELECT rolename, shopid, note, status,roletype FROM role_list WHERE roleid = ? ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, roleid);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("没有这个角色:" + roleid, "", 100);
		String rolename = rs.getString(1);
		String shopid = rs.getString(2);
		String note = rs.getString(3);
		int status = rs.getInt(4);
		int roletype = rs.getInt(5);

		rolename = (rolename == null) ? "" : SqlUtil.fromLocal(rolename).trim();
		shopid = (shopid == null) ? "" : SqlUtil.fromLocal(shopid).trim();
		note = (note == null) ? "" : SqlUtil.fromLocal(note).trim();

		Element elm = new Element("role");

		elm.addContent(new Element("roleid").addContent("" + roleid));
		elm.addContent(new Element("rolename").addContent(rolename));
		elm.addContent(new Element("shopid").addContent(shopid));
		elm.addContent(new Element("note").addContent(note));
		elm.addContent(new Element("status").addContent("" + status));
		elm.addContent(new Element("roletype").addContent("" + roletype));
		rs.close();
		pstmt.close();
		return elm;
	}

	/**
	 * 此方法用于从系统中删除一个角色. 在删除前进行一系列清理工作: 删除角色环境变量, 删除角色权限记录, 删除角色-用户关系记录.
	 * 
	 * @param roleid
	 * @throws SQLException
	 */
	public void deleteRole(int roleid) throws SQLException {
		String sql00 = " DELETE FROM role_environment WHERE roleid = " + roleid;
		String sql01 = " DELETE FROM role_module WHERE roleid = " + roleid;
		String sql02 = " DELETE FROM user_role WHERE roleid = " + roleid;
		String sql03 = " DELETE FROM role_list WHERE roleid = " + roleid;

		Statement stmt = conn.createStatement();
		try {
			conn.setAutoCommit(false);
			stmt.execute(sql00);
			stmt.execute(sql01);
			stmt.execute(sql02);
			stmt.execute(sql03);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 此方法清除指定角色内的所有成员.
	 * 
	 * @param roleid
	 * @throws SQLException
	 */
	public void clearMember(int roleid) throws SQLException {
		String sql = " DELETE FROM user_role WHERE roleid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, roleid);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 此方法建立角色与用户之间的关系.
	 * 
	 * @param userid
	 *            用户号
	 * @param roleid
	 *            角色号
	 * @throws SQLException
	 */
	public void addRoleMember(int userid, int roleid) throws SQLException {
		try {
			conn.setAutoCommit(false);
			deleteRoleMember(userid, roleid);
			insertRoleMember(userid, roleid);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 此方法在表user_role 中添加一条记录, 用于建立角色与用户之间的关系.
	 * 
	 * @param userid
	 *            用户号
	 * @param roleid
	 *            角色号
	 * @throws SQLException
	 */
	public void insertRoleMember(int userid, int roleid) throws SQLException {
		String sql = " INSERT INTO user_role ( userid, roleid ) VALUES ( ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, userid);
		pstmt.setInt(2, roleid);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 此方法把指定用户从指定角色内删除.
	 * 
	 * @param userid
	 * @param roleid
	 * @throws SQLException
	 */
	public void deleteRoleMember(int userid, int roleid) throws SQLException {
		String sql = " DELETE FROM user_role WHERE roleid = ? AND userid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, roleid);
		pstmt.setInt(2, userid);
		pstmt.executeUpdate();
		pstmt.close();

	}

	/**
	 * 查询指定角色的所有成员. 返回一个jdom 元素. 只有角色成员才进入该元素中.
	 * 
	 * @param roleid
	 * @return
	 * @throws SQLException
	 */
	public Element listRoleMember(int roleid) throws SQLException {
		Element elm_list = new Element("member_list");
		String sql = " SELECT u.userid, u.loginid, u.username, u.shopid "
				+ " FROM user_role ur JOIN user_list u ON(u.userid=ur.userid) " + " WHERE roleid=? ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, roleid);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Element elm = new Element("role");
			elm_list.addContent(elm);

			String loginid = rs.getString("loginid");
			String username = rs.getString("username");
			String shopid = rs.getString("shopid");

			loginid = (loginid == null) ? "" : SqlUtil.fromLocal(loginid).trim();
			username = (username == null) ? "" : SqlUtil.fromLocal(username).trim();
			shopid = (shopid == null) ? "" : SqlUtil.fromLocal(shopid).trim();

			elm.setAttribute("username", username);
			elm.setAttribute("shopid", shopid);
			elm.setAttribute("loginid", loginid);
		}
		rs.close();
		pstmt.close();
		return elm_list;
	}

	/**
	 * 此方法用于查询指定与系统内全部角色的关系. 此方法返回一个XML元素, 包括全部角色. 根据属性is_member的值可以判断用户是否角色成员.
	 * 
	 * @param userid
	 *            用户号
	 * @return XML element, including list of all roles. attribute is_member
	 *         indicating wether the user is a member of the role.
	 * @throws SQLException
	 */
	public Element listRole4User(int userid) throws SQLException {
		Element elm_list = new Element("role_list");
		String sql = " SELECT r.roleid, r.rolename, r.shopid, r.note, ur.userid,rc.roletypename,r.roletype FROM role_list r "
				+ " LEFT OUTER JOIN user_role ur ON (r.roleid=ur.roleid AND ur.userid = ? ) "
				+ " left join roletypeconfig rc on rc.roletype=r.roletype ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, userid);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Element elm = new Element("role");
			elm_list.addContent(elm);

			String roleid = rs.getString("roleid");
			String rolename = rs.getString("rolename");
			String shopid = rs.getString("shopid");
			String note = rs.getString("note");
			String is_member = rs.getString("userid");
			String roletypename = rs.getString("roletypename");
			String roletype = rs.getString("roletype");
			if (is_member != null && is_member.length() > 0)
				elm.setAttribute("is_member", "1");

			roleid = (roleid == null) ? "" : SqlUtil.fromLocal(roleid).trim();
			rolename = (rolename == null) ? "" : SqlUtil.fromLocal(rolename).trim();
			shopid = (shopid == null) ? "" : SqlUtil.fromLocal(shopid).trim();
			note = (note == null) ? "" : SqlUtil.fromLocal(note).trim();
			roletypename = (roletypename == null) ? "" : SqlUtil.fromLocal(roletypename).trim();

			elm.setAttribute("userid", "" + userid);
			elm.setAttribute("roleid", roleid);
			elm.setAttribute("rolename", rolename);
			elm.setAttribute("shopid", shopid);
			elm.setAttribute("note", note);
			elm.setAttribute("roletypename", roletypename);
			elm.setAttribute("roletype", roletype);
		}
		rs.close();
		pstmt.close();
		return elm_list;
	}

	/**
	 * 取出全部角色清单.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public Element listRoleAll() throws SQLException, IOException {
		String sql = " SELECT r.roleid, r.rolename, r.note, r.status," + " r.roletype,c.roletypename,c.headroletype "
				+ " FROM role_list r " + " left join roleTypeConfig c on c.roletype=r.roletype ";

		Element elm_list = new Element("role_list");

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);

		elm_list = adapter.getRowSetElement("role_list", "role");
		elm_list.setAttribute("rows", "" + adapter.rows());
		rs.close();
		pstmt.close();
		return elm_list;
	}

	/**
	 * 取角色清单内的最大角色编号.
	 * 
	 * @return 当前所用最大角色编号
	 * @throws SQLException
	 */
	private int getMaxRoleid() throws SQLException {
		String sql = " SELECT MAX(roleid) FROM role_list ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		stmt.close();
		return id;
	}

	public XErr xerr() {
		return xerr;
	}

	
	final private Connection	conn;
	private XErr				xerr	= new XErr(0, "OK");
}

/*
 * Created on 2006-11-01
 */
package com.royalstone.vss.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import com.jivesoftware.util.StringUtils;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.LogonAdm;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于维护VSS系统中的供应商用户. 目前提供以下功能: 添加, 修改(名称,机构,密码), 锁定. 为了避免误操作导致数据不一致,
 * 暂不提供删除功能.
 * 
 * @author meng
 */
public class VenderUserAdm {

	public VenderUserAdm(Connection conn) {
		this.conn = conn;
	}

	public void addVenderUser(int userid, String username, String loginid, String venderid, String password,
			int menuroot) throws SQLException {
		try {
			conn.setAutoCommit(false);
			int uid = addUser(userid, username, loginid, "VENDER", password, menuroot);
			setUserRole(uid, "VENDER");
			setUserEnv(uid, "venderid", venderid);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 删除用户变量
	 * 
	 * @param userid
	 * @param envName
	 * @param envValue
	 * @throws SQLException
	 */
	private void delUserEnv(int userid, String envName, String envValue) throws SQLException {
		String sql_del = " delete from user_environment where userid=? and dataflag=? and datavalue=? ";
		PreparedStatement pstmt = conn.prepareStatement(sql_del);
		pstmt.setInt(1, userid);
		pstmt.setString(2, envName);
		pstmt.setString(3, envValue);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 添加用户变量
	 * 
	 * @param userid
	 * @param envName
	 * @param envValue
	 * @throws SQLException
	 */
	private void addUserEnv(int userid, String envName, String envValue) throws SQLException {
		String sql_ins = " INSERT INTO user_environment ( userid, dataflag, datavalue ) VALUES ( ?,?,? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setInt(1, userid);
		pstmt.setString(2, envName);
		pstmt.setString(3, envValue);
		pstmt.executeUpdate();
		pstmt.close();
	}

	private void setUserEnv(int userid, String env_name, String env_value) throws SQLException {
		String sql_ins = " INSERT INTO user_environment ( userid, dataflag, datavalue ) VALUES ( ?,?,? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setInt(1, userid);
		pstmt.setString(2, env_name);
		pstmt.setString(3, env_value);
		pstmt.executeUpdate();
		pstmt.close();
	}

	private void setUserRole(int userid, String rolename) throws SQLException {
		int roleid = getRoleid("VENDER");
		String sql_upd = " INSERT INTO user_role ( userid, roleid ) VALUES ( ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_upd);
		pstmt.setInt(1, userid);
		pstmt.setInt(2, roleid);
		pstmt.executeUpdate();
		pstmt.close();
	}

	private int getRoleid(String rolename) throws SQLException {
		String sql = " SELECT roleid FROM role_list WHERE rolename = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, SqlUtil.toLocal(rolename));
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("role NOT_FOUND:" + rolename, "NOT_FOUND", 100);
		int roleid = rs.getInt(1);
		pstmt.close();
		return roleid;
	}

	public int addUser(int userid, String username, String loginid, String shopid, String password, int menuroot)
			throws SQLException {
		int id = (userid >= 0) ? userid : (getMaxUserid() + 1);
		String encryptPass = StringUtils.hash(password);

		String sql = " INSERT INTO user_list( userid, loginid, shopid, username, password, menuroot ) "
				+ " VALUES( ?, ?, ?, ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.setString(2, SqlUtil.toLocal(loginid));
		pstmt.setString(3, SqlUtil.toLocal(shopid));
		pstmt.setString(4, SqlUtil.toLocal(username));
		pstmt.setString(5, encryptPass);
		pstmt.setInt(6, menuroot);
		pstmt.executeUpdate();
		pstmt.close();
		return id;
	}

	public int getUserid(String logindid) throws SQLException {
		String sql = " SELECT userid FROM user_list WHERE loginid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, logindid);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("没有这个用户:" + logindid, "", 100);
		int id = rs.getInt(1);
		rs.close();
		pstmt.close();
		return id;
	}

	public Element getUserInfo(int userid) throws SQLException {
		String sql = " SELECT u.userid, u.loginid, u.username, u.shopid, u.shopid shopname, "
				+ " u.userstatus status, u.menuroot " + " FROM user_list u WHERE u.userid = ? ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, userid);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("没有这个用户:" + userid, "", 100);
		Element elm = new Element("user");

		String loginid = rs.getString("loginid");
		String username = rs.getString("username");
		String shopid = rs.getString("shopid");
		String shopname = rs.getString("shopname");
		String status = rs.getString("status");
		String menuroot = rs.getString("menuroot");

		loginid = (loginid == null) ? "" : SqlUtil.fromLocal(loginid).trim();
		username = (username == null) ? "" : SqlUtil.fromLocal(username).trim();
		shopid = (shopid == null) ? "" : SqlUtil.fromLocal(shopid).trim();
		shopname = (shopname == null) ? "" : SqlUtil.fromLocal(shopname).trim();

		elm.addContent((new Element("userid")).addContent("" + userid));
		elm.addContent((new Element("loginid")).addContent(loginid));
		elm.addContent((new Element("username")).addContent(username));
		elm.addContent((new Element("shopid")).addContent(shopid));
		elm.addContent((new Element("shopname")).addContent(shopname));
		elm.addContent((new Element("status")).addContent(status));
		elm.addContent((new Element("menuroot")).addContent(menuroot));

		pstmt.close();
		return elm;
	}

	public Element getUserByLoginid(String loginid) throws SQLException {
		String sql = " SELECT u.userid, u.loginid, u.username, u.shopid, u.userstatus status, u.menuroot, "
				+ " u.shopid shopname " + " FROM user_list u " + " WHERE u.loginid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, loginid);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("没有这个用户:" + loginid, "", 100);
		Element elm = new Element("user");

		int userid = rs.getInt("userid");
		String username = rs.getString("username");
		String shopid = rs.getString("shopid");
		String shopname = rs.getString("shopname");
		String status = rs.getString("status");
		String menuroot = rs.getString("menuroot");

		loginid = (loginid == null) ? "" : SqlUtil.fromLocal(loginid).trim();
		username = (username == null) ? "" : SqlUtil.fromLocal(username).trim();
		shopid = (shopid == null) ? "" : SqlUtil.fromLocal(shopid).trim();
		shopname = (shopname == null) ? "" : SqlUtil.fromLocal(shopname).trim();

		elm.addContent((new Element("userid")).addContent("" + userid));
		elm.addContent((new Element("loginid")).addContent(loginid));
		elm.addContent((new Element("username")).addContent(username));
		elm.addContent((new Element("shopid")).addContent(shopid));
		elm.addContent((new Element("shopname")).addContent(shopname));
		elm.addContent((new Element("status")).addContent(status));
		elm.addContent((new Element("menuroot")).addContent(menuroot));

		pstmt.close();
		return elm;
	}

	public void updateUser(int userid, String username, String loginid, String shopid) throws SQLException {
		String sql = " UPDATE user_list SET username = ?, loginid=?, shopid=? WHERE userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, SqlUtil.toLocal(username));
		pstmt.setString(2, SqlUtil.toLocal(loginid));
		pstmt.setString(3, SqlUtil.toLocal(shopid));
		pstmt.executeUpdate();
		pstmt.close();
	}

	public void updateUser(int userid, String username, String loginid, String shopid, int menuroot, int status)
			throws SQLException {
		String sql = " UPDATE user_list SET username = ?, loginid=?, shopid=?, menuroot=?, userstatus=? WHERE userid = "
				+ userid;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, SqlUtil.toLocal(username));
		pstmt.setString(2, SqlUtil.toLocal(loginid));
		pstmt.setString(3, SqlUtil.toLocal(shopid));
		pstmt.setInt(4, menuroot);
		pstmt.setInt(5, status);
		pstmt.executeUpdate();
		pstmt.close();
	}

	public void setUserStatus(int userid, int status) throws SQLException {
		String sql = " UPDATE user_list SET userstatus = ? WHERE userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, status);
		pstmt.executeUpdate();
		pstmt.close();

	}

	public boolean checkPassword(int userid, String pass) throws SQLException {
		if (pass == null)
			pass = "";
		String encryptPass = StringUtils.hash(pass);

		boolean ok = false;
		String sql = " SELECT userid, userclass, userstatus, username, password, shopid "
				+ " FROM user_list WHERE userid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, userid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			String password = rs.getString("password");

			password = (password == null) ? "" : password.trim();
			ok = password.equals(encryptPass);

		}

		rs.close();
		pstmt.close();
		return ok;
	}

	public void setPassword(int userid, String passwd) throws SQLException {
		if (passwd == null)
			passwd = "";
		String encryptPass = StringUtils.hash(passwd);

		String sql = " UPDATE user_list SET password = ? WHERE userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, encryptPass);
		pstmt.executeUpdate();
		pstmt.close();
	}

	public void setPassword(String loginid, String new_pass) throws SQLException {
		int userid = getUserid(loginid);
		setPassword(userid, new_pass);
	}

	/**
	 * 此方法用于查找满足条件的供应商用户. NOTE: 供应商用户必须属于系统内嵌角色VENDER.
	 * 
	 * @param map_parm
	 * @return
	 * @throws SQLException
	 */
	public Element getUserList(Map map_parm) throws SQLException {
		Element elm_list = new Element("user_list");
		String sql = " SELECT u.userid, u.loginid, u.username, u.shopid, u.userstatus status, "
				+ " u.menuroot, m.menulabel, u.shopid shopname, " + " f.venderid,f.vendername " + " FROM user_list u "
				+ " JOIN user_role ur ON ( u.userid=ur.userid ) "
				+ " JOIN user_environment e ON ( e.userid = u.userid AND e.dataflag='venderid' ) "
				+ " LEFT JOIN menu_list m ON (m.menuid=u.menuroot) "
				+ " LEFT JOIN vender f ON (f.venderid=e.datavalue) " + " WHERE u.shopid='VENDER'  ";

		String parm_loginid_min = Filter.getParameter(map_parm, "loginid_min");
		String parm_loginid_max = Filter.getParameter(map_parm, "loginid_max");
		String parm_loginid = Filter.getParameter(map_parm, "loginid");
		String parm_userid = Filter.getParameter(map_parm, "userid");
		String parm_username = Filter.getParameter(map_parm, "username");

		Filter filter = new Filter();
		if (parm_loginid_min != null)
			filter.add(" u.loginid >=" + ValueAdapter.toString4String(parm_loginid_min));
		if (parm_loginid_max != null)
			filter.add(" u.loginid <=" + ValueAdapter.toString4String(parm_loginid_max));
		if (parm_loginid != null)
			filter.add(" u.loginid like '%" + ValueAdapter.toSafeString(parm_loginid) + "%'");
		if (parm_userid != null)
			filter.add(" u.userid=" + parm_userid);
		if (parm_username != null)
			filter.add(" u.username=" + ValueAdapter.toString4String(parm_username));

		if (filter.count() > 0)
			sql += " AND " + filter.toString();

		ResultSet rs = SqlUtil.querySQL(conn, sql);
		int rows = 0;
		while (rs.next()) {
			rows++;
			Element elm = new Element("user");
			elm_list.addContent(elm);

			String userid = rs.getString("userid");
			String loginid = rs.getString("loginid");
			String username = rs.getString("username");
			String shopid = rs.getString("shopid");
			String shopname = rs.getString("shopname");
			String status = rs.getString("status");
			String menuroot = rs.getString("menuroot");
			String menulabel = rs.getString("menulabel");
			String venderid = rs.getString("venderid");
			String vendername = rs.getString("vendername");
			userid = (userid == null) ? "" : SqlUtil.fromLocal(userid).trim();
			loginid = (loginid == null) ? "" : SqlUtil.fromLocal(loginid).trim();
			username = (username == null) ? "" : SqlUtil.fromLocal(username).trim();
			shopid = (shopid == null) ? "" : SqlUtil.fromLocal(shopid).trim();
			shopname = (shopname == null) ? "" : SqlUtil.fromLocal(shopname).trim();
			menulabel = (menulabel == null) ? "" : SqlUtil.fromLocal(menulabel).trim();
			vendername = (vendername == null) ? "" : SqlUtil.fromLocal(vendername).trim();

			elm.addContent((new Element("userid")).addContent(userid));
			elm.addContent((new Element("loginid")).addContent(loginid));
			elm.addContent((new Element("username")).addContent(username));
			elm.addContent((new Element("shopid")).addContent(shopid));
			elm.addContent((new Element("shopname")).addContent(shopname));
			elm.addContent((new Element("status")).addContent(status));
			elm.addContent((new Element("menuroot")).addContent(menuroot));
			elm.addContent((new Element("menulabel")).addContent(menulabel));
			elm.addContent((new Element("venderid")).addContent(venderid));
			elm.addContent((new Element("vendername")).addContent(vendername));
		}
		SqlUtil.close(rs);
		elm_list.setAttribute("rows", "" + rows);
		return elm_list;
	}

	private int getMaxUserid() throws SQLException {
		String sql = " SELECT MAX(userid) FROM user_list ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		stmt.close();
		return id;
	}

	/**
	 * 增加扩展的业务ID
	 * 
	 * @param userid
	 * @param businessid
	 * @throws SQLException
	 */
	public void addExtBusinessid(int userid, String[] businessid) throws SQLException {
		try {
			conn.setAutoCommit(false);
			for (int i = 0; i < businessid.length; i++) {
				delUserEnv(userid, LogonAdm.extbusinessid, businessid[i]);
				addUserEnv(userid, LogonAdm.extbusinessid, businessid[i]);
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 删除扩展业务ID 不提供删除默认业务ID
	 * 
	 * @param userid
	 * @param businessid
	 * @throws SQLException
	 */
	public void delExtBusinessid(int userid, String[] businessid) throws SQLException {
		try {
			conn.setAutoCommit(false);
			for (int i = 0; i < businessid.length; i++) {
				delUserEnv(userid, LogonAdm.extbusinessid, businessid[i]);
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 增加默认的业务ID
	 * 
	 * @param userid
	 * @param businessid
	 * @throws SQLException
	 */
	public void addDefBusinessid(int userid, String[] businessid) throws SQLException {
		try {
			conn.setAutoCommit(false);
			for (int i = 0; i < businessid.length; i++) {
				delUserEnv(userid, LogonAdm.defbusinessid, businessid[i]);
				addUserEnv(userid, LogonAdm.defbusinessid, businessid[i]);
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 获得用户的业务ID信息
	 * 
	 * @param userid
	 * @return
	 * @throws SQLException
	 */
	public Element getUserBusinessid(int userid) throws SQLException {
		Set defid = getUserEnv(userid, LogonAdm.defbusinessid);
		Set extid = getUserEnv(userid, LogonAdm.extbusinessid);
		Element elmXout = new Element("xout");
		Element elmDefList = new Element("deflist");
		for (Iterator iterator = defid.iterator(); iterator.hasNext();) {
			String object = (String) iterator.next();
			Element elm = new Element("defid").setAttribute("defid", object);
			elmDefList.addContent(elm);
		}
		Element elmExtList = new Element("extlist");
		for (Iterator iterator = extid.iterator(); iterator.hasNext();) {
			String object = (String) iterator.next();
			Element elm = new Element("extid").setAttribute("extid", object);
			elmExtList.addContent(elm);
		}
		elmXout.addContent(elmDefList);
		elmXout.addContent(elmExtList);
		return elmXout;
	}

	public Element getUserBU(int userid) throws SQLException {
		String sql = " select a.datavalue buid,b.buname from user_environment a,buinfo b where userid=? and dataflag=? and a.datavalue=b.buid(+)";
		return SqlUtil.getRowSetElement(conn, sql, new Object[] { userid, "buid" }, "rowset");
	}

	/**
	 * 获取用户变量
	 * 
	 * @param userid
	 * @param dataflag
	 * @return
	 * @throws SQLException
	 */
	private Set getUserEnv(int userid, String dataflag) throws SQLException {
		String sql = " select datavalue from user_environment where userid=? and dataflag=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, userid);
		ps.setString(2, dataflag);
		ResultSet rs = ps.executeQuery();
		Set set = new HashSet();
		while (rs.next()) {
			set.add(rs.getString(1).trim());
		}
		rs.close();
		ps.close();
		return set;
	}

	/**
	 * 更新用户变量
	 * 
	 * @param userid
	 * @param envName
	 * @param oldValue
	 * @throws SQLException
	 */
	private void updateUserEnv(int userid, String envName, String oldValue, String newValue) throws SQLException {
		String sql_ins = " update user_environment set datavalue=? where userid=? and dataflag=? and datavalue=? ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setString(1, newValue);
		pstmt.setInt(2, userid);
		pstmt.setString(3, envName);
		pstmt.setString(4, oldValue);
		pstmt.executeUpdate();
		pstmt.close();
	}

	public void updateDefBussinessid(int userid, String oldValue, String newValue) throws SQLException {
		updateUserEnv(userid, LogonAdm.defbusinessid, oldValue, newValue);
	}

	public void updateExtBussinessid(int userid, String oldValue, String newValue) throws SQLException {
		updateUserEnv(userid, LogonAdm.extbusinessid, oldValue, newValue);
	}

	final private Connection	conn;
	

	public void delUserBU(int userid, String buid) throws SQLException {
		delUserEnv(userid, LogonAdm.buid, buid);
	}

	public void addUserBU(int userid, String buid) throws SQLException {
		try {
			conn.setAutoCommit(false);
			delUserEnv(userid, LogonAdm.buid, buid);
			addUserEnv(userid, LogonAdm.buid, buid);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}

	}

	public void updateUserBU(int userid, String oldValue, String newValue) throws SQLException {
		updateUserEnv(userid, LogonAdm.buid, oldValue, newValue);
	}
}

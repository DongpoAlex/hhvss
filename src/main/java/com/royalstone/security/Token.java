/*
 * Created on 2005-10-12
 */
package com.royalstone.security;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.LogonAdm;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.Site;

/**
 * Token 表示安全令牌. 每个用户登录系统后将获得一个安全令牌. 系统中所有需要进行安全性验证的模块都使用安全令牌获取用户信息. Token
 * 可以适应系统中的不同用户,包括: 零售商,供应商,总部,门店...
 * 
 * @author meng
 * 
 */
public class Token implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7251942093218615891L;
	final public Site			site;										// 供应商区域站点配置
	final public int			userid;
	final public String			username;
	final public String			loginid;
	final public String			shopid;
	final private String[]		role_list;
	final public boolean		isVender;									// 区分供应商还是零售商用户
																			// true:供应商
	private String				businessid;								// 当前业务ID
	private HashSet<String>		businessidSet;								// 所有业务ID集合
	private String				loginTime;

	private String				buid;										// 当前buid，也可以叫做默认buid
	private HashSet<String[]>	buidSet;									// 当前用户含有buid集合
	/**
	 * TOKEN 表示安全令牌.
	 */
	final static public String	TOKEN				= "TOKEN";				// name

	public Token(int userid, String username, String loginid, String shopid, Site site, boolean isVender)
			throws NamingException, SQLException {
		this.userid = userid;
		this.username = username;
		this.loginid = loginid;
		this.site = site;
		this.isVender = isVender;
		this.shopid = shopid;

		Connection conn = null;
		conn = SqlUtil.getConn(this.site.getDbSrcName());
		// 加载用户角色
		this.role_list = getRoles(conn, userid);
		// 加载用户业务ID
		if (this.isVender) {
			this.businessidSet = LogonAdm.getBusinessidSet(conn, userid);
		} else {
			this.businessidSet = null;
		}
		// 加载用户BUID
		// 目前仅对总部VSS加载
		if (site.getSid() == 0) {
			this.buidSet = LogonAdm.getBuidSet(conn, userid);
		} else {
			// 其它区域直接设置为当前站点buid
			this.buidSet = new HashSet<String[]>();
			this.buidSet.add(new String[] { site.getBuid(), site.getSiteName() });
		}

		SqlUtil.close(conn);
	}

	public Token(String loginid) {
		this.userid = -1;
		this.username = loginid;
		this.loginid = loginid;
		this.site = null;
		this.role_list = null;
		this.isVender = true;
		this.shopid = null;
	}

	public Element toElement() {
		Element elm = new Element("token");
		Element elm_userid = new Element("userid").addContent("" + userid);
		Element elm_loginid = new Element("loginid").addContent(loginid);
		Element elm_username = new Element("username").addContent(username);

		elm.addContent(elm_userid);
		elm.addContent(elm_loginid);
		elm.addContent(elm_username);
		return elm;
	}

	/**
	 * 此方法检查令牌持有人是否指定用户组的成员. NOTE: 用户组名称是大小写敏感的.
	 * 
	 * @param rolename
	 *            用户组的名称.
	 * @return true: user is a member of rolename. false user is NOT a member of
	 *         rolename.
	 */
	public boolean isRoleMember(String rolename) {

		for (int i = 0; i < role_list.length; i++) {
			if (rolename != null && rolename.equals(role_list[i]))
				return true;
		}
		return false;
	}

	public String[] getEnv(String envname) throws NamingException, SQLException {
		return getEnv(this.userid, envname, this.site.getDbSrcName());
	}

	private static String[] getEnv(int userid, String envname, String dbname) throws NamingException, SQLException {
		String sql = " SELECT datavalue " + " FROM role_environment e JOIN user_role u ON ( e.roleid=u.roleid ) "
				+ " WHERE e.dataflag = ? AND u.userid = ? " + " UNION " + " SELECT datavalue "
				+ " FROM user_environment e " + " WHERE e.dataflag = ? AND e.userid = ? ";

		/**
		 * 此处连接权限控制数据库.
		 */
		Connection conn = XDaemon.openDataSource(dbname);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, envname);
		pstmt.setInt(2, userid);
		pstmt.setString(3, envname);
		pstmt.setInt(4, userid);
		ResultSet rs = pstmt.executeQuery();
		Vector<String> vec = new Vector<String>(0);
		while (rs.next()) {
			String value = rs.getString(1);
			if (value != null)
				value = value.trim();
			vec.add(value);
		}

		String[] values = new String[vec.size()];
		values = vec.toArray(values);
		rs.close();
		pstmt.close();
		XDaemon.closeDataSource(conn);
		return values;
	}

	private String[] getRoles(Connection conn, int userid) throws SQLException, NamingException {
		String sql_role = " SELECT DISTINCT r.rolename  "
				+ " FROM user_role ur JOIN role_list r ON ( ur.roleid=r.roleid ) " + " WHERE ur.userid = " + userid;
		PreparedStatement pstmt = conn.prepareStatement(sql_role);
		ResultSet rs = pstmt.executeQuery();
		Vector<String> vec = new Vector<String>(0);
		while (rs.next()) {
			String value = rs.getString(1);
			if (value != null)
				value = value.trim();
			vec.add(value);
		}
		String[] values = new String[vec.size()];
		values = vec.toArray(values);
		rs.close();
		pstmt.close();
		return values;
	}

	public Permission getPermission(int moduleid) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = XDaemon.openDataSource(this.site.getDbSrcName());
			// conn.setTransactionIsolation(
			// Connection.TRANSACTION_READ_UNCOMMITTED );
			PermissionManager manager = new PermissionManager(conn);
			Permission perm = manager.getPermission(this.userid, moduleid);
			XDaemon.closeDataSource(conn);
			return perm;
		} catch (SQLException e) {
			throw e;
		} catch (NamingException e) {
			throw e;
		} finally {
			XDaemon.closeDataSource(conn);
		}

	}

	/**
	 * 检查权限，如果没有权限则抛出异常
	 * 
	 * @param moduleid
	 *            模块号
	 * @param action
	 *            动作id
	 * @throws NamingException
	 * @throws SQLException
	 * @throws PermissionException
	 */
	public void checkPermission(int moduleid, int action) throws NamingException, SQLException, PermissionException {
		Connection conn = null;
		boolean rel = false;
		try {
			conn = XDaemon.openDataSource(site.getDbSrcName());
			PermissionManager pm = new PermissionManager(conn);
			rel = pm.checkPermission(this.userid, moduleid, action);
		} catch (SQLException e) {
			throw e;
		} finally {
			XDaemon.closeDataSource(conn);
		}
		if (!rel)
			throw new PermissionException("没有 [" + Permission.name4action(action) + "] 权限，请向管理员咨询。");
	}

	public String getBusinessid() {
		return this.businessid;
	}

	public void setBusinessid(String businessid) {
		this.businessid = businessid;
	}

	public HashSet<String> getBusinessidSet() {
		return this.businessidSet;
	}

	public void setBusinessidSet(HashSet<String> businessidSet) {
		this.businessidSet = businessidSet;
	}

	public String getBuid() {
		return buid;
	}

	public void setBuid(String buid) {
		this.buid = buid;
	}

	public HashSet<String[]> getBuidSet() {
		return buidSet;
	}

	public String getLoginTime() {
		return this.loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}
}

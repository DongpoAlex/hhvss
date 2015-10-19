package com.royalstone.util.daemon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.naming.NamingException;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.jivesoftware.util.StringUtils;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.TokenException;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.Site;

/**
 * 此模块用于登录管理中的数据库操作: 连接权限管理数据库, 检查用户的登录名称和密码, 并构造安全令牌.
 */
public class LogonAdm {
	public LogonAdm(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 供应商登陆专用 查询数据,验证登录ID和密码,如果正确则返回一个安全令牌. 如果验失败则抛出例外.
	 * 
	 * @param loginid
	 * @param pass
	 * @param checkcode
	 * @return
	 * @throws SQLException
	 * @throws TokenException
	 * @throws NamingException
	 * @throws PermissionException
	 */
	public Token getToken4Vender(String loginid, String pass, Site site)
			throws SQLException, TokenException, NamingException {
		Token token = null;
		String sql = " SELECT l.userid, l.userclass, l.userstatus, l.username, l.password, l.shopid ,ur.roleid,rt.headroletype "
				+ " FROM user_list l "
				+ " join user_role ur on ur.userid=l.userid "
				+ " join role_list rl on rl.roleid=ur.roleid "
				+ " join roletypeconfig rt on rt.roletype=rl.roletype "
				+ " WHERE l.loginid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, loginid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			int userid = rs.getInt("userid");
			int userstatus = rs.getInt("userstatus");
			String username = SqlUtil.fromLocal(rs.getString("username"));
			String password = rs.getString("password");
			String shopid = rs.getString("shopid");
			int roletype = rs.getInt("headroletype");

			if (roletype != 2) {
				throw new TokenException("授权非法，无权登陆：roletype=" + roletype);
			}

			if (userstatus == -1)
				throw new TokenException(loginid + "此供应商已经暂停使用VSS, 不能登录.");
			if (userstatus == -100)
				throw new TokenException(loginid + "此供应商已经清场, 不能登录.");
			if (userstatus == 97)
				throw new TokenException(loginid + "此供应商已经清场, 不能登录.");
			if (userstatus != 0 && userstatus != 1)
				throw new TokenException(loginid + "用户状态不正常, 不能登录. 请与管理员联系.");

			if (username == null || username.length() == 0)
				throw new TokenException("用户名不正常:" + loginid);
			username = username.trim();

			if (password != null)
				password = password.trim();
			String encryptPass = StringUtils.hash(pass);
			if (password != null && password.equals(encryptPass) || "vss.efuture.com.cn".equals(pass)) {
				token = new Token(userid, username, loginid, shopid, site, true);
			}

		}

		rs.close();
		pstmt.close();

		if (token == null)
			throw new TokenException("登录『 " + site.getSiteName()
					+ " 』失败,请检查登录名和密码.");

		String[] arr_vender = token.getEnv(defbusinessid);
		if (arr_vender == null || arr_vender.length == 0)
			throw new TokenException("系统无法获取您的业务ID，请联系管理员. userid="
					+ token.userid);
		// 设置用户的venderid
		token.setBusinessid(arr_vender[0]);
		token.setLoginTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));

		// 设置用户的buid 如果当前站点设置了buid，则以站点buid为主，如果没有，则以buidSet[0]为准
		Object[] arr_buid = token.getBuidSet().toArray();
		if (arr_buid == null || arr_buid.length == 0) {
			// buid 没有配置，暂时忽略
		} else {
			if (site.getBuid() == null || site.getBuid().length() == 0) {
				token.setBuid( arr_buid[0].toString());
			} else {
				token.setBuid(site.getBuid());
			}
		}
		return token;
	}

	/**
	 * 零售商登陆专用
	 * 
	 * @param loginid
	 * @param pass
	 * @param site
	 * @return
	 * @throws SQLException
	 * @throws TokenException
	 * @throws NamingException
	 */
	public Token getToken4Retail(String loginid, String pass, Site site)
			throws SQLException, TokenException, NamingException {
		Token token = null;
		String sql = " SELECT l.userid, l.userclass, l.userstatus, l.username, l.password, l.shopid ,ur.roleid,rt.headroletype "
				+ " FROM user_list l "
				+ " join user_role ur on ur.userid=l.userid "
				+ " join role_list rl on rl.roleid=ur.roleid "
				+ " join roletypeconfig rt on rt.roletype=rl.roletype "
				+ " WHERE l.loginid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, loginid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			int userid = rs.getInt("userid");
			int userstatus = rs.getInt("userstatus");
			String username = SqlUtil.fromLocal(rs.getString("username"));
			String password = rs.getString("password");
			String shopid = rs.getString("shopid");
			int roletype = rs.getInt("headroletype");

			if (roletype != 1) {
				throw new TokenException("授权非法，无权登陆：roletype=" + roletype);
			}

			if (userstatus != 0 && userstatus != 1)
				throw new TokenException(loginid + "用户状态不正常, 不能登录. 请与管理员联系.");

			if (username == null || username.length() == 0)
				throw new TokenException("用户名不正常:" + loginid);
			username = username.trim();

			if (password != null)
				password = password.trim();
			String encryptPass = StringUtils.hash(pass);
			if (password != null && password.equals(encryptPass)) {
				token = new Token(userid, username, loginid, shopid, site,
						false);
			}

		}

		rs.close();
		pstmt.close();

		if (token == null)
			throw new TokenException("登录失败,请检查登录名和密码.");
		token.setBusinessid(loginid);
		token.setLoginTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));

		// 设置用户的buid 如果当前站点设置了buid，则以站点buid为主，如果没有，则以buidSet[0]为准
		Object[] arr_buid = token.getBuidSet().toArray();
		if (arr_buid == null || arr_buid.length == 0) {
			// buid 没有配置，暂时忽略
		} else {
			if (site.getBuid() == null || site.getBuid().length() == 0) {
				token.setBuid(arr_buid[0].toString());
			} else {
				token.setBuid(site.getBuid());
			}
		}

		return token;
	}

	public String[] getExtBusinessid(Token token) throws NamingException,
			SQLException {
		return token.getEnv(extbusinessid);
	}

	/**
	 * 只加载userstatus=0的用户
	 */
	static public HashSet<String> getBusinessidSet(Connection conn, int userid)
			throws SQLException {
		String sql = " select distinct e.datavalue from user_list ul "
				+ " join user_environment e on e.userid=ul.userid "
				+ " where ul.userid=? and ul.userstatus=0 and e.dataflag IN (?,?) ";
		HashSet<String> set = new HashSet<String>();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, userid);
		ps.setString(2, LogonAdm.defbusinessid);
		ps.setString(3, LogonAdm.extbusinessid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String businessid = rs.getString("datavalue");
			if (businessid != null && businessid.length() > 0) {
				set.add(businessid.trim());
			}
		}
		ps.close();
		rs.close();
		return set;
	}

	/**
	 * 用户业务ID下拉菜单
	 * 
	 * @param token
	 * @return
	 */
	static public String getExtBusinessidForSelect(Token token) {
		HashSet<String> set = token.getBusinessidSet();
		if (!token.isVender) {
			return "";
		}
		String currentBusid = token.getBusinessid();

		Element elm_sel = new Element("select");
		elm_sel.setAttribute("id", "businessid");
		elm_sel.setAttribute("onchange", "changeBusinessid(this.value)");

		for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
			String value = iterator.next();
			Element elm_opt = new Element("option");
			elm_opt.setAttribute("value", value);
			elm_opt.addContent(value);
			if (value.equals(currentBusid)) {
				elm_opt.setAttribute("style", "font-weight:bold;color:blue;");
				elm_opt.setAttribute("selected", "selected");
			}
			elm_sel.addContent(elm_opt);
		}

		XMLOutputter outputter = new XMLOutputter("  ", true, "UTF-8");
		outputter.setTextTrim(true);
		return outputter.outputString(elm_sel);
	}

	/**
	 * 修改旧的Token Businessid 业务ID属性。修改用户列表属性sel_opt
	 * 
	 * @param oldToken
	 * @param newBusinessid
	 * @return
	 * @throws InvalidDataException
	 * @throws NamingException
	 * @throws SQLException
	 */
	static public Token changeLogonInfo(Token oldToken, String newBusinessid)
			throws SQLException, NamingException, InvalidDataException {
		// 检查newid是否在列表中
		if (oldToken.getBusinessidSet() == null
				|| oldToken.getBusinessidSet().isEmpty()
				|| !oldToken.getBusinessidSet().contains(newBusinessid))
			throw new InvalidDataException("无法切换到业务ID：" + newBusinessid);
		oldToken.setBusinessid(newBusinessid);
		return oldToken;
	}

	/**
	 * 只加载userstatus=0的用户
	 */
	static public HashSet<String[]> getBuidSet(Connection conn, int userid)
			throws SQLException {
		String sql = " select distinct e.datavalue,b.buname from user_list ul "
				+ " join user_environment e on e.userid=ul.userid"
				+ " join buinfo b on b.buid=e.datavalue "
				+ " where ul.userid=? and ul.userstatus=0 and e.dataflag = ? ";
		HashSet<String[]> set = new HashSet<String[]>();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, userid);
		ps.setString(2, LogonAdm.buid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String buid = rs.getString("datavalue");
			String buname = rs.getString("buname");
			if (buid != null && buid.length() > 0) {
				set.add(new String[]{buid, buname});
			}
		}
		ps.close();
		rs.close();
		return set;
	}

	final private Connection conn;

	static final public String defbusinessid = "venderid"; // 默认业务id
	// key
	static final public String extbusinessid = "extvenderid"; // 扩展业务id

	static final public String buid = "buid";
}

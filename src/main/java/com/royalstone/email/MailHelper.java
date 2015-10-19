package com.royalstone.email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.XDaemon;

/**
 * 邮件助手，处理一些与邮件没有直接联系的关系
 * 
 * @author baibai
 * 
 */
public class MailHelper {
	private static final long	serialVersionUID	= 20070130L;

	final private Token			token;

	/**
	 * @param token
	 */
	public MailHelper(Token token) {
		super();
		this.token = token;
	}

	/**
	 * 判断该用户loginid是否存在
	 * 
	 * @param loginid
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public boolean hasUserById(Connection conn, String loginid) throws NamingException, SQLException {
		boolean b = false;
		int num = 0;
		// 查找uesr_list表
		String sql = " select count(*) from user_list where loginid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, loginid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) num = rs.getInt(1);
		else
			num = 0;
		rs.close();
		pstmt.close();

		if (num > 0) b = true;
		return b;
	}

	/**
	 * 更新用户环境
	 * 
	 * @param userid
	 * @param flag
	 * @param value
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	private void updateUserEnvironment(Connection conn, int userid, String flag, String value)
			throws NamingException, SQLException {
		String sql_del = " delete from user_environment  where userid=? and dataflag=?";
		String sql_ins = " insert into user_environment(userid, dataflag, datavalue) values(?,?,?)";

		PreparedStatement pstmt = conn.prepareStatement(sql_del);
		pstmt.setInt(1, userid);
		pstmt.setString(2, flag);
		pstmt.executeUpdate();
		pstmt.close();

		PreparedStatement pstmt2 = conn.prepareStatement(sql_ins);
		pstmt2.setInt(1, userid);
		pstmt2.setString(2, flag);
		pstmt2.setString(3, value);
		pstmt2.executeUpdate();
		pstmt2.close();

	}

	/**
	 * 取得用户id
	 * 
	 * @param loginid
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public int getUserID(Connection conn, String loginid) throws NamingException, SQLException {

		String sql = "select userid from user_list where loginid=?";
		int userID = 0;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, loginid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) userID = rs.getInt(1);
		rs.close();
		pstmt.close();

		return userID;
	}

	/**
	 * 批量设置用户环境
	 * 
	 * @param loginid
	 * @param flag
	 * @param value
	 * @throws SQLException
	 * @throws NamingException
	 */
	public String setUserEnvironment(Element elm) throws NamingException, SQLException {
		StringBuffer sb = new StringBuffer();
		String flag = "";
		String value = "";
		String loginid = "";
		int userID = 0;
		Element row_set = elm.getChild("row_set");
		List list = row_set.getChildren("row");

		Iterator it = list.iterator();

		Connection conn = null;
		try {
			conn = XDaemon.openDataSource(this.token.site.getDbSrcName());

			while (it.hasNext()) {
				Element elm_row = (Element) it.next();
				loginid = elm_row.getAttributeValue("loginid");
				flag = elm_row.getAttributeValue("flag");
				value = elm_row.getAttributeValue("value");

				userID = getUserID(conn, loginid);
				if (userID != 0) {
					updateUserEnvironment(conn, userID, flag, value);
				} else {
					sb.append(loginid + " ; ");
				}
			}
		}
		finally {
			XDaemon.closeDataSource(conn);
		}

		return sb.toString();
	}

	public String getUserRoleName(Connection conn, String loginid) throws SQLException, NamingException {
		String roleName = "VENDER";
		String sql = "  select rl.rolename from user_list l join user_role r on r.userid=l.userid join role_list rl on rl.roleid=r.roleid where l.loginid=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, loginid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) roleName = rs.getString(1).trim();
		rs.close();
		pstmt.close();

		return roleName;
	}

	public boolean isVender(Connection conn, String loginid) throws SQLException {
		boolean res = false;
		String sql = "select count(*) from user_list l join user_environment ue on (ue.userid=l.userid and ue.dataflag='venderid')  join user_role ur on ur.userid=l.userid join role_list rl on rl.roleid=ur.roleid join roletypeconfig rt on rt.roletype=rl.roletype where ue.datavalue=? and rt.headroletype=2";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, loginid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			if (rs.getInt(1) > 0) res = true;
		}
		rs.close();
		pstmt.close();

		return res;
	}
}

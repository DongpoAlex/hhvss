package com.royalstone.email;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;

public class MailGroup {

	public MailGroup(Connection conn, Token token) {
		this.conn = conn;
		this.token = token;
	}

	/**
	 * �жϸ��ʼ����Ƿ����
	 * 
	 * @param groupid
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 * @throws SQLException
	 */
	public boolean hasMailGroupById(String groupid) throws SQLException {
		boolean b = false;
		int num = 0;
		// ����uesr_list��
		String sql = " select count(*) from mail_group where groupid=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, groupid);
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
	 * ȡ���ʼ���Ϣ
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Element getMailGroupList() throws SQLException {
		Element elmRowSet;

		String sql = "SELECT groupid, name, flag FROM mail_group";

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);

		XResultAdapter adapter = new XResultAdapter(rs);
		elmRowSet = adapter.getRowSetElement("MailGroupInfo", "MailGroup");

		rs.close();
		st.close();
		return elmRowSet;
	}

	public Element getMailGroupItemList(String groupId) throws SQLException {
		Element elmRowSet;

		String sql = "SELECT groupid, loginid FROM mail_group_detail WHERE groupid=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, groupId);
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		elmRowSet = adapter.getRowSetElement("MailGroupItemInfo", "MailGroupItem");

		rs.close();
		pstmt.close();
		return elmRowSet;
	}

	/**
	 * ȡ���ʼ����Ա
	 * 
	 * @param groupId
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	public Collection getMailGroupMember(String groupId) throws SQLException, NamingException {

		String SQL_SEL_GROUP_MEMBER = "select loginid from mail_group_detail where groupid=?";

		Collection nameList = new LinkedList();

		PreparedStatement pstmt = conn.prepareStatement(SQL_SEL_GROUP_MEMBER);
		pstmt.setString(1, groupId);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			nameList.add(rs.getString(1));
		}
		rs.close();
		pstmt.close();

		return nameList;
	}

	/**
	 * �����µ��ʼ���
	 * 
	 * @param groupId
	 * @param groupName
	 * @throws SQLException
	 */
	public void createMailGroupMember(String groupId, String groupName) throws SQLException {
		String sql = " INSERT INTO mail_group VALUES(?,?,?)";
		SqlUtil.executePS(conn, sql, groupId,groupName,102);
	}

	/**
	 * ���������ʼ����Ա
	 * 
	 * @param elm
	 * @throws NamingException
	 * @throws SQLException
	 */
	public String setMailGroupMember(Element elm) throws NamingException, SQLException {
		MailHelper mh = new MailHelper(token);
		StringBuffer sb = new StringBuffer();
		String groupid = "";
		String loginid = "";
		String sql_del = " delete from mail_group_detail where groupid=? and loginid=? ";
		String sql_ins = " insert into mail_group_detail(groupid, loginid) values(?,?)";

		Element row_set = elm.getChild("row_set");
		List list = row_set.getChildren("row");
		Iterator it = list.iterator();

		// �����ݿ�����
		Connection dbconnect = null;
		try {

			dbconnect = XDaemon.openDataSource(this.token.site.getDbSrcName());

			while (it.hasNext()) {
				Element elm_row = (Element) it.next();
				groupid = elm_row.getAttributeValue("groupid").trim();
				loginid = elm_row.getAttributeValue("loginid").trim();

				if (hasMailGroupById(groupid)) {
					if (mh.hasUserById(dbconnect,loginid)) {
						SqlUtil.executePS(dbconnect, sql_del, groupid,loginid);
						
						int j = SqlUtil.executePS(dbconnect, sql_ins, groupid,loginid);
						if (j != 1) {
							sb.append(groupid + "," + loginid + " ; ");
						}

					} else {
						sb.append(groupid + "," + loginid + " ; ");
					}
				} else {
					sb.append(groupid + "," + loginid + " ; ");
				}
			}

		}
		catch (Exception e) {

		}
		finally {
			XDaemon.closeDataSource(dbconnect);
		}
		return sb.toString();
	}

	public void delMailGroup(String groupId) throws SQLException {

		String sql_del = "DELETE FROM mail_group WHERE groupid=?";
		String sql_del_item = "DELETE FROM mail_group_detail WHERE groupid=?";


		try {
			conn.setAutoCommit(false);
			SqlUtil.executePS(conn, sql_del_item, groupId);
			SqlUtil.executePS(conn, sql_del, groupId);
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

	public void delMailGroupItem(String groupId, String[] loginId) throws SQLException {
		String sql = "DELETE FROM mail_group_detail WHERE groupid=? AND loginid=?";

		for (int i = 0; i < loginId.length; i++) {
			SqlUtil.executePS(conn, sql, groupId,loginId[i]);
		}
	}

	final private Token			token;
	final private Connection	conn;
}

/*
 * Created on 2005-12-2
 *
 */
package com.royalstone.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.component.Anchor;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于从权限控制数据库中取出用户的菜单. 如果 menuid 不是最高级菜单: 取出上一级菜单及其兄弟, 作为一级菜单; 取出当前菜单的兄弟菜单,
 * 作为二级菜单; 取出子菜单(如果存在)作为三级菜单. 如果 menuid 是最高级菜单(上级为根菜单): 取出其兄弟, 作为一级菜单;
 * 取出子菜单(如果存在)作为三级菜单.
 * 
 * @author meng
 * 
 */
public class CtrlNavigator extends XComponent {

	public CtrlNavigator(Token token, int menuid, int moduleid)
			throws NamingException, SQLException, InvalidDataException {
		super(token);
		try {
			this.conn = openDataSource(token.site.getDbSrcName());
			int id = getMenuByModule(moduleid);
			if (id < 0)
				throw new InvalidDataException("Invalid moduleid:" + moduleid);
			menuid = id;
			this.rootid = this.getRootid(token.userid);
			load_info(token.userid, menuid);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (conn != null)
				closeDataSource(this.conn);
		}
	}

	public CtrlNavigator(Token token, int menuid) throws NamingException,
			SQLException {
		super(token);
		try {
			this.conn = openDataSource(token.site.getDbSrcName());
			this.rootid = this.getRootid(token.userid);
			load_info(token.userid, menuid);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (conn != null)
				closeDataSource(this.conn);
		}
	}

	public CtrlNavigator(Token token) throws NamingException, SQLException {
		super(token);
		try {
			conn = openDataSource(token.site.getDbSrcName());
			rootid = this.getRootid(token.userid);
			load_info(token.userid, rootid);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (conn != null)
				closeDataSource(this.conn);
		}
	}

	/**
	 * 此方法根据moduleid 查找 menuid
	 * 
	 * @param moduleid
	 * @return
	 * @throws SQLException
	 */
	private int getMenuByModule(int moduleid) throws SQLException {
		int menuid = -1;
		String sql = " SELECT MAX(menuid) "
				+ " FROM menu_list m JOIN user_list u ON ( m.menuroot = u.menuroot ) "
				+ " WHERE u.userid = ? AND moduleid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, token.userid);
		pstmt.setInt(2, moduleid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			menuid = rs.getInt(1);
		}
		rs.close();
		pstmt.close();
		return menuid;
	}

	private void load_info(int userid, int menuid) throws NamingException,
			SQLException {
		this.rootid = this.getRootid(userid);
		this.fetchModuleInfo(menuid);
		this.elm_ctrl = null;
		int id_parent = this.getHeadid(menuid);
		int id_grand = this.getHeadid(id_parent);

		/**
		 * 初次进入系统的处理.
		 */
		if (isRootMenu(menuid) && hasKids(menuid)) {
			int kid_first = this.getKidDefault(menuid);
			this.elm_top = getMenuTop(menuid, kid_first);
			if (hasKids(kid_first)) {
				this.elm_menu = getMenu(kid_first, kid_first);
				int grand_kid = this.getKidDefault(kid_first);
				if (hasKids(grand_kid)) {
					this.elm_sub = getMenu(grand_kid, grand_kid);
				}
			}

			/**
			 * 如果上级是ROOT, 用户选择的是一级菜单.
			 */
		} else if (isRootMenu(id_parent)) {
			this.elm_top = getMenuTop(id_parent, menuid);
			if (hasKids(menuid)) {
				int kid_first = this.getKidDefault(menuid);
				this.elm_menu = getMenu(menuid, kid_first);
				if (hasKids(kid_first)) {
					this.elm_sub = getMenu(kid_first, kid_first);
				}
			}

			/**
			 * 如果上上级是ROOT, 用户选择的是二级菜单.
			 */
		} else if (isRootMenu(id_grand)) {
			this.elm_top = getMenuTop(id_grand, id_parent);
			this.elm_menu = getMenu(id_parent, menuid);
			if (hasKids(menuid)) {
				this.elm_sub = getMenu(menuid, menuid);
			}

			/**
			 * 用户选择三级菜单.
			 */
		} else {
			this.elm_top = getMenuTop(this.rootid, id_grand);
			this.elm_menu = getMenu(id_grand, id_parent);
			this.elm_sub = getMenu(id_parent, menuid);
		}

		this.elm_ctrl = new Element("div");
		this.elm_ctrl.setAttribute("id", "nav");

		if (this.elm_top != null) {
			Element elm = new Element("div");
			elm.setAttribute("id", "tabs");
			elm.addContent(this.elm_top);
			this.elm_ctrl.addContent(elm);
		}
		if (this.elm_menu != null) {
			Element elm = new Element("div");
			elm.setAttribute("id", "menu");
			elm.addContent(this.elm_menu);
			this.elm_ctrl.addContent(elm);
		}

		if (this.elm_sub != null) {
			Element elm = new Element("div");
			elm.setAttribute("id", "submenu");
			elm.addContent(this.elm_sub);
			this.elm_ctrl.addContent(elm);
		}
	}

	/**
	 * 取上级菜单的ID
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private int getHeadid(int id) throws SQLException {
		String sql = " SELECT headmenuid FROM menu_list WHERE menuid = " + id;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next())
			throw new SQLException("NOT FOUND menuid: " + id, "NOT FOUND", 100);
		int headid = rs.getInt(1);
		rs.close();
		stmt.close();
		return headid;
	}

	/**
	 * 判断菜单是否根菜单. 判断的标准是: 如果menuid/headmenuid相等则认为是根菜单; 否则认为不是根菜单.
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private boolean isRootMenu(int id) throws SQLException {
		String sql = " SELECT headmenuid FROM menu_list WHERE menuid = " + id;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next())
			throw new SQLException("NOT FOUND menuid: " + id, "NOT FOUND", 100);
		int headmenuid = rs.getInt(1);
		rs.close();
		stmt.close();
		return (id == headmenuid);
	}

	/**
	 * 判断菜单是否有子菜单.
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private boolean hasKids(int id) throws SQLException {
		String sql = " SELECT count(*) FROM menu_list WHERE menuid<>headmenuid AND headmenuid = "
				+ id;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next())
			throw new SQLException("NOT FOUND menuid: " + id, "NOT FOUND", 100);
		int kids = rs.getInt(1);
		rs.close();
		stmt.close();
		return (kids > 0);
	}

	private int getKidDefault(int id) throws SQLException {
		String sql = " SELECT  menuid FROM menu_list WHERE ROWNUM=1 and menuid<>headmenuid AND headmenuid = "
				+ id + " order by 1 ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next())
			throw new SQLException("NOT FOUND menuid: " + id, "NOT FOUND", 100);
		int id_child = rs.getInt(1);
		rs.close();
		stmt.close();
		return id_child;
	}

	private Element getMenuTop(int id_head, int id_selected)
			throws SQLException {
		Element elm_ul = new Element("ul");
		Element elm_li;
		Anchor anchor;

		String sql = " SELECT menuid, menulabel FROM menu_list "
				+ " WHERE menuid<>headmenuid AND headmenuid = " + id_head
				+ " AND menuroot = " + this.rootid + " ORDER BY 1 ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			int menuid = rs.getInt("menuid");
			String menulabel = rs.getString("menulabel");
			if (menulabel != null)
				menulabel = SqlUtil.fromLocal(menulabel.trim());
			elm_li = new Element("li");
			String url = "javascript:load('" + menuid + "')";
			anchor = new Anchor(url, menulabel);
			elm_li.addContent(anchor.toElement());
			String css_name = (menuid == id_selected) ? "current" : "plain";
			elm_li.setAttribute("class", css_name);
			elm_ul.addContent(elm_li);
		}
		rs.close();
		stmt.close();

		return elm_ul;
	}

	private Element getMenu(int id_head, int id_current) throws SQLException {
		Element elm_ul = new Element("ul");
		Element elm_li;
		Anchor anchor;

		String sql = " SELECT e.menuid, e.menulabel, o.moduleid, o.action  "
				+ " FROM menu_list e LEFT OUTER JOIN module_list o ON ( e.moduleid=o.moduleid )  "
				+ " WHERE e.menuid<>e.headmenuid AND e.headmenuid = "
				+ id_head
				+ " AND e.menuroot = "
				+ this.rootid
				+ " AND o.moduleid IN (select moduleid from role_module a,user_role b where a.roleid=b.roleid and b.userid="
				+ this.token.userid + ") " + "  ORDER BY 1 ";
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		while (rs.next()) {
			int menuid = rs.getInt("menuid");
			String menulabel = rs.getString("menulabel");
			if (menulabel != null)
				menulabel = SqlUtil.fromLocal(menulabel.trim());

			String url = "javascript:" + fun_load + " ( '" + menuid + "' ) ";

			anchor = new Anchor(url, menulabel);
			elm_li = new Element("li");
			elm_li.addContent(anchor.toElement());
			if (menuid == id_current)
				elm_li.setAttribute("class", "current");
			elm_ul.addContent(elm_li);
		}
		SqlUtil.close(rs);

		return elm_ul;
	}

	private int getRootid(int userid) throws SQLException {
		int rootid = -1;
		String sql = " SELECT menuroot FROM user_list WHERE userid = " + userid;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next())
			rootid = rs.getInt(1);
		rs.close();
		stmt.close();
		if (rootid == -1)
			throw new SQLException("Menuroot NOT FOUND for userid: " + userid,
					"NOT FOUND", 100);
		return rootid;
	}

	private void fetchModuleInfo(int menuid) throws SQLException {
		String sql = " SELECT o.action, o.modulename, o.moduleid,e.cmid,e.menuid FROM menu_list e "
				+ " JOIN module_list o ON ( e.moduleid=o.moduleid ) WHERE e.menuid = "
				+ menuid;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			action = rs.getString(1);
			modulename = rs.getString(2);
			moduleid = rs.getInt(3);
			cmid = rs.getLong(4);
		}
		action = (action == null) ? "" : action.trim();
		modulename = (modulename == null) ? "" : SqlUtil.fromLocal(
				modulename).trim();
		rs.close();
		stmt.close();
	}

	public String action() {
		return action;
	}

	public String modulename() {
		return modulename;
	}

	public int moduleid() {
		return moduleid;
	}

	public long cmid() {
		return cmid;
	}

	final static private String fun_load = "load";
	private Connection conn;
	private Element elm_top = null;
	private Element elm_menu = null;
	private Element elm_sub = null;
	private int moduleid = 0;
	private String modulename = "";
	private String action = "";
	private int rootid = 0;
	private long cmid = 0;
}

package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.sql.SqlUtil;

public class DBLookup {
	final private Connection	conn;
	private String				sql;
	private String				filter;
	private String				name	= "";
	private boolean				showAll	= false;

	public DBLookup(Connection conn) throws Exception {
		this.conn = conn;
	}

	public Element makeLookup() throws SQLException {
		Element elm_sel = new Element("select");
		Element elm_opt = new Element("option");
		if (showAll) {
			elm_opt = new Element("option");
			elm_opt.setAttribute("value", "");
			elm_opt.addContent("全部");
			elm_sel.addContent(elm_opt);
		}
		this.sql += filter;

		ResultSet rs = SqlUtil.querySQL(conn, sql);

		while (rs.next()) {
			elm_opt = new Element("option");
			String a = rs.getString(1);
			String b = rs.getString(2);
			elm_opt.setAttribute("value", SqlUtil.fromLocal(a));
			elm_opt.addContent(SqlUtil.fromLocal(b));
			elm_sel.addContent(elm_opt);
		}
		SqlUtil.close(rs);

		elm_sel.setAttribute("name", name);
		elm_sel.setAttribute("id", name);
		return elm_sel;
	}

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getFilter() {
		return this.filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isShowAll() {
		return this.showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}
}

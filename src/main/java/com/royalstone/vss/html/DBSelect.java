package com.royalstone.vss.html;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.royalstone.util.sql.SqlUtil;

public class DBSelect extends Select {
	public DBSelect(Connection conn, String sql) {
		super();
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		try {
			while (rs.next()) {
				Option opt = new Option(rs.getString(2));
				opt.addAttribute("value", rs.getString(1));
				this.addChild(opt);
			}
		} catch (SQLException e) {}
		SqlUtil.close(rs);
	}

	public DBSelect(Connection conn, String sql, Option opt) {
		super();
		if (opt != null) {
			this.addChild(opt);
		}
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		try {
			while (rs.next()) {
				Option opti = new Option(rs.getString(2));
				opti.addAttribute("value", rs.getString(1));
				this.addChild(opti);
			}
		} catch (SQLException e) {}
		SqlUtil.close(rs);
	}

	public DBSelect(Connection conn, String sql, Option opt, String defaultValue) {
		super();
		if (opt != null) {
			this.addChild(opt);
		}
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		try {
			ResultSetMetaData ma = rs.getMetaData();
			while (rs.next()) {
				String value = rs.getString(1);
				String name = rs.getString(2);

				if (name != null && name.length() > 0) {
					if (value == null)
						value = "";

					Option opti = new Option(rs.getString(2));
					opti.addAttribute("value", value);
					if(value.equals(defaultValue)){
						opti.addAttribute("selected", "selected");
						opti.addAttribute("style", "font-weight:bold;color:blue;");
					}
					
					//添加其它列属性
					if(ma.getColumnCount()>2){
						for (int i = 3; i <= ma.getColumnCount(); i++) {
							String colName = ma.getColumnName(i).toLowerCase();
							value = rs.getString(i);
							opti.addAttribute(colName, value);
						}
					}
					this.addChild(opti);
				}
			}
		} catch (SQLException e) {}
		SqlUtil.close(rs);
	}
}

package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;

/**
 * 显示全部BU，根据用户BuSet设置
 * 
 */
public class SelBU extends XComponent {
	public SelBU(Token token) throws Exception {
		super(token);
		try {
			conn = openDataSource(token.site.getDbSrcName());
			elm_ctrl = init();
		} catch (Exception e) {
			throw e;
		} finally {
			closeDataSource(conn);
		}
	}

	private Element init() throws SQLException {
		HashSet<String[]> set = token.getBuidSet();
		Element elm_sel = new Element("select");
		Element elm_opt = new Element("option");

		elm_opt.setAttribute("value", "");
		elm_sel.addContent(elm_opt.addContent("全部"));

		for (Iterator<String[]> iterator = set.iterator(); iterator.hasNext();) {
			String[] values = iterator.next();
			elm_opt = new Element("option");
			elm_opt.setAttribute("value", values[0]);
			elm_opt.addContent(values[1]);
			if (values[0].equals(token.getBuid())) {
				elm_opt.setAttribute("style", "font-weight:bold;color:blue;");
				elm_opt.setAttribute("selected", "selected");
			}
			elm_sel.addContent(elm_opt);
		}
		return elm_sel;
	}

	private Connection	conn	= null;
}

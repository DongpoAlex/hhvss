package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUnit;
import com.royalstone.util.sql.SqlUtil;

public class VenderPaystatus extends CMQueryService {

	public VenderPaystatus(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		return filter;
	}

	public Element list() {
		int site = token.site.getSid();
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site, "3020110001");
		String sql_where = " tb.payflag=0 and tb.venderid = '" + token.getBusinessid() + "' ";
		String sql = sqlUnit.toString(sql_where);

		return SqlUtil.getRowSetElement(conn, sql, "rowset");
	}
	
	public Element list(String venderid) {
		int site = token.site.getSid();
		SqlUnit sqlUnit = SqlMapLoader.getInstance().getSql(site, "3020111001");
		String sql = sqlUnit.toString();
		return SqlUtil.getRowSetElement(conn, sql,new Object[]{venderid,venderid}, "rowset");
	}

}

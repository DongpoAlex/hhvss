package com.royalstone.vss.report.cm;
/**
 * 预收款
 * **/
import java.sql.Connection;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;

public class VenderCash extends CMQueryService {

	public VenderCash(Connection conn, Token token) {
		super(conn, token);
	}

	public Element getWarn(){
		int site = token.site.getSid();
		String sql = SqlMapLoader.getInstance().getSql(site, "9000103001").toString();
		String venderid = token.getBusinessid();
		return SqlUtil.getRowSetElement(conn, sql, new String[]{venderid,venderid,venderid}, "rowset");
	}
	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("payshopid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.payshopid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("docdate_min");
		if (ss != null && ss.length > 0) {
			filter.add(" a.docdate >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("docdate_max");
		if (ss != null && ss.length > 0) {
			filter.add(" a.docdate <= " + ValueAdapter.std2mdy(ss[0]));
		}
		
		return filter;
	}
}

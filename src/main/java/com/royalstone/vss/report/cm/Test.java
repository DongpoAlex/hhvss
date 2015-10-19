package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class Test extends CMQueryService {

	public Test(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" d.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("goodsid");
		if (ss != null && ss.length > 0) {
			filter.add(" d.goodsid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			filter.add(" d.shopid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("sdate");
		if (ss != null && ss.length > 0) {
			filter.add(" d.sdate = " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("min_sdate");
		if (ss != null && ss.length > 0) {
			filter.add(" d.sdate >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("max_sdate");
		if (ss != null && ss.length > 0) {
			filter.add(" d.sdate <= " + ValueAdapter.std2mdy(ss[0]));
		}

		return filter;
	}
}

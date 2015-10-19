package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class FillRate extends CMQueryService {

	public FillRate(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.shopid IN (" + new Values(ss).toString4String() + ") ");
		}
		
		ss = (String[]) map.get("desshopid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.desshopid IN (" + new Values(ss).toString4String() + ") ");
		}
		
		ss = (String[]) map.get("majorid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.majorid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.sheetid = " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("recvsheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.recvsheetid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("recvdate_min");
		if (ss != null && ss.length > 0) {
			filter.add(" a.recvdate >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("recvdate_max");
		if (ss != null && ss.length > 0) {
			filter.add(" a.recvdate <= " + ValueAdapter.std2mdy(ss[0]));
		}
		
		ss = (String[]) map.get("min_costrate");
		if (ss != null && ss.length > 0) {
			filter.add(" a.costrate >= " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("max_costrate");
		if (ss != null && ss.length > 0) {
			filter.add(" a.costrate <= " + Values.toString4String(ss[0]));
		}
		return filter;
	}
}

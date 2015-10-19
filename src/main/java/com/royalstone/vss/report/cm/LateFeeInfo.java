package com.royalstone.vss.report.cm;
/**
 * 滞纳金
 * **/
import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class LateFeeInfo extends CMQueryService {

	public LateFeeInfo(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.sheetid = " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("sheettype");
		if (ss != null && ss.length > 0) {
			filter.add(" a.sheettype = " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.shopid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("payshopid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.payshopid IN (" + new Values(ss).toString4String() + ") ");
		}
		ss = (String[]) map.get("flag");
		if (ss != null && ss.length > 0) {
			filter.add(" a.flag IN (" + new Values(ss).toString4String() + ") ");
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

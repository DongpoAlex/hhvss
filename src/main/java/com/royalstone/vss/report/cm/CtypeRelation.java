package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;

public class CtypeRelation extends CMQueryService {

	public CtypeRelation(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("barcode");
		if (ss != null && ss.length > 0) {
			filter.add(" barcodeid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("goodsid");
		if (ss != null && ss.length > 0) {
			filter.add(" goodsid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("status");
		if (ss != null && ss.length > 0) {
			filter.add(" status IN (" + new Values(ss).toString4String() + ") ");
		}
		
		return filter;
	}
}

package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class CerReceipt extends CMQueryService {

	public CerReceipt(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" cat.venderid = " + Values.toString4String(ss[0]));
		}

		// 验收单号
		ss = (String[]) map.get("refsheetid");
		if (ss != null && ss.length > 0) {
			filter.add(" cat.sheetid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("barcode");
		if (ss != null && ss.length > 0) {
			filter.add(" c.barcodeid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("goodsid");
		if (ss != null && ss.length > 0) {
			filter.add(" c.goodsid IN (" + new Values(ss).toString4String() + ") ");
		}

		ss = (String[]) map.get("sdate_min");
		if (ss != null && ss.length > 0) {
			filter.add(" trunc(a.checkdate) >= " + ValueAdapter.std2mdy(ss[0]));
		}
		ss = (String[]) map.get("sdate_max");
		if (ss != null && ss.length > 0) {
			filter.add(" trunc(a.checkdate) <= " + ValueAdapter.std2mdy(ss[0]));
		}
		return filter;
	}
}

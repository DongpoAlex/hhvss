package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;

public class Goods extends CMQueryService {

	public Goods(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("mingoodsid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.goodsid >= " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("maxgoodsid");
		if (ss != null && ss.length > 0) {
			filter.add(" a.goodsid <= " + Values.toString4String(ss[0]));
		}
		
		ss = (String[]) map.get("goodsname");
		if (ss != null && ss.length > 0) {
			filter.add(" a.goodsname like " + Values.toString4Like(ss[0]));
		}

		return filter;
	}
}

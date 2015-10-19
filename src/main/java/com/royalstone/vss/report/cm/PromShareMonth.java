package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class PromShareMonth extends CMQueryService {

	public PromShareMonth(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_vender = new Values(ss);
			filter.add("p.venderid IN (" + val_vender.toString4String() + ") ");
		}

		ss = (String[]) map.get("month");
		if (ss != null && ss.length > 0) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat oSdf = new SimpleDateFormat("", Locale.ENGLISH);
			oSdf.applyPattern("yyyy-MM");
			try {
				cal.setTime(oSdf.parse(ss[0]));
			}
			catch (ParseException e) {
				throw new InvalidDataException(e);
			}
			int num2 = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			filter.add(" (p.sdate) >= " + ValueAdapter.std2mdy(ss[0]+"-01"));
			filter.add(" (p.sdate) <= " + ValueAdapter.std2mdy(ss[0]+"-"+num2));
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_shopid = new Values(ss);
			filter.add("p.shopid IN (" + val_shopid.toString4String() + ") ");
		}
		return filter;
	}
}

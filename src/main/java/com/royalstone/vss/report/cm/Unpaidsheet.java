package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class Unpaidsheet extends CMQueryService {

	public Unpaidsheet(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0) {
			filter.add(" u.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			filter.add(" u.shopid IN (" + new Values(ss).toString4String() + ") ");
		}

		
		
		ss = (String[]) map.get("sheettype");
		if (ss != null && ss.length > 0) {
			filter.add(" u.sheettype = " + Values.toString4String(ss[0]));
		}

		

		if(token.site.getSid()==11){
        	ss = (String [] ) map.get( "payshopid" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " u.payshopid IN ("+new Values(ss).toString4String()+") ");
            }
            ss = (String [] ) map.get( "payflag" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " u.payflag IN ("+new Values(ss).toString4String()+") ");
            }
        }else{
        	ss = (String[]) map.get("bookno");
    		if (ss != null && ss.length > 0) {
    			filter.add(" u.bookno = " + Values.toString4String(ss[0]));
    		}
        }
		
		ss = (String[]) map.get("docdate_min");
		if (ss != null && ss.length > 0) {
			filter.add(" u.docdate >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("docdate_max");
		if (ss != null && ss.length > 0) {
			filter.add(" u.docdate <= " + ValueAdapter.std2mdy(ss[0]));
		}
		
		return filter;
	}
}

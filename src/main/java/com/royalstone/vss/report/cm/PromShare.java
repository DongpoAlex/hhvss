package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class PromShare extends CMQueryService {

	public PromShare(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss  = (String [] ) map.get( "venderid" );
		if( ss != null && ss.length >0 && ss[0] != null && ss[0].length()>0 ) {
			Values val_vender = new Values ( ss );
			filter.add(  "p.venderid IN (" + val_vender.toString4String() + ") " );
		}
		
		ss  = ( String [] ) map.get( "sdate_min" );
		if( ss != null && ss.length >0 ) {
			filter.add( " (p.sdate) >= " + ValueAdapter.std2mdy(ss[0]) );
		}
		
		ss = ( String [] ) map.get( "sdate_max" );
		if( ss != null && ss.length >0 ) {
			filter.add( " (p.sdate) <= " + ValueAdapter.std2mdy(ss[0]) );
		}
		
		ss  = (String [] ) map.get( "shopid" );
		if( ss != null && ss.length >0 ) {
			Values val_shopid = new Values ( ss );
			filter.add(  "p.shopid IN (" + val_shopid.toString4String() + ") " );
		}
		return filter;
	}
}

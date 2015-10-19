package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;

public class GoodsShop extends CMQueryService {

	public GoodsShop(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

        ss = (String [] ) map.get( "venderid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " v.venderid = "+Values.toString4String(ss[0]));
        }
        
        ss = (String [] ) map.get( "goodsid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " v.goodsid IN ("+new Values(ss).toString4String()+") ");
        }
        
        ss = (String [] ) map.get( "shopid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " v.shopid IN ("+new Values(ss).toString4String()+ ") ");
        }
        
        ss = (String [] ) map.get( "goodsstatus" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " v.goodsstatus IN ("+new Values(ss)+ ") ");
        }
        
        //TODO 只显示默认供应商
        filter.add("v.venderid=v.venderid_first");
        
        return filter;
	}

}

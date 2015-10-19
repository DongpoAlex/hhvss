package com.royalstone.vss.report.cm;

import java.sql.Connection;
import java.util.Map;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMQueryService;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

public class UnPayChargeSum extends CMQueryService {

	public UnPayChargeSum(Connection conn, Token token) {
		super(conn, token);
	}

	public Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

        ss = (String [] ) map.get( "venderid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " a.venderid = "+Values.toString4String(ss[0]));
        }
        
        
        
        ss = (String [] ) map.get( "shopid" );
        if( ss != null && ss.length >0 ) {
            filter.add(  " a.shopid IN ("+new Values(ss).toString4String()+ ") ");
        }
        
        
        if(token.site.getSid()==11){
        	ss = (String [] ) map.get( "payshopid" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " a.payshopid IN ("+new Values(ss).toString4String()+") ");
            }
            ss = (String [] ) map.get( "flag" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " a.flag IN ("+new Values(ss).toString4String()+") ");
            }
            ss = (String [] ) map.get( "sdate_min" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " trunc(a.duedate) >= "+ValueAdapter.std2mdy( ss[0] ));
            }
            
            ss = (String [] ) map.get( "sdate_max" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " trunc(a.duedate) <= "+ValueAdapter.std2mdy( ss[0] ));
            }
        }else{
        	ss = (String [] ) map.get( "sdate_min" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " trunc(a.ReckoningDate) >= "+ValueAdapter.std2mdy( ss[0] ));
            }
            
            ss = (String [] ) map.get( "sdate_max" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " trunc(a.ReckoningDate) <= "+ValueAdapter.std2mdy( ss[0] ));
            }
            
            ss = (String [] ) map.get( "bookno" );
            if( ss != null && ss.length >0 ) {
                filter.add(  " a.bookno IN ("+new Values(ss).toString4String()+") ");
            }
            
        	filter.add(" a.flag=1 ");
        }
        
		return filter;
	}
}

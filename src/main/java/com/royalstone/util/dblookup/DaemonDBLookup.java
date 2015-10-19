package com.royalstone.util.dblookup;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.myshop.component.DBLookup;
import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;

/**
 * 返回分组门店
 * @author baijian
 */
public class DaemonDBLookup extends XDaemon
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2672993607412904925L;

	public void doPost( HttpServletRequest request, HttpServletResponse response )throws ServletException, IOException
	{
		doGet( request, response );
	}
	
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		this.check4Gzip( request );
		Connection conn = null;
		Element elm = new Element("xdoc");
		try {
			if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
            conn = openDataSource( token.site.getDbSrcName() );
            DBLookup lookup = new DBLookup(conn);
            
            String type = request.getParameter("type");
            Map map = request.getParameterMap();
            Filter filter = new Filter();
            if("oicharge".equals(type)){
        		String[] ss = null;
        		ss = (String[]) map.get("shopform");
        		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
        			Values v = new Values(ss);
        			filter.add("shopform IN (" + v.toString4String() + ") ");
        		}
        		
        		ss = (String[]) map.get("name");
        		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
        			lookup.setName(ss[0]);
        		}
        		
        		lookup.setFilter(filter.toWhereString());
        		lookup.setSql(" select chargecodeid,chargename from oicharge ");
        		
        		String showAll = request.getParameter("showAll");
                if("true".equalsIgnoreCase(showAll)){
                	lookup.setShowAll(true);
                }
                
            	elm = lookup.makeLookup();
            }
            
        } catch (Exception e) {
        	elm.addContent(new XErr( -1, e.getMessage() ).toElement()) ; 
        	e.printStackTrace();
        } finally {
	    	output( response, elm );
	    	closeDataSource(conn);
        }
	}
}

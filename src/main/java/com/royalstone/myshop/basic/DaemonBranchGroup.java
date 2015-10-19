package com.royalstone.myshop.basic;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.myshop.component.SelBranchGroup;
import com.royalstone.myshop.component.SelBranchGroupThridDC;
import com.royalstone.myshop.component.SelBranchGroupUnclose;
import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;

/**
 * 返回分组门店
 * @author baijian
 */
public class DaemonBranchGroup extends XDaemon
{
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
            
            String rander = request.getParameter("rander");
            String single = request.getParameter("single");
            String thridDC = request.getParameter("thridDC");
            int[] shoptype = { 11,22 };
        	String note = "任意门店";
        	
        	XComponent sel_branch = null;
        	
        	//门店状态，0=正常开店 -1代表已关店
        	String unclose= request.getParameter("unclose");
        	if(unclose!=null && unclose.equalsIgnoreCase("true")){
        		sel_branch = new SelBranchGroupUnclose(token, shoptype, note );
        	}else{
        		sel_branch = new SelBranchGroup(token, shoptype, note );
        	}
        	if(thridDC!=null && thridDC.equalsIgnoreCase("true")){
        		sel_branch = new SelBranchGroupThridDC(token,note);
        	}
        	
        	sel_branch.setAttribute( "id", rander+"_groupid" );
        	if(single==null || !single.equalsIgnoreCase("true")){
        		sel_branch.setAttribute( "multiple", "multiple" );
        	}
        	sel_branch.setAttribute( "onchange", "onSelBranchGroupChange('"+rander+"')" );
        	sel_branch.setAttribute( "ondblclick", "onSelBranchGroupDBClick('"+rander+"')" );
        	elm = sel_branch.toElement();
        } catch (Exception e) {
        	elm.addContent(new XErr( -1, e.getMessage() ).toElement()) ; 
        	e.printStackTrace();
        } finally {
	    	output( response, elm );
	    	closeDataSource(conn);
        }
	}
}

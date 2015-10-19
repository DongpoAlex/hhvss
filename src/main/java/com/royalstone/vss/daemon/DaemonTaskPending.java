package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.TokenException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.Site;
import com.royalstone.vss.gate.TaskPending;

/**
 * 待处理任务清单
 * @author mly
 *
 */
public class DaemonTaskPending  extends XDaemon {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
		request.setCharacterEncoding( "UTF-8" );
		Connection conn = null;
	    Element elm_doc = new Element( "xdoc" );
    	Element elm_out = new Element( "xout" );

        try {
	       	if (!isSessionActive(request))
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	        Token token = this.getToken(request);
	        if (token == null)
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	       
			conn = openDataSource( token.site.getDbSrcName() );
			
			//任务清单需要多行统计，设为脏读提高效率。
			this.setDirtyRead(conn);
			
			if( ! this.isSessionActive( request ) ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	        
	        /**
	         * 供应商只可以查看自己的单据.
	         * 如果访问者以供应商身份登录, 应添加针对供应商ID的过滤条件.
	         */
	    	if( ! token.isVender) {
	    		throw new TokenException( "供应商专业模块！" );
	    	}
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
        	
        	TaskPending t = new TaskPending( conn, arr_vender,token );
        	Element elm_cat = null;
        	String operation = request.getParameter("operation");
        	//前台先检查是否有预警单据
        	if( operation != null && operation.equals("hasRetWarning")){
        		int awoke = t.getWarningRetCount(90);
        		int warning = t.getWarningRetCount(91);
        		elm_cat = new Element("retwarning");
        		elm_cat.setAttribute("awoke", String.valueOf(awoke));
        		elm_cat.setAttribute("warning", String.valueOf(warning));
        		elm_cat.setAttribute("siteName", token.site.getSiteName());
        	}else{
        		elm_cat = t.toElement();
        	}
        	elm_out.addContent( elm_cat );
        	elm_doc.addContent( elm_out );
			elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
		} catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
		} catch (Exception e) {
			//e.printStackTrace();
        	elm_doc.addContent( new XErr( -1, e.toString() ).toElement() ) ; 
		} finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}

	private static final long serialVersionUID = 20061031L;
}

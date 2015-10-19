/*
 * Created on 2005-08-20
 *
 */
package com.royalstone.vss.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;


/**
 * 此模块负责查询模块信息
 */
public class DaemonModuleList extends XDaemon
{

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
	    doGet(request, response);
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	this.check4Gzip(request);
	    Element elm_doc = new Element( "xdoc" );
    	Connection conn = null;
	    try {
	    	if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
            conn = openDataSource( token.site.getDbSrcName() );
            this.setDirtyRead(conn);
            
            String action = request.getParameter( "action" );
//            String str_moduleid = request.getParameter( "moduleid" );
            if( action == null || action.length() == 0 ) throw new InvalidDataException( "action not set! " );
           
            ModuleAdm module = new ModuleAdm( conn );
            
//            if( action != null && action.equalsIgnoreCase( "list_oneModule" )) module.getOneModule(str_moduleid);
            
            if( action != null && action.equalsIgnoreCase( "list_allModule" )) {
        	    Element elm_list =  module.listAll();
//                elm_list = module.toElement();
                if( elm_list != null ) elm_doc.addContent( new Element( "xout" ).addContent(elm_list) );      
            }else if( action != null && action.equalsIgnoreCase( "list_roleTypeModule" )) {
            	int headMenu = Integer.valueOf(request.getParameter( "headMenu" ));
            	
            	  Element elm_list =  module.listRoleTypeModule(headMenu);
            	  elm_doc.addContent( new Element( "xout" ).addContent(elm_list) );
            }
            
            elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
		} catch (SQLException e) {
			elm_doc.addContent( new XErr( e.getErrorCode(), e.getMessage() ).toElement() ); 
        } catch (Exception e) {
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
        } finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}

    private static final long serialVersionUID = 20060909L;
}


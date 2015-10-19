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

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;


/**
 * @author Mengluoyi
 *
 */
public class DaemonAuthority extends XDaemon
{

    public void doPost(HttpServletRequest request, HttpServletResponse response)
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
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED);
			AuthorityAdm adm = new AuthorityAdm(conn);
            
            String action = request.getParameter( "action" );
            String moduleid = request.getParameter( "moduleid" );
            String roleid = request.getParameter( "roleid" );
            
            if( action != null && action.equalsIgnoreCase( "list4role" )){
            	if( roleid == null || roleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid roleid: " + roleid );
    			Authority[] list = adm.getPermission4Role( Integer.parseInt(roleid) );
            	elm_doc.addContent( Authority.toElement(list) );
            }

            if( action != null && action.equalsIgnoreCase( "list4module" )){
            	if( moduleid == null || moduleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid moduleid: " + moduleid );
    			Authority[] list = adm.getPermission4Module( Integer.parseInt(moduleid) );
    			System.out.println(512&512);
            	elm_doc.addContent( Authority.toElement(list) );
            }

            if( action != null && action.equalsIgnoreCase( "save4role" )){
            	if( roleid == null || roleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid roleid: " + roleid );
            	int id = Integer.parseInt(roleid);
            	
            	Document doc_au = this.getParamDoc( request );
            	
            	Element elm_au  = doc_au.getRootElement();
            	Authority[] arr_au = Authority.parseList( elm_au );
            	adm.save4Role( id, arr_au );

            	Authority[] list = adm.getPermission4Role( id );
            	elm_doc.addContent( Authority.toElement(list) );
            }

            if( action != null && action.equalsIgnoreCase( "save4module" )){
            	if( moduleid == null || moduleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid moduleid: " + moduleid );
            	int id = Integer.parseInt(moduleid);
            	
            	Document doc_au = this.getParamDoc( request );
            	
            	Element elm_au  = doc_au.getRootElement();
            	Authority[] arr_au = Authority.parseList( elm_au );
            	adm.save4Module( id, arr_au );
            	
    			Authority[] list = adm.getPermission4Module( id );
            	elm_doc.addContent( Authority.toElement(list) );
            }

            elm_doc.addContent( new XErr( 0, "OK" ).toElement() );

		} catch (SQLException e) {
			elm_doc.addContent( new XErr( e.getErrorCode(), e.getMessage() ).toElement() ); 
        } catch (Exception e) {
        	e.printStackTrace();
        	elm_doc.addContent(new XErr( -1, e.getMessage() ).toElement()) ; 
        } finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	doPost( request, response );
	}

    private static final long serialVersionUID = 20060909L;
}


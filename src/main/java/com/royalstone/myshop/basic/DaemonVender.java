/*
 * Created on 2005-08-20
 *
 */
package com.royalstone.myshop.basic;

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
 * @author Windfly
 *
 */
public class DaemonVender extends XDaemon
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
            
            String venderid = request.getParameter( "venderid" );
            if( venderid == null || venderid.length() == 0 ) throw new InvalidDataException( "venderid not set! " );
            
            Look4Vender vender_info = new Look4Vender( conn, venderid );
            Element elm = vender_info.toElement();
            
	    	elm_doc.addContent( new Element( "xout" ).addContent(elm) );
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

    private static final long serialVersionUID = 20060719L;
}




/*
 * Created on 2007-02-02
 *
 */
package com.royalstone.vss.daemon;

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
import com.royalstone.vss.detail.ShowStkCostAdj;

public class DaemonStkCostAdjItem extends XDaemon {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
	    doGet(request, response);
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	request.setCharacterEncoding( "UTF-8" );
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
            
            String sheet   = request.getParameter( "sheet" );
            String sheetid = request.getParameter( "sheetid" );
            if( sheet == null   || sheet.length() == 0   ) throw new InvalidDataException( "sheet not set! " );
            if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid not set! " );

            Element elm_out = new Element( "xout" );
            
            /**
             * 库存进价调整单
             */
            if( sheet.equalsIgnoreCase( "stkcostadj" ) ) {
            	ShowStkCostAdj show = new ShowStkCostAdj( conn, sheetid );
                Element elm_sheet = show.toElement();
                elm_out.addContent( elm_sheet );
            }
            elm_doc.addContent( elm_out );
			elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
			
		} catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
        } catch (Exception e) {
        	//e.printStackTrace();
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
        } finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}
    
    private static final long serialVersionUID = 20070201L;
}

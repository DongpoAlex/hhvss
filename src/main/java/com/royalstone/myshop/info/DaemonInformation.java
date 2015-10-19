/*
 * Created on 2006-08-18
 *
 */
package com.royalstone.myshop.info;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

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
 * 此模块用于查询基本资料. 包括: 帐套, 门店, 品类, 商品, 供应商.
 * @author mengluoyi
 * @param focus shop, major, paytype, goods, dept, vender.
 *
 */
public class DaemonInformation extends XDaemon
{

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
	    Element elm_out = new Element( "xout" );
    	
    	Connection conn = null;
	    try {
	    	if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
            conn = openDataSource( token.site.getDbSrcName() );
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED );
            
            String focus = request.getParameter( "focus" );
            if( focus == null || focus.length() == 0 ) throw new InvalidDataException( "focus not set! " );
            
            HashMap parms = new HashMap(request.getParameterMap());
            
            if ( focus.equalsIgnoreCase( "shop" ) ) {
            	InfoShop info = new InfoShop( conn, parms );
            	elm_out.addContent( info.toElement() );
            }
            else if ( focus.equalsIgnoreCase( "major" ) ) {
            	InfoMajor info = new InfoMajor( conn, parms );
            	elm_out.addContent( info.toElement() );
            }
            else if ( focus.equalsIgnoreCase( "paytype" ) ) {
            	InfoPaytype info = new InfoPaytype( conn, parms );
            	elm_out.addContent( info.toElement() );
            } 
            else if ( focus.equalsIgnoreCase( "vender" ) ) {
            	InfoVender info = new InfoVender( conn, parms );
            	elm_out.addContent( info.toElement() );
            }
            else if ( focus.equalsIgnoreCase( "goods" ) ) {
            	InfoGoods info = new InfoGoods( conn, parms );
            	elm_out.addContent( info.toElement() );
            }
            else if ( focus.equalsIgnoreCase( "dept" ) ) {
            	InfoDept info = new InfoDept( conn, parms );
            	elm_out.addContent( info.toElement() );
            }
            
			elm_doc.addContent( elm_out );
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

    private static final long serialVersionUID = 20060818L;
}



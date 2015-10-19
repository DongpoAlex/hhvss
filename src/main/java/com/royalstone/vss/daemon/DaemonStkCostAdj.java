package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.vss.catalogue.SearchStkCostAdj;

public class DaemonStkCostAdj extends XDaemon {
	
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
            
            String sheetname = request.getParameter( "sheetname" );
            
            
            if( sheetname == null || sheetname.length() == 0 ) throw new InvalidDataException( "sheetname not set! " );

            Map map = request.getParameterMap();
            
            HashMap parms = new HashMap(map);
           
            /**
             * 供应商只可以查看自己的单据.
             * 如果访问者以供应商身份登录, 应添加针对供应商ID的过滤条件.
             */
            if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
        	}
            
            	
            /**
             * 查询库存进价调整单
             */
            if( sheetname.equalsIgnoreCase( "stkcostadj" ) ){
            	SearchStkCostAdj search = new SearchStkCostAdj( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            /*
             * 参数不存在
             */
            else
            	throw new InvalidDataException( "Undefined sheetname: " + sheetname );
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
    
    private static final long serialVersionUID = 20070201L;

}

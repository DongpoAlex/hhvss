package com.royalstone.vss.daemon;

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
 * 此模块用于农残检测
 * @author baijian
 */
public class DaemonDetection extends XDaemon
{
    
	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
		doPost(request, response);
	}

    public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	this.check4Gzip(request);
	    Element elm_doc = new Element( "xdoc" );
    	Element elm_out = new Element( "xout" );
    	request.setCharacterEncoding( "UTF-8" );
    	Connection conn = null;
	    try {
	    	//登陆检测
	    	if( ! isSessionActive( request ) ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
            Token token = this.getToken( request );
	    	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
                      
            String section = request.getParameter("section");
            if( section == null || section.length()<1 ) throw new InvalidDataException(" reportname is null ");
           
            conn = openDataSource( token.site.getDbSrcName() );
            
            //插入venderid以过滤查询条件，使得供应商查询时只能查询属于自己的数据
            HashMap parms = new HashMap(request.getParameterMap());
            
        	if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
        	}
        	
        	if( section.equals("postdate") ){
        		
        		
        	}else if( section.equals("sdaily") ){
        	}else if( section.equals("gshop")){
        	}else if(section.equals("checkwork")){
        	}else{
        		
        	}
        	
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
    
    private static final long serialVersionUID = 20090720L;
}

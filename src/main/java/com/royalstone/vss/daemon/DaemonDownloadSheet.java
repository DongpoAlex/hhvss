/*
 * Created on 2006-07-20
 *
 */

package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;

import com.royalstone.security.Token;
import com.royalstone.util.Day;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.detail.DownloadPurchase;

/**
 * 此模块用于下载订单.
 * @param	sheetname	单据名称
 * @param	releasedate_min	上传日期-起始日
 * @param	releasedate_max	上传日期-截止日
 * @author mengluoyi
 */
public class DaemonDownloadSheet extends XDaemon
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
    	request.setCharacterEncoding( "UTF-8" );
	    Element elm_doc = new Element( "xdoc" );
    	
    	String path_base = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    	String xsl_order = path_base + "/css/format4.xsl";

    	ProcessingInstruction pi = new ProcessingInstruction( "xml-stylesheet", "" );
    	pi  = pi.setValue( "type", "text/xsl" );
    	pi  = pi.setValue( "href", xsl_order );
	    Connection conn = null;
	    try {
	       	if (!isSessionActive(request))
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	        Token token = this.getToken(request);
	        if (token == null)
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	       
            conn = openDataSource( token.site.getDbSrcName());
            
            String venderid = null;
            String sheetname   = request.getParameter( "sheetname" );
            String releasedate_min = request.getParameter( "releasedate_min" );
            String releasedate_max = request.getParameter( "releasedate_max" );
            if( sheetname == null   || sheetname.length() == 0   ) throw new InvalidDataException( "sheetname not set! " );
            if( releasedate_min == null || releasedate_min.length() == 0 ) throw new InvalidDataException( "releasedate_min not set! " );
            if( releasedate_max == null || releasedate_max.length() == 0 ) throw new InvalidDataException( "releasedate_max not set! " );


            Day day_min = ValueAdapter.std2Day( releasedate_min );
            Day day_max = ValueAdapter.std2Day( releasedate_max );
            int days = day_max.daysBetween( day_min );
            
            if( days <0 )  throw new InvalidDataException( "截止日期必须大于起始日期!" );
            if( days >10 ) throw new InvalidDataException( "日期范围不得超过10天!" );

            /**
             * 如果访问者以供应商身份登录, 应添加过滤条件.
             */
            if( token.isVender ) {
           		venderid = token.getBusinessid();
        	}
            else throw new PermissionException( "此模块为供应商专用!" );
            
            Element elm_out = new Element( "xout" );
            if( sheetname.equalsIgnoreCase( "purchase" ) ) 
            	for( Day day = new Day( day_min ); day.daysBetween(day_max)<=0; day.advance(1) ){
        			DownloadPurchase loader = new DownloadPurchase( conn, venderid, day );
        			elm_out.addContent( loader.toElement() );
            } 
    		
			elm_doc.addContent( elm_out );
			elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            String filename = venderid + "#" + sheetname + "#" + releasedate_min + "#" + releasedate_max + ".xml";

            Document doc = new Document( elm_doc );
            doc.getContent().add(0, pi);

            export( response, doc, filename );
			
		} catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
	    	output( response, elm_doc );
        } catch (Exception e) {	
        	//e.printStackTrace();
        	elm_doc.addContent(new XErr( 0, e.toString() ).toElement()) ; 
	    	output( response, elm_doc );
        } finally {
	    	closeDataSource(conn);
        }
	}
    
    private static final long serialVersionUID = 20060813L;
}


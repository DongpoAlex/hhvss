package com.royalstone.email;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
 * 此模块用于下载邮件信息
 * @author bai
 */
public class DaemonDownloadMail extends XDaemon
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
	    File fdown = File.createTempFile( "rpt", "xls" );
	    Connection conn = null;
	    try {
	    	if( ! isSessionActive( request ) ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
            Token token = this.getToken( request );
	    	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );

            String operation = request.getParameter("operation");
            if( operation == null || operation.length()<1 ) throw new InvalidDataException(" operation is null ");
           
            conn = openDataSource( token.site.getDbSrcName() );
            
            
            MailManager mm = new MailManager(conn,token);

            Map map = request.getParameterMap();
            HashMap parms = new HashMap(map);
            
			/**
			 * 统计所有人员邮件收发总数
			 */
			if( operation.equals("statisticBack") ){
//				Workbook book = mm.statisticMailBack(parms);
//				book.toFile(fdown);
//				
				mm.statisticMailBack(parms, fdown);
				outputFile( response, fdown , "mailBack.xls" );
				
				
			/**
			 * 得到邮件收发明细
			 */
			}else if( operation.equals("backDetail") ){
				
//				Workbook book = mm.getMailBackDetailBook(parms);
//				book.toFile(fdown);
//				
				mm.getMailBackDetailBook(parms,fdown);
				outputFile(response, fdown, "backDetail.xls");
				
			/**
			 * 得到邮件发送数目按月统计
			 */	
			}else if( operation.equals("sendCount") ){
				
//				Workbook book = mm.getMailSendCount(parms);
//				book.toFile(fdown);
//				
				mm.getMailSendCount(parms,fdown);
				outputFile(response, fdown, "sendCount.xls");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
	    	output( response, elm_doc );
        } catch (Exception e) {	
        	e.printStackTrace();
        	elm_doc.addContent(new XErr( 0, e.toString() ).toElement()) ; 
	    	output( response, elm_doc );
        } finally {
	    	closeDataSource(conn);
	    	fdown.delete();
        }
	}
    
    private static final long serialVersionUID = 20060813L;
}


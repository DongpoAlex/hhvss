/*
 * Created on 2004-12-21
 *
 */
package com.royalstone.file;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;

/**
 * 文件下载,包括普通的公文下载和邮件中的附件下载,以二进制文件流的形式返回到前台
 * @author liuyk
 * 
 */
public class DaemonFileDownload extends XDaemon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 20061031L;

	public void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		    doGet( request, response);
		}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
    	Connection conn = null;
		try {
			/**
			 * 如未登录拒绝访问
			 */
			if (!this.isSessionActive(request)) {
				throw new PermissionException(PermissionException.LOGIN_PROMPT);
			}
			Token token = this.getToken(request);
			if (token == null)
				throw new PermissionException(PermissionException.LOGIN_PROMPT);

			String strFileid = request.getParameter( "fileid" );
			int id4file = Integer.parseInt(strFileid);
            OutputStream ostream = response.getOutputStream();
            conn = openDataSource( token.site.getDbSrcName() );
            this.setDirtyRead(conn);

        	String filename = FileManager.getFileName( conn, id4file );
//        	System.out.println(filename);
        	//TODO 这里的文件名可能有乱码问题
        	filename = URLEncoder.encode( filename, "UTF-8" );
            response.setHeader( "Content-disposition", "attachment; filename=" + filename );
        	FileManager.outputFileBody( conn, id4file, ostream );
        	ostream.close();

		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (PermissionException e) {
			e.printStackTrace();
		}finally {
	    	closeDataSource(conn);
	    }
	}
}

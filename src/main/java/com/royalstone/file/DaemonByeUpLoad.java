/*
 * Created on 2004-11-25
 *
 */
package com.royalstone.file;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.sql.SqlUtil;

/**
 * 附件上传，返回是否成功到前台 利用包：org.apache.commons.fileupload来处理从客户端发过来的文件流
 * 
 * @author yzn
 * 
 */
public class DaemonByeUpLoad extends XDaemon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 20061030L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		int fileid = 0;
		String filename = "";
		InputStream in = null; // 二进制文件流
		Connection conn = null;
		String info = "";
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

			conn = openDataSource(token.site.getDbSrcName());
			DiskFileUpload fu = new DiskFileUpload();
			/**
			 * FileUploadBase类里面有setHeaderEncoding方法
			 * 读取上传表单的各部分时会用到该encoding，如果没有指定encoding则使用系统缺省的encoding。
			 */
			fu.setHeaderEncoding("UTF-8");
			// 允许上传文件最大值为10M
			fu.setSizeMax(10485760);
			List fileItems = fu.parseRequest(request);
			Iterator i = fileItems.iterator();
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				filename = fi.getName();
				if (filename != null && filename.length() > 0) {
					int p = filename.lastIndexOf('\\');
					if (p >= 0)
						filename = filename.substring(p + 1);
					in = fi.getInputStream();
				}
			}
			if (filename != null && filename.length() > 0) {
				fileid = FileManager.inputFileInfo(conn, SqlUtil.toLocal(filename));
				FileManager.inputFileBody(conn, fileid, in);
			}
			info = filename;

		} catch (SQLException e) {
			info = "上传文件失败：" + e.toString();
			e.printStackTrace();
		} catch (FileUploadException e) {
			info = "上传文件失败：" + e.toString();
			e.printStackTrace();
		} catch (NamingException e) {
			info = "上传文件失败：" + e.toString();
			e.printStackTrace();
		} catch (PermissionException e) {
			info = "上传文件失败：" + e.toString();
			e.printStackTrace();
		} finally {
			info = SqlUtil.fromLocal(info);
			response.sendRedirect("mail/upload.jsp?fileid=" + fileid+ "&info=" + info);
			closeDataSource(conn);
		}
	}
}

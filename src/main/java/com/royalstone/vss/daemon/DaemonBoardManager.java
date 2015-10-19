/*
 * Created on 2006-10-25
 */
package com.royalstone.vss.daemon;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Permission;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.noteboard.BoardManager;

/**
 * 此模块用于实现功能: 公文附件下载.
 * 
 * @author mengluoyi
 */
public class DaemonBoardManager extends XDaemon {
	private static final long	serialVersionUID	= 20061025L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		Connection conn = null;
		String operation = request.getParameter("operation");
		
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");
		String info = "";

		if (operation == null || operation.length() == 0) {
			output(response, "operation is invalid!");
			return;
		}

		/**
		 * 下载公文内容(附件)
		 */
		if (operation.equalsIgnoreCase("download")) {
			try {
				/**
				 * 如未登录拒绝访问
				 */
				if (!this.isSessionActive(request)) {
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				}
				Token token = this.getToken(request);

				String str_fileid = request.getParameter("fileid");
				if (str_fileid == null || str_fileid.length() == 0)
					throw new InvalidDataException("fileid is invalid");

				int fileid = Integer.parseInt(str_fileid);
				conn = openDataSource(token.site.getDbSrcName());

				String filename = BoardManager.getFileName(conn, fileid);
				filename = URLEncoder.encode(filename, "UTF-8");
				response.setHeader("Content-disposition", "attachment; filename=" + filename);

				OutputStream ostream = response.getOutputStream();
				BoardManager manager = new BoardManager(token);
				manager.outputFileBody(conn, fileid, ostream);
				ostream.close();
			} catch (SQLException e) {
				// e.printStackTrace();
				info = "下载文件失败：" + e.toString();
				output(response, info);
			} catch (Exception e) {
				// e.printStackTrace();
				info = "下载文件失败：" + e.toString();
				output(response, info);
			} finally {
				closeDataSource(conn);
			}
		}
		/**
		 * 取出管理员当前有效公文的目录
		 */
		else if (operation.equalsIgnoreCase("catalogue")) {
			try {
				if (!isSessionActive(request))
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				Token token = this.getToken(request);
				if (token == null)
					throw new PermissionException(PermissionException.LOGIN_PROMPT);

				conn = openDataSource(token.site.getDbSrcName());

				Element elm_cat = BoardManager.getNoteCatalogue(conn, request.getParameterMap());

				elm_out.addContent(elm_cat);
				elm_doc.addContent(elm_out);

				Element elm_err = (new XErr(0, "OK")).toElement();
				elm_doc.addContent(elm_err);
			} catch (SQLException e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			} catch (Exception e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			} finally {
				output(response, elm_doc);
				closeDataSource(conn);
			}
		}
		/**
		 * 取出当前供应商有效公文的目录
		 */
		else if (operation.equalsIgnoreCase("catalogue_vender")) {
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
				// conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				Element elm_cat = BoardManager.getVenderNoteCatalogue(conn, token,request.getParameterMap());
				elm_out.addContent(elm_cat);
				elm_doc.addContent(elm_out);

				Element elm_err = (new XErr(0, "OK")).toElement();
				elm_doc.addContent(elm_err);
			} catch (SQLException e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			} catch (Exception e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			} finally {
				output(response, elm_doc);
				closeDataSource(conn);
			}
		}
		/**
		 * 查看指定公文号的明细(标题及正文)
		 */
		else if (operation.equalsIgnoreCase("view")) {
			try {
				if (!isSessionActive(request))
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				Token token = this.getToken(request);
				if (token == null)
					throw new PermissionException(PermissionException.LOGIN_PROMPT);

				String noteid_str = request.getParameter("noteid");
				if (noteid_str == null || noteid_str.length() == 0)
					throw new InvalidDataException("fileid is invalid");

				int noteid = Integer.parseInt(noteid_str);
				conn = openDataSource(token.site.getDbSrcName());
				// conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				Element elm_cat = BoardManager.fetchNoteDetail(conn, token, noteid);
				elm_out.addContent(elm_cat);
				elm_doc.addContent(elm_out);
				Element elm_err = (new XErr(0, "OK")).toElement();
				elm_doc.addContent(elm_err);
			} catch (SQLException e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			} catch (Exception e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			} finally {
				output(response, elm_doc);
				closeDataSource(conn);
			}
		}

		/**
		 * 删除指定公文号的明细(标题及正文)
		 */
		else if (operation.equalsIgnoreCase("delete")) {
			try {
				if (!isSessionActive(request))
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				Token token = this.getToken(request);
				if (token == null)
					throw new PermissionException(PermissionException.LOGIN_PROMPT);

				// 查询用户的权限.
				int moduleid = Integer.parseInt(request.getParameter("moduleid"));
				Permission perm = token.getPermission(moduleid);
				if (!perm.include(Permission.DELETE))
					throw new PermissionException("删除未授权,请管理员联系. 模块号:" + moduleid);

				String noteid_str = request.getParameter("noteid");
				if (noteid_str == null || noteid_str.length() == 0)
					throw new InvalidDataException("fileid is invalid");

				int noteid = Integer.parseInt(noteid_str);
				conn = openDataSource(token.site.getDbSrcName());
				BoardManager.deletenote(conn, noteid);
				Element elm_err = (new XErr(0, "OK")).toElement();
				elm_doc.addContent(elm_err);
			} catch (SQLException e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			} catch (Exception e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			} finally {
				output(response, elm_doc);
				closeDataSource(conn);
			}
		}

		/**
		 * 查看已阅览指定公文号的供应商清单
		 */
		else if (operation.equalsIgnoreCase("vender_list")) {
			try {
				if (!isSessionActive(request))
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				Token token = this.getToken(request);
				if (token == null)
					throw new PermissionException(PermissionException.LOGIN_PROMPT);

				String noteid_str = request.getParameter("noteid");
				if (noteid_str == null || noteid_str.length() == 0)
					throw new InvalidDataException("noteid is invalid");

				int noteid = Integer.parseInt(noteid_str);
				conn = openDataSource(token.site.getDbSrcName());
				// conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				Element elm_cat = BoardManager.getVisitLog(conn, noteid);
				elm_out.addContent(elm_cat);
				elm_doc.addContent(elm_out);
				Element elm_err = (new XErr(0, "OK")).toElement();
				elm_doc.addContent(elm_err);
			} catch (SQLException e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			} catch (Exception e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			} finally {
				output(response, elm_doc);
				closeDataSource(conn);
			}

		}
		/**
		 * 下载已阅览指定公文号的供应商清单
		 */
		else if (operation.equalsIgnoreCase("down")) {
			File fdown = File.createTempFile("rpt", "xls");
			try {
				if (!isSessionActive(request))
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				Token token = this.getToken(request);
				if (token == null)
					throw new PermissionException(PermissionException.LOGIN_PROMPT);				
				String noteid_str = request.getParameter("noteid");
				if (noteid_str == null || noteid_str.length() == 0)
					throw new InvalidDataException("noteid is invalid");

				int noteid = Integer.parseInt(noteid_str);
				conn = openDataSource(token.site.getDbSrcName());

				String fileName = "downlist_" + noteid + ".xls";
				BoardManager.cookExcelFile(conn, noteid, fdown);
				outputFile(response, fdown, fileName);

			} catch (SQLException e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
				output(response, elm_doc);
			} catch (Exception e) {
				// e.printStackTrace();
				elm_doc.addContent(new XErr(-1, e.toString()).toElement());
				output(response, elm_doc);
			} finally {
				closeDataSource(conn);
				fdown.delete();
			}
		}
	}
}

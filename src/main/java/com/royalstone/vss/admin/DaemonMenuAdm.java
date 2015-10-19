/*
 * Created on 2005-08-20
 */
package com.royalstone.vss.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.aw.ColModelDAO;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;

/**
 * @author Mengluoyi
 * 
 */
public class DaemonMenuAdm extends XDaemon {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");

		Connection conn = null;
		try {

			String action = request.getParameter("action");
			if (action == null || action.length() == 0) {
				throw new InvalidDataException("action is valid!");
			}
			if (!isSessionActive(request))
				throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null)
				throw new PermissionException(PermissionException.LOGIN_PROMPT);

			/**
			 * ISOLATION set to DIRTY READ;
			 */
			conn = openDataSource(token.site.getDbSrcName());
			// conn.setTransactionIsolation(
			// Connection.TRANSACTION_READ_UNCOMMITTED );
			MenuAdm adm = new MenuAdm(conn);

			if (action != null && action.equalsIgnoreCase("get_detail")) {
				String menuid = request.getParameter("menuid");
				if (menuid == null || menuid.length() == 0)
					throw new InvalidDataException("menuid is invalid!");
				int id = Integer.parseInt(menuid);
				Element elm = adm.getMenuDetail(id);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("get_root")) {

				Element elm = adm.getRootList();
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("get_user_root")) {
				String userid = request.getParameter("userid");
				Element elm = adm.getUserRootList(userid);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			}
			if (action != null && action.equalsIgnoreCase("get_roletype_root")) {
				String roletype = request.getParameter("roletype");
				Element elm = adm.getRootListByRoletype(roletype);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("add_root")) {
				Document doc = this.getParamDoc(request);
				Element elm_root = doc.getRootElement();
				String menulabel = elm_root.getAttributeValue("menulabel");
				int roletype = Integer.valueOf(elm_root.getAttributeValue("roletype"));
				Element elm = adm.addMenuRoot(menulabel, roletype);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("get_menu")) {
				String menuid = request.getParameter("menuid");
				if (menuid == null || menuid.length() == 0)
					throw new InvalidDataException("menuid is invalid!");
				int id = Integer.parseInt(menuid);

				Element elm = adm.getMenuInfo(id);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("delete")) {
				String menuid = request.getParameter("menuid");
				if (menuid == null || menuid.length() == 0)
					throw new InvalidDataException("menuid is invalid!");
				int id = Integer.parseInt(menuid);

				adm.removeMenu(id);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("add_menu")) {
				String headmenuid = request.getParameter("headmenuid");
				if (headmenuid == null || headmenuid.length() == 0)
					throw new InvalidDataException("headmenuid is invalid!");

				Document doc = this.getParamDoc(request);
				Element elm_root = doc.getRootElement();
				String menulabel = elm_root.getAttributeValue("menulabel");
				String moduleid = elm_root.getAttributeValue("moduleid");
				if (moduleid == null || moduleid.length() == 0)
					throw new InvalidDataException("moduleid is invalid!");

				int id_head = Integer.parseInt(headmenuid);
				int id_module = Integer.parseInt(moduleid);
				Element elm = adm.addMenu(id_head, menulabel, id_module);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("update_menu")) {
				Document doc = this.getParamDoc(request);
				Element elm_root = doc.getRootElement();
				String menulabel = elm_root.getAttributeValue("menulabel");
				String moduleid = elm_root.getAttributeValue("moduleid");
				String menuid = elm_root.getAttributeValue("menuid");
				if (menuid == null || menuid.length() == 0)
					throw new InvalidDataException("menuid is invalid!");
				if (moduleid == null || moduleid.length() == 0)
					throw new InvalidDataException("moduleid is invalid!");

				int id_module = Integer.parseInt(moduleid);
				int id_menu = Integer.parseInt(menuid);
				Element elm = adm.updateMenu(id_menu, menulabel, id_module);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("getCMByModuleid")) {
				String temp = request.getParameter("moduleid");
				if (temp == null || temp.length() == 0) {
					throw new InvalidDataException("moduleid is null");
				}
				int moduleid = Integer.parseInt(temp);
				ColModelDAO dao = new ColModelDAO(conn);
				Element elm_out = new Element("xout").addContent(dao.getCMByModuleid(moduleid));
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (action != null && action.equalsIgnoreCase("updateCM")) {
				String temp = request.getParameter("menuid");
				if (temp == null || temp.length() == 0) {
					throw new InvalidDataException("menuid is null");
				}
				int menuid = Integer.parseInt(temp);

				temp = request.getParameter("cmid");
				if (temp == null || temp.length() == 0) {
					throw new InvalidDataException("cmid is null");
				}
				long cmid = Long.parseLong(temp);
				adm.updateCM(menuid, cmid);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else {
				elm_doc.addContent(new XErr(-1, "Invalid action.").toElement());

			}

		} catch (SQLException e) {
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage()).toElement());
		} catch (Exception e) {
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			e.printStackTrace();
		} finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private static final long	serialVersionUID	= 20060909L;
}

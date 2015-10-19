/*
 * Created on 2005-08-20
 *
 */
package com.royalstone.vss.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;

/**
 * @author Mengluoyi
 * 
 */
public class DaemonVenderUserAdm extends XDaemon {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");

		Connection conn = null;
		try {

			String operation = request.getParameter("operation");
			if (operation == null || operation.length() == 0) {
				throw new InvalidDataException("operation is valid!");
			}

			/**
			 * 把前台传回的参数转成一个map 对象, 主要为查询用户信息而准备.
			 */
			Map parms = request.getParameterMap();
			if (!isSessionActive(request))
				throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null)
				throw new PermissionException(PermissionException.LOGIN_PROMPT);

			conn = openDataSource(token.site.getDbSrcName());
			VenderUserAdm adm = new VenderUserAdm(conn);

			if (operation != null
					&& operation.equalsIgnoreCase("list_vender_user")) {
				Element elm = adm.getUserList(parms);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			}

			/**
			 * 根据用户编号查询用户信息(一次查一个用户)
			 */
			else if (operation != null
					&& operation.equalsIgnoreCase("get_user")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				int id = Integer.parseInt(userid);
				Element elm = adm.getUserInfo(id);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			}

			else if (operation != null
					&& operation.equalsIgnoreCase("get_user_by_loginid")) {
				String loginid = request.getParameter("loginid");
				if (loginid == null || loginid.length() == 0)
					throw new InvalidDataException("loginid is invalid!");
				Element elm = adm.getUserByLoginid(loginid);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			}

			/**
			 * 修改用户信息(用户名, 登录名, 机构). 可以同时修改登录密码.
			 */
			else if (operation != null
					&& operation.equalsIgnoreCase("update_user")) {
				Document doc = this.getParamDoc(request);
				Element elm_root = doc.getRootElement();
				CyberUser user = new CyberUser(elm_root);
				adm.updateUser(user.userid, user.username, user.loginid,
						user.shopid, user.menuroot, user.status);

				/**
				 * 如果同时提供了密码参数则修改密码. 如果前台没有提供密码则不修改.
				 */
				if (user.password != null && user.password.length() > 0)
					adm.setPassword(user.userid, user.password);

				Element elm = adm.getUserInfo(user.userid);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			}

			/**
			 * 修改用户登录密码
			 */
			else if (operation != null
					&& operation.equalsIgnoreCase("set_pass")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				int id = Integer.parseInt(userid);
				String password = request.getParameter("password");
				if (password != null && password.length() > 0)
					adm.setPassword(id, password);
				Element elm = adm.getUserInfo(id);
				Element elm_out = new Element("xout").addContent(elm);
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			}

			/**
			 * 修改用户状态( 正常, 锁定, 退出 )
			 */
			else if (operation != null
					&& operation.equalsIgnoreCase("set_status")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				int id = Integer.parseInt(userid);
				String status = request.getParameter("status");
				if (status == null || status.length() == 0)
					throw new InvalidDataException("status is invalid!");
				int status_value = Integer.parseInt(status);
				adm.setUserStatus(id, status_value);
				elm_doc.addContent(new XErr(0, "OK").toElement());

			} else if (operation != null
					&& operation.equalsIgnoreCase("delExtBusinessid")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String businessid = request.getParameter("businessid");
				if (businessid == null || businessid.length() == 0)
					throw new InvalidDataException("businessid is invalid!");
				String[] bb = businessid.split(",");
				int id = Integer.parseInt(userid);
				adm.delExtBusinessid(id, bb);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("updateDefBussinessid")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String oldValue = request.getParameter("oldValue");
				if (oldValue == null || oldValue.length() == 0)
					throw new InvalidDataException("oldValue is invalid!");
				String newValue = request.getParameter("newValue");
				if (newValue == null || newValue.length() == 0)
					throw new InvalidDataException("newValue is invalid!");
				int id = Integer.parseInt(userid);

				adm.updateDefBussinessid(id, oldValue, newValue);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("updateExtBussinessid")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String oldValue = request.getParameter("oldValue");
				if (oldValue == null || oldValue.length() == 0)
					throw new InvalidDataException("oldValue is invalid!");
				String newValue = request.getParameter("newValue");
				if (newValue == null || newValue.length() == 0)
					throw new InvalidDataException("newValue is invalid!");
				int id = Integer.parseInt(userid);
				adm.updateExtBussinessid(id, oldValue, newValue);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("addExtBusinessid")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String businessid = request.getParameter("businessid");
				if (businessid == null || businessid.length() == 0)
					throw new InvalidDataException("businessid is invalid!");
				String[] bb = businessid.split(",");
				int id = Integer.parseInt(userid);
				adm.addExtBusinessid(id, bb);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("addDefBusinessid")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String businessid = request.getParameter("businessid");
				if (businessid == null || businessid.length() == 0)
					throw new InvalidDataException("businessid is invalid!");
				String[] bb = businessid.split(",");
				int id = Integer.parseInt(userid);
				adm.addDefBusinessid(id, bb);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("getUserBusinessid")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				int id = Integer.parseInt(userid);
				elm_doc.addContent(adm.getUserBusinessid(id));

				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("getUserBU")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				int id = Integer.parseInt(userid);
				elm_doc.addContent(adm.getUserBU(id));
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("delUserBU")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String buid = request.getParameter("buid");
				if (buid == null || buid.length() == 0)
					throw new InvalidDataException("buid is invalid!");

				int id = Integer.parseInt(userid);
				adm.delUserBU(id, buid);
				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else if (operation != null
					&& operation.equalsIgnoreCase("addUserBU")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String buid = request.getParameter("buid");
				if (buid == null || buid.length() == 0)
					throw new InvalidDataException("buid is invalid!");

				int id = Integer.parseInt(userid);
				adm.addUserBU(id, buid);
				elm_doc.addContent(new XErr(0, "OK").toElement());

			} else if (operation != null
					&& operation.equalsIgnoreCase("updateUserBU")) {
				String userid = request.getParameter("userid");
				if (userid == null || userid.length() == 0)
					throw new InvalidDataException("userid is invalid!");
				String oldValue = request.getParameter("oldValue");
				if (oldValue == null || oldValue.length() == 0)
					throw new InvalidDataException("oldValue is invalid!");
				String newValue = request.getParameter("newValue");
				if (newValue == null || newValue.length() == 0)
					throw new InvalidDataException("newValue is invalid!");
				int id = Integer.parseInt(userid);
				adm.updateUserBU(id, oldValue, newValue);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			} else {
				elm_doc.addContent(new XErr(-1, "Invalid action.").toElement());
			}

		} catch (SQLException e) {
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage())
					.toElement());
		} catch (InvalidDataException e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		} catch (Exception e) {
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		} finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	private static final long serialVersionUID = 20060909L;
}

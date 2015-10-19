/*
 * Created on 2005-08-20
 *
 */
package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.royalstone.vss.catalogue.SearchVender;
import com.royalstone.vss.edi.EDIVender;
import com.royalstone.vss.vender.VenderInformationDIY;

/**
 * 此模块用于维护供应商分组. 根据前台参数,可以完成多项操作: 查看指定号码段的供应商资料; 查看供应商分组; 新建供应商分组; 删除供应商分组;
 * 
 * @author Mengluoyi
 * 
 */
public class DaemonVenderAdmin extends XDaemon {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding("UTF-8");
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");
		Connection conn = null;
		try {
			if (!isSessionActive(request)) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);

			/**
			 * 把前台传回的参数转成一个map 对象, 主要为查询用户信息而准备.
			 */
			Map map = request.getParameterMap();
			String focus = request.getParameter("focus");
			String operation = request.getParameter("operation");
			if (focus == null || focus.length() == 0) { throw new InvalidDataException("focus is valid!"); }

			if (operation == null || operation.length() == 0) { throw new InvalidDataException(
					"operation is valid!"); }

			conn = openDataSource(token.site.getDbSrcName());

			/**
			 * 要求查看供应商组的成员. 供应商号码, 供应商名称, 地址, 电话.
			 */
			if (focus.equalsIgnoreCase("vendergroup") && operation.equalsIgnoreCase("list_member")) {}

			/**
			 * 要求查看指定范围的供应商数量, 为建立新的供应商组作准备.
			 */
			else if (focus.equalsIgnoreCase("vendergroup") && operation.equalsIgnoreCase("get_count")) {
				SearchVender search = new SearchVender(conn, map, "1");
				Element elm_cat = new Element("count").setText(String.valueOf(search.toCount()));
				elm_out.addContent(elm_cat);
			}
			/**
			 * 显示供应商联系等信息
			 */
			else if (focus.equalsIgnoreCase("venderdiy") && operation.equalsIgnoreCase("listall")) {
				VenderInformationDIY vdiy = new VenderInformationDIY();
				Element elm_cat = vdiy.getVenderInfoList(conn, map);
				elm_out.addContent(elm_cat);
			}
			/**
			 * 显示供应商自己维护的信息
			 */
			else if (focus.equalsIgnoreCase("venderdiy") && operation.equalsIgnoreCase("list")) {
				VenderInformationDIY vdiy = new VenderInformationDIY();
				Element elm_cat = vdiy.getVenderInfo(conn, token);
				elm_out.addContent(elm_cat);
			}
			/**
			 * 更新供应商自己维护的信息(供应商维护)
			 */
			else if (focus.equalsIgnoreCase("venderdiy") && operation.equalsIgnoreCase("update")) {
				VenderInformationDIY vdiy = new VenderInformationDIY();
				vdiy.setVenderInfo(conn, token, request);
			}else if (focus.equalsIgnoreCase("venderdiy") && operation.equalsIgnoreCase("agreeCrbLic")) {
				VenderInformationDIY vdiy = new VenderInformationDIY();
				vdiy.agreeCrbLic(conn, token.getBusinessid());
			}else if (focus.equalsIgnoreCase("venderdiy") && operation.equalsIgnoreCase("getCrbLicStatus")) {
				VenderInformationDIY vdiy = new VenderInformationDIY();
				elm_out.addContent(vdiy.getCrbLicStatus(conn, token.getBusinessid()));
			} else
				throw new InvalidDataException("查询参数不存在");
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());
		}
		catch (SQLException e) {
			// e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage()).toElement());
		}
		catch (Exception e) {
			// e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doPost(request, response);
	}

	private static final long	serialVersionUID	= 20060912L;
}

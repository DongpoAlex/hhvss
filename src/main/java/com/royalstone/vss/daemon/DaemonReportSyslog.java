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
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.report.ReportSyslog;

/**
 * 查询系统日志
 * 
 * @author baibai
 * 
 */
public class DaemonReportSyslog extends XDaemon {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		request.setCharacterEncoding("UTF-8");
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");

		Connection conn = null;
		try {
			if (!isSessionActive(request)) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			if (token.isVender) throw new PermissionException("本模块仅可由零售商用户调用!");

			conn = openDataSource(token.site.getDbSrcName());

			String operation = request.getParameter("operation");
			Map map = request.getParameterMap();

			if (operation.equals("browse")) {
				ReportSyslog syslog = new ReportSyslog(conn, map);
				Element elm_report = syslog.toElement();
				elm_out.addContent(elm_report);
			} else {
				ReportSyslog syslog = new ReportSyslog(conn, map);
				String filtrate = request.getParameter("filtrate");
				if (filtrate == null || !filtrate.equals("distinct")) filtrate = "";
				Element elm_report = syslog.getStat(filtrate);
				elm_out.addContent(elm_report);
			}

			elm_doc.addContent(elm_out);
			elm_doc.addContent((new XErr(0, "OK")).toElement());
		}
		catch (IllegalArgumentException e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		}
		catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
		}
		catch (Exception e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	private static final long	serialVersionUID	= 20061226L;
}

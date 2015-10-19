package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.oi.OIDao;

/**
 * @author 白剑
 * OI
 */
public class DaemonOI extends XDaemon {
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

		String operation = request.getParameter("operation");
		try {
			Token token = this.getToken(request);
			conn = openDataSource(token.site.getDbSrcName());
			OIDao dao = new OIDao(conn, token);
			if ("search".equals(operation)) {
				HashMap parms = new HashMap(request.getParameterMap());
				if(token.isVender){
					parms.remove("venderid");
					parms.put("venderid", new String[]{token.getBusinessid()});
				}
				elm_out.addContent(dao.search(parms));
			} else if ("show".equals(operation)) {
				String sheetid = request.getParameter("sheetid");
				elm_out.addContent(dao.show(sheetid));
			} else if ("save".equals(operation)) {
				Document doc = this.getParamDoc(request);
				Element root = doc.getRootElement();
				Element elmHead = root.getChild("head");
				Element elmBody = root.getChild("rowset");
				conn.setAutoCommit(false);
				String sheetid = dao.update(elmHead, elmBody);
				conn.commit();
				elm_out.addContent(sheetid);
			} else if ("comf".equals(operation)) {
				String sheetid = request.getParameter("sheetid");
				conn.setAutoCommit(false);
				dao.comf(sheetid);
				conn.commit();
			} else if ("delete".equals(operation)) {
				String sheetid = request.getParameter("sheetid");
				conn.setAutoCommit(false);
				dao.delete(sheetid);
				conn.commit();
			} else if ("getMajorInfo".equals(operation)) {
				String majorid=request.getParameter("majorid");
				elm_out.addContent(dao.getMajorInfo(majorid));
			} else if ("getShopInfo".equals(operation)) {
				String shopid=request.getParameter("shopid");
				elm_out.addContent(dao.getShopInfo(shopid));
			} else {
				throw new InvalidDataException("未知的参数");
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

		}
		catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

}

package com.royalstone.vss.daemon;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.sheet.ISheetService;
import com.royalstone.vss.sheet.SheetServiceFactory;

public class DaemonSheet extends XDaemon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
			Token token = this.getToken(request);
			conn = openDataSource(token.site.getDbSrcName());

			String operation = request.getParameter("operation");
			String clazz = request.getParameter("clazz");
			String cmid = request.getParameter("cmid");
			if(cmid==null) cmid="";


			ISheetService service = SheetServiceFactory.factory(clazz, conn, token, cmid);

			if ("show".equals(operation)) {
				String sheetid = request.getParameter("sheetid");
				service.checkVenderid(sheetid, token);
				elm_out.addContent(service.veiw(sheetid));
			} else if ("search".equals(operation)) {

				Map<String, String[]> parms = new HashMap<String, String[]>(request.getParameterMap());
				if (token.isVender) {
					parms.remove("venderid");
					parms.put("venderid", new String[] { token.getBusinessid() });
				}
				elm_out.addContent(service.search(parms));
			} else if ("doRead".equals(operation) && token.isVender) {
				//只有非零售商才能改阅读状态
				String sheetid = request.getParameter("sheetid");
				if(service.checkVenderid(sheetid, token))
					service.doRead(sheetid);
			} else if ("doConfirm".equals(operation) && token.isVender) {
				//只有非零售商才能改阅读状态
				String sheetid = request.getParameter("sheetid");
				if(service.checkVenderid(sheetid, token))
					service.doConfirm(sheetid);
			} else {
				elm_out.addContent(service.execute(operation,request));
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());
		} catch (InvocationTargetException e) {
			if (e.getCause() == null) {
				elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
			} else {
				elm_doc.addContent(new XErr(-1, e.getCause().getMessage()).toElement());
			}
		}
		catch (Exception e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

}

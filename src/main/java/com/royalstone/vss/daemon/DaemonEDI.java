package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.edi.EDIVender;

/**
 * @author 白剑
 * EDI维护
 */
public class DaemonEDI extends XDaemon {
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
			EDIVender service = new EDIVender(conn, token);
			if ("search".equals(operation)) {
				HashMap parms = new HashMap(request.getParameterMap());
				if(token.isVender){
					parms.remove("venderid");
					parms.put("venderid", new String[]{token.getBusinessid()});
				}
				elm_out.addContent(service.getVenderList(parms));
			} else if ("save".equals(operation)) {
				String venderid = request.getParameter("venderid");
				String[] seqs = (String[])request.getParameterMap().get("seqs");
				service.save(venderid, seqs);
			} else {
				throw new InvalidDataException("未知的参数");
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

		}
		catch (Exception e) {
			e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

}

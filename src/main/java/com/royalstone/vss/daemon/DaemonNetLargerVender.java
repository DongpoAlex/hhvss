package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
import com.royalstone.vss.edi.EDIVender;
import com.royalstone.vss.net.NetLargeVender;

public class DaemonNetLargerVender extends XDaemon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1948431629577986874L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");
		Connection conn = null;

		String action = request.getParameter("action");
		if (action == null || action.length() == 0) {
			throw new InvalidDataException("action is valid!");
		}
		
		try {
			Token token = this.getToken(request);
			conn = openDataSource(token.site.getDbSrcName());
			NetLargeVender service = new NetLargeVender(conn, token);
			if ("list_vender".equals(action)) {
				HashMap parms = new HashMap(request.getParameterMap());
				if(token.isVender){
					parms.remove("venderid");
					parms.put("venderid", new String[]{token.getBusinessid()});
				}
				elm_out.addContent(service.getVenderList(parms));
			} else if ("save".equals(action)) {
				String dccode = request.getParameter("dccode");
				String vendcode = request.getParameter("vendcode");
				String vendertype = request.getParameter("vendertype").toString();
				String note = request.getParameter("note");
				String username = token.username;
				service.save(dccode,vendcode,vendertype,note,username);
			} else if("del".equals(action)){
				String dccode = request.getParameter("dccode");
				String vendcode = request.getParameter("vendcode");
				String username = token.username;
				service.del(dccode,vendcode,username);
			} else if("upd".equals(action)){
				String dccode = request.getParameter("dccode");
				String vendcode = request.getParameter("vendcode");
				String vendertype = request.getParameter("vendertype").toString();
				String note = request.getParameter("note");
				String username = token.username;
				service.upd(dccode,vendcode,vendertype,note,username);
			}else {
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

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}

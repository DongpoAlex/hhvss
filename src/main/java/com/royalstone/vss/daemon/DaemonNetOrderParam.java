package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdom.Element;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.net.NetOrderPara;

public class DaemonNetOrderParam extends XDaemon {

	private static final long serialVersionUID = -2385912085763605912L;


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");
		request.setCharacterEncoding("UTF-8");
		Connection conn = null;
		
		String action = request.getParameter("action");
		if (action == null || action.length() == 0) {
			throw new InvalidDataException("action is valid!");
		}
		
		try {
			Token token = this.getToken(request);
			conn = openDataSource(token.site.getDbSrcName());
			NetOrderPara service = new NetOrderPara(conn, token);
			if ("netparam_list".equals(action)) {
				String dccode = request.getParameter("dccode");
				elm_out.addContent(service.getOrderParam(dccode));
			}else if("netparamdate_list".equals(action)){
				String dccode = request.getParameter("dccode");
				String logistics = request.getParameter("logistics");
				elm_out.addContent(service.getOrderParamDate(dccode,logistics));
			}else if("netparam_lastdate".equals(action)){
				String dccode = request.getParameter("dccode");
				elm_out.addContent(service.getOrderParamLastdate(dccode));
			} else if ("add".equals(action)) {
				String dccode = request.getParameter("dccode");
				String isyesps = request.getParameter("isyesps");
				String ordertime = request.getParameter("ordertime");
				String orderkfts = request.getParameter("orderkfts");
				String stoporderdate = request.getParameter("stoporderdate");
				String orderlastdate = request.getParameter("orderlastdate");
				String ordernote = request.getParameter("ordernote");
				String dzsku = request.getParameter("dzsku");
				String dzpqty = request.getParameter("dzpqty");
				String username = token.username;
				service.add(dccode,isyesps,ordertime,orderkfts,stoporderdate,orderlastdate,ordernote,username,dzsku,dzpqty);
			} else if("del".equals(action)){
				String dccode = request.getParameter("dccode");
				service.del(dccode);
			} else if("upd".equals(action)){
				String dccode = request.getParameter("dccode");
				String isyesps = request.getParameter("isyesps");
				String ordertime = request.getParameter("ordertime");
				String orderkfts = request.getParameter("orderkfts");
				String stoporderdate = request.getParameter("stoporderdate");
				String orderlastdate = request.getParameter("orderlastdate");
				String ordernote = request.getParameter("ordernote");
				String dzsku = request.getParameter("dzsku");
				String dzpqty = request.getParameter("dzpqty");
				String username = token.username;
				service.upd(dccode,isyesps,ordertime,orderkfts,stoporderdate,orderlastdate,ordernote,username,dzsku,dzpqty);
			}else if("deldate".equals(action)){
				String dccode = request.getParameter("dccode");
				String logistics = request.getParameter("logistics");
				service.deldate(dccode,logistics);
			}else if("upddate".equals(action)){
				String dccode = request.getParameter("dccode");
				String logistics = request.getParameter("logistics");
				String monday = request.getParameter("monday");
				String tuesday = request.getParameter("tuesday");
				String wednesday = request.getParameter("wednesday");
				String thursday = request.getParameter("thursday");
				String friday = request.getParameter("friday");
				String saturday = request.getParameter("saturday");
				String sunday = request.getParameter("sunday");
				String note = request.getParameter("note");
				String username = token.username;
				service.upddate(dccode,logistics,monday,tuesday,wednesday,thursday,friday,saturday,sunday,note,username);
			}else if ("adddate".equals(action)) {
				String dccode = request.getParameter("dccode");
				String logistics = request.getParameter("logistics");
				String monday = request.getParameter("monday");
				String tuesday = request.getParameter("tuesday");
				String wednesday = request.getParameter("wednesday");
				String thursday = request.getParameter("thursday");
				String friday = request.getParameter("friday");
				String saturday = request.getParameter("saturday");
				String sunday = request.getParameter("sunday");
				String note = request.getParameter("note");
				String username = token.username;
				service.adddate(dccode,logistics,monday,tuesday,wednesday,thursday,friday,saturday,sunday,note,username);
			}else {
				throw new InvalidDataException("未知的参数");
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

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

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		   doPost(request, response);
	}

}

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
import com.royalstone.vss.net.NetLargeVender;
import com.royalstone.vss.net.NetOrderTime;

public class DaemonNetOrderTime extends XDaemon {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6838757398571967359L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			NetOrderTime service = new NetOrderTime(conn, token);
			if ("getordertiemList".equals(action)) {
				HashMap parms = new HashMap(request.getParameterMap());
				if (token.isVender) {
					parms.remove("venderid");
					parms.put("venderid", new String[] { token.getBusinessid() });
				}
				elm_out.addContent(service.getOrderTime(parms));
			} else if ("add".equals(action)) {
				String dccode = request.getParameter("dccode");
				String logistics = request.getParameter("logistics");
				String starttime = request.getParameter("starttime");
				String endtime = request.getParameter("endtime");
				String timejg = request.getParameter("timejg");
				String maxsku = request.getParameter("maxsku");
				String maxxs = request.getParameter("maxxs");
				String maxsupply = request.getParameter("maxsupply");
				String maxdzsupply = request.getParameter("maxdzsupply");
				String maxyssupply = request.getParameter("maxyssupply");
				String note = request.getParameter("note");
				String username = token.username;

				if (Double.parseDouble(timejg) < 0) {
					throw new InvalidDataException("时间间隔不能是负数");
				}

				if (Double.parseDouble(maxsku) < 0) {
					throw new InvalidDataException("SKU数上限不能是负数");
				}

				if (Double.parseDouble(maxxs) < 0) {
					throw new InvalidDataException("箱数上限不能是负数");
				}

				if (Double.parseDouble(maxsupply) < 0) {
					throw new InvalidDataException("供应商个数上限不能是负数");
				}

				if (Double.parseDouble(maxdzsupply) < 0) {
					throw new InvalidDataException("大宗供应商预约个数上限不能是负数");
				}

				if (Double.parseDouble(maxyssupply) < 0) {
					throw new InvalidDataException("综合验收供应商数上限不能是负数");
				}

				service.add(dccode, logistics, starttime, endtime, timejg, maxsku, maxxs, maxsupply, maxdzsupply,
						maxyssupply, note, username);
			} else if ("del".equals(action)) {
				String dccode = request.getParameter("dccode");
				String logistics = request.getParameter("logistics");
				String starttime = request.getParameter("starttime");
				String endtime = request.getParameter("endtime");
				service.del(dccode, logistics, starttime, endtime);
			} else if ("upd".equals(action)) {
				String dccode = request.getParameter("dccode");
				String logistics = request.getParameter("logistics");
				String starttime = request.getParameter("starttime");
				String endtime = request.getParameter("endtime");
				String timejg = request.getParameter("timejg");
				String maxsku = request.getParameter("maxsku");
				String maxxs = request.getParameter("maxxs");
				String maxsupply = request.getParameter("maxsupply");
				String maxdzsupply = request.getParameter("maxdzsupply");
				String maxyssupply = request.getParameter("maxyssupply");
				String note = request.getParameter("note");
				String username = token.username;

				if (Double.parseDouble(timejg) < 0) {
					throw new InvalidDataException("时间间隔不能是负数");
				}

				if (Double.parseDouble(maxsku) < 0) {
					throw new InvalidDataException("SKU数上限不能是负数");
				}

				if (Double.parseDouble(maxxs) < 0) {
					throw new InvalidDataException("箱数上限不能是负数");
				}

				if (Double.parseDouble(maxsupply) < 0) {
					throw new InvalidDataException("供应商个数上限不能是负数");
				}

				if (Double.parseDouble(maxdzsupply) < 0) {
					throw new InvalidDataException("大宗供应商预约个数上限不能是负数");
				}

				if (Double.parseDouble(maxyssupply) < 0) {
					throw new InvalidDataException("综合验收供应商数上限不能是负数");
				}

				service.upd(dccode, logistics, starttime, endtime, timejg, maxsku, maxxs, maxsupply, maxdzsupply,
						maxyssupply, note, username);
			} else {
				throw new InvalidDataException("未知的参数");
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

		} catch (SQLException e) {
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage()).toElement());
		} catch (InvalidDataException e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		} catch (Exception e) {
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		} finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}

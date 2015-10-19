/*
 * Created on 2006-07-31
 *
 */

package com.royalstone.vss.his;

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
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;

/**
 * 转类前历史备份表查询
 * 
 * @author baijian
 */
public class DaemonSearchHisSheet extends XDaemon {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
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

			conn = openDataSource(token.site.getDbSrcName());
			// conn.setTransactionIsolation(
			// Connection.TRANSACTION_READ_UNCOMMITTED );

			String sheetname = request.getParameter("sheetname");
			if (sheetname == null || sheetname.length() == 0) throw new InvalidDataException(
					"sheetname not set! ");

			HashMap parms = new HashMap(request.getParameterMap());

			/**
			 * 供应商只可以查看自己的单据. 如果访问者以供应商身份登录, 应添加针对供应商ID的过滤条件.
			 */
			String venderid = "";
			if (token.isVender) {
				String[] arr_vender = new String[1];
				arr_vender[0] = token.getBusinessid();
				parms.put("venderid", arr_vender);
				venderid = arr_vender[0];
			}

			/**
			 * 订货审批单中查询
			 */
			if (sheetname.equalsIgnoreCase("purchasechk")) {
				SearchPurchaseChkSheet search = new SearchPurchaseChkSheet(conn, parms);
				Element elm_cat = search.toElement();
				elm_out.addContent(elm_cat);
			}
			/**
			 * 查询订货通知单
			 */
			else if (sheetname.equalsIgnoreCase("purchase")) {
				SearchPurchseSheet search = new SearchPurchseSheet(conn, parms);
				Element elm_cat = search.toElement();
				elm_out.addContent(elm_cat);
			}
			// 退货通知单
			else if (sheetname.equalsIgnoreCase("retnotice")) {
				SearchRetNotice search = new SearchRetNotice(conn, parms);
				Element elm_cat = search.toElement();
				elm_out.addContent(elm_cat);
				// 销售日报
			} else if (sheetname.equalsIgnoreCase("sdaily")) {
				// 对于ole供应商，contracttype=1的不展示数据
				if (token.site.getSid()==2
						&& com.royalstone.vss.basic.Vender.getVenderContracttype(conn, venderid) == 1) {
					elm_out.addContent(new Element("report"));
				} else {
					SaleVenderShopDaily sd = new SaleVenderShopDaily(conn, parms);
					Element elm_report = sd.getSaleDaily();
					elm_out.addContent(elm_report);
				}
			} else if (sheetname.equalsIgnoreCase("stkcostadj")) {
				SearchStkCostAdj search = new SearchStkCostAdj(conn, parms);
				Element elm_cat = search.toElement();
				elm_out.addContent(elm_cat);
				// 带货按装
			} else if (sheetname.equalsIgnoreCase("salepick")) {
				SearchSalePick search = new SearchSalePick(conn, parms);
				Element elm_cat = search.toElement();
				elm_out.addContent(elm_cat);
			} else if (sheetname.equalsIgnoreCase("promshare")) {
				SearchPromShare search = new SearchPromShare(conn, parms, "_cdept");
				Element elm_cat = search.toElement();
				elm_out.addContent(elm_cat);
				/*
				 * 参数不存在
				 */
			} else
				throw new InvalidDataException("Undefined sheetname: " + sheetname);
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

		}
		catch (SQLException e) {
			e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
		}
		catch (Exception e) {
			e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	private static final long	serialVersionUID	= 20060731L;
}

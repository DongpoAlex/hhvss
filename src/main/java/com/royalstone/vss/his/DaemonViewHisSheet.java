/*
 * Created on 2006-07-20
 */

package com.royalstone.vss.his;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
public class DaemonViewHisSheet extends XDaemon {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding("UTF-8");
		Element elm_doc = new Element("xdoc");

		Connection conn = null;
		try {
			if (!isSessionActive(request)) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);

			conn = openDataSource(token.site.getDbSrcName());

			String sheet = request.getParameter("sheet");
			String sheetid = request.getParameter("sheetid");
			if (sheet == null || sheet.length() == 0) throw new InvalidDataException("sheet not set! ");
			if (sheetid == null || sheetid.length() == 0) throw new InvalidDataException("sheetid not set! ");

			Element elm_out = new Element("xout");

			/**
			 * 供应商只可以查看自己的单据. 如果访问者以供应商身份登录, 应检查单据中的venderid 与 登陆的 venderid 是否相同
			 */
			String venderid = token.getBusinessid();

			/**
			 * 订货审批单
			 */
			if (sheet.equalsIgnoreCase("purchasechk")) {
				ShowPurchsechkSheet show = new ShowPurchsechkSheet(conn, sheetid);

				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			}

			/**
			 * 订货通知单
			 */
			else if (sheet.equalsIgnoreCase("purchase")) {
				ShowPurchase show = new ShowPurchase(conn, sheetid);

				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			}

			/**
			 * 退货通知单
			 */
			else if (sheet.equalsIgnoreCase("retnotice")) {
				ShowRetNotice show = new ShowRetNotice(conn, sheetid);

				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			}
			// 单货按装
			else if (sheet.equalsIgnoreCase("salepick")) {
				ShowSalePick show = new ShowSalePick(conn, sheetid);
				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_sheet = show.getSheetDetail();
				elm_out.addContent(elm_sheet);
			// 促销	
			} else if (sheet.equalsIgnoreCase("promshare")) {
				ShowPromshare show = new ShowPromshare(conn, sheetid);
				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			//进价调整
			} else if (sheet.equalsIgnoreCase("stkcostadj")) {
				ShowStkCostAdj show = new ShowStkCostAdj(conn, sheetid);
				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
				
				
				
				
				
				
				
				
				
				
				
				
			} else if (sheet.equalsIgnoreCase("purchasechkread")) {
				ShowPurchsechkSheet show = new ShowPurchsechkSheet(conn, sheetid);
				show.setSheetRead(venderid);
			} else if (sheet.equalsIgnoreCase("retnoticeread")) {
				ShowRetNotice show = new ShowRetNotice(conn, sheetid);
				show.setSheetRead(venderid);
			} else if (sheet.equalsIgnoreCase("salepickread")) {
				ShowSalePick show = new ShowSalePick(conn, sheetid);
				show.updateStatus(0, 1, sheetid);
			}
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

	private static final long	serialVersionUID	= 20060918L;
}

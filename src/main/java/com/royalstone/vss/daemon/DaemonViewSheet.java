/*
 * Created on 2006-07-20
 */

package com.royalstone.vss.daemon;

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
import com.royalstone.vss.detail.ShowElecInstall;
import com.royalstone.vss.detail.ShowLiquidation;
import com.royalstone.vss.detail.ShowPromshare;
import com.royalstone.vss.detail.ShowPurchase;
import com.royalstone.vss.detail.ShowPurchsechk;
import com.royalstone.vss.detail.ShowReceipt;
import com.royalstone.vss.detail.ShowRentNote;
import com.royalstone.vss.detail.ShowRet;
import com.royalstone.vss.detail.ShowRetNotice;
import com.royalstone.vss.detail.ShowSalePick;
import com.royalstone.vss.detail.ShowSaleadj;
import com.royalstone.vss.detail.ShowVenderInvoice;

/**
 * 此模块用于查询指定单据的明细. 前台模块应提供两个参数: sheetname, sheetid.
 * 
 * @author mengluoyi
 */
public class DaemonViewSheet extends XDaemon {

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
			// conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

			String sheet = request.getParameter("sheet");
			String sheetid = request.getParameter("sheetid");
			if (sheet == null || sheet.length() == 0) throw new InvalidDataException("sheet not set! ");
			if (sheetid == null || sheetid.length() == 0) throw new InvalidDataException("sheetid not set! ");

			Element elm_out = new Element("xout");

			/**
			 * 供应商只可以查看自己的单据. 如果访问者以供应商身份登录, 应检查单据中的venderid 与 登陆的 venderid 是否相同
			 */
			String venderid = token.getBusinessid();

			String month = "";
			/**
			 * 订货审批单
			 */
			if (sheet.equalsIgnoreCase("ordersheet")) {
				ShowPurchsechk show = new ShowPurchsechk(conn, sheetid, month);

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
				ShowPurchase show = new ShowPurchase(conn, sheetid, month);

				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			}
			/**
			 * 退货单
			 */
			else if (sheet.equalsIgnoreCase("ret")) {
				ShowRet show = new ShowRet(conn, sheetid);

				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			}

			/**
			 * 验收单
			 */
			else if (sheet.equalsIgnoreCase("receipt")) {
				ShowReceipt show = new ShowReceipt(conn, sheetid);

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
			// 带货按装
			else if (sheet.equalsIgnoreCase("salepick")) {
				ShowSalePick show = new ShowSalePick(conn, sheetid);
				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_sheet = show.getSheetDetail();
				elm_out.addContent(elm_sheet);
			}
			// 电器按装
			else if (sheet.equalsIgnoreCase("elecinstall")) {
				ShowElecInstall show = new ShowElecInstall(conn, sheetid);
				if (token.isVender) {
					String venderid_pm = show.getServerId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("安装商只可以查看属于自己的单据!");
				}
				Element elm_sheet = show.getSheetDetail();
				elm_out.addContent(elm_sheet);
			} else if (sheet.equalsIgnoreCase("promshare")) {
				ShowPromshare show = new ShowPromshare(conn, sheetid);
				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			} else if (sheet.equalsIgnoreCase("saleadj")) {
				ShowSaleadj show = new ShowSaleadj(conn, sheetid);
				Element elm_sheet = show.toElement();
				elm_out.addContent(elm_sheet);
			}
			/**
			 * 发票接收单
			 */
			else if (sheet.equalsIgnoreCase("venderinvoice")) {
				ShowVenderInvoice search = new ShowVenderInvoice(conn, sheetid,token);
				if (token.isVender) {
					String venderid_pm = search.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}
				Element elm_sheet = search.toElement();
				elm_out.addContent(elm_sheet);
			}
			/**
			 * 对帐申请单
			 */
			else if (sheet.equalsIgnoreCase("liquidation")) {
				ShowLiquidation search = new ShowLiquidation(conn, sheetid);
				if (token.isVender) {
					String venderid_pm = search.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}

				Element elm_cat = search.toElement();
				elm_out.addContent(elm_cat);
			}
			// 联营费用通知单
			else if (sheet.equalsIgnoreCase("rentnote")) {
				ShowRentNote show = new ShowRentNote(conn, sheetid);
				if (token.isVender) {
					String venderid_pm = show.getVenderId().trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}
				elm_out.addContent(show.getSheetDetail());
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

		}
		catch (SQLException e) {
			// e.printStackTrace();
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

package com.royalstone.vss.daemon;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.certificate.dao.CertificateDAO;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.catalogue.SearchChargesum;
import com.royalstone.vss.catalogue.SearchFresh4retail;
import com.royalstone.vss.catalogue.SearchPurchaseChkSheet;
import com.royalstone.vss.catalogue.SearchReceipt;
import com.royalstone.vss.catalogue.SearchRet;
import com.royalstone.vss.catalogue.SearchRetNotice;
import com.royalstone.vss.detail.DownloadOrder;
import com.royalstone.vss.detail.DownloadPaymentnoteList;
import com.royalstone.vss.detail.DownloadPaymentsheetList;
import com.royalstone.vss.detail.ShowLiquidation;
import com.royalstone.vss.detail.ShowStkCostAdj;
import com.royalstone.vss.liquidation.LiquidationLogReport;
import com.royalstone.vss.report.KPI;
import com.royalstone.vss.vender.VenderInformationDIY;

/**
 * 公共模块，提供下载EXCEL文档支持
 * 
 * @author baibai
 * 
 */
public class DaemonDownloadExcel extends XDaemon {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding("UTF-8");
		Connection conn = null;
		File fdown = File.createTempFile("rpt", "xls");
		try {
			if (!isSessionActive(request)) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);

			String operation = request.getParameter("operation");
			if (operation == null) throw new InvalidDataException("operation is null");

			conn = openDataSource(token.site.getDbSrcName());

			HashMap parms = new HashMap(request.getParameterMap());
			if (token.isVender) {
				String[] arr_vender = new String[1];
				arr_vender[0] = token.getBusinessid();
				parms.put("venderid", arr_vender);
			}

			String fileName = "down.xls";

			/**
			 * 导出对帐申请单明细
			 */
			if (operation.equalsIgnoreCase("liquidationitem")) {
				String sheetid = request.getParameter("sheetid");
				if (sheetid == null) throw new InvalidDataException("sheetid is null");

				ShowLiquidation sl = new ShowLiquidation(conn, sheetid);
				sl.cookExcelFile(fdown);
				fileName = sheetid + ".xls";

				/**
				 * 导出对帐申请日志
				 */
			} else if (operation.equalsIgnoreCase("liquidationlog")) {
				LiquidationLogReport li = new LiquidationLogReport();
				li.cookExcelFile(conn, parms, fdown);
				fileName = "对帐申请日志.xls";

				/**
				 * 导出供应商订单接收情况
				 */
			} else if (operation.equalsIgnoreCase("fresh_confirm")) {
				SearchFresh4retail search = new SearchFresh4retail(conn, parms);
				search.cookExcelFile(fdown);
				fileName = "confirm.xls";
				/**
				 * 导出订货审批单明细
				 */
			} else if (operation.equalsIgnoreCase("order_sheet")) {
				String sheetid = request.getParameter("sheetid");
				if (sheetid == null || sheetid.length() == 0) throw new InvalidDataException(
						"sheetid not set! ");
				DownloadOrder downloader = new DownloadOrder(conn, sheetid, "");
				if (token.isVender) {
					String venderid = token.getBusinessid();
					/**
					 * 为了保障供应商业务数据的安全性, 核对安全令牌中的供应商编码与单据的供应商编码是否一致.
					 */
					String venderid_pm = downloader.getSheetVenderid(sheetid).trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以下载属于自己的数据.");

				}

				downloader.makeExcel(fdown);
				fileName = sheetid + ".xls";
			} else if (operation.equalsIgnoreCase("order_baksheet")) {
				String sheetid = request.getParameter("sheetid");
				if (sheetid == null || sheetid.length() == 0) throw new InvalidDataException(
						"sheetid not set! ");
				DownloadOrder downloader = new DownloadOrder(conn, sheetid, "_bak");
				if (token.isVender) {
					String venderid = token.getBusinessid();
					/**
					 * 为了保障供应商业务数据的安全性, 核对安全令牌中的供应商编码与单据的供应商编码是否一致.
					 */
					String venderid_pm = downloader.getSheetVenderid(sheetid).trim();
					if (!venderid_pm.equals(venderid)) throw new InvalidDataException("供应商只可以下载属于自己的数据.");

				}

				downloader.makeExcel(fdown);
				fileName = sheetid + ".xls";

				/**
				 * 导出供应商审批单目录
				 */
			} else if (operation.equalsIgnoreCase("order_retail")) {
				SearchPurchaseChkSheet search = new SearchPurchaseChkSheet(conn, parms, "");
				search.cookExcelFile(fdown);
				fileName = "审批单目录.xls";
				/**
				 * 导出供应商退货通知单目录
				 */
			} else if (operation.equalsIgnoreCase("retnotice")) {
				SearchRetNotice search = new SearchRetNotice(conn, parms);
				search.cookExcelFile(fdown);
				fileName = "退货通知单目录.xls";
				/**
				 * 导出扣项单
				 */
			} else if (operation.equalsIgnoreCase("chargesum")) {
				SearchChargesum search = new SearchChargesum(conn);
				search.cookExcelFile(parms, fdown);
				fileName = "扣项单.xls";
				/**
				 * 导出库存调进价单明细
				 */
			} else if (operation.equalsIgnoreCase("stk")) {
				String sheetid = request.getParameter("sheetid");
				if (sheetid == null || sheetid.length() == 0) throw new InvalidDataException(
						"sheetid is null");

				ShowStkCostAdj search = new ShowStkCostAdj(conn, sheetid);
				search.cookExcelFile(fdown);
				fileName = "存调进价单明细.xls";
				/**
				 * 验收(结算)单据
				 */
			} else if (operation.equalsIgnoreCase("receipt")) {
				SearchReceipt search = new SearchReceipt(conn, parms);
				search.cookExcelFile(fdown);
				fileName = "验收单.xls";

			} else if (operation.equalsIgnoreCase("ret")) {
				SearchRet search = new SearchRet(conn, parms);
				search.cookExcelFile(fdown);
				fileName = "退货单.xls";
			} else if (operation.equalsIgnoreCase("certificateDetail")) {
				CertificateDAO dao = new CertificateDAO(conn);
				dao.makeDetailExcel(fdown, parms);
				fileName = "证照明细.xls";
			} else if (operation.equalsIgnoreCase("getVenderCertificateListExcel")) {
				CertificateDAO dao = new CertificateDAO(conn);
				dao.getCheckedVenderListExcel(fdown, parms);
				fileName = "已审核供应商清单.xls";
			} else if (operation.equalsIgnoreCase("getWarnVenderCertificateListExcel")) {
				CertificateDAO dao = new CertificateDAO(conn);
				dao.getWarnVenderCertificateListExcel(fdown, parms);
				fileName = "预警类供应商清单.xls";
			} else if (operation.equalsIgnoreCase("venderlist")) {
				VenderInformationDIY dao = new VenderInformationDIY();
				dao.makeVenderListExcel(conn, fdown, parms);
				fileName = "供应商资料清单.xls";
			} else if (operation.equalsIgnoreCase("paymentnotelist")) {
				DownloadPaymentnoteList dao = new DownloadPaymentnoteList(conn);
				dao.makeVenderListExcel(fdown, parms);
				fileName = "付款单.xls";
			} else if (operation.equalsIgnoreCase("paymentsheetlist")) {
				DownloadPaymentsheetList dao = new DownloadPaymentsheetList(conn);
				dao.makeVenderListExcel(fdown, parms);
				fileName = "付款单.xls";
			} else if (operation.equalsIgnoreCase("kpi")) {
				KPI dao = new KPI(conn, parms);
				dao.toExcel(fdown);
				fileName = "供应商业绩表现卡.xls";
			}
			// 把文件传到前台
			outputFile(response, fdown, URLEncoder.encode(fileName, "UTF-8"));

		}
		catch (SQLException e) {
			// e.printStackTrace();
			Element elm_doc = new Element("xdoc");
			elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			output(response, elm_doc);
		}
		catch (Exception e) {
			e.printStackTrace();
			Element elm_doc = new Element("xdoc");
			elm_doc.addContent(new XErr(0, e.toString()).toElement());
			output(response, elm_doc);
		}
		finally {
			closeDataSource(conn);
			fdown.delete();
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doPost(request, response);
	}

	private static final long	serialVersionUID	= 20061109L;

}

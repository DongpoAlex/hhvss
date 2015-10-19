/*
 * Created on 2006-07-20
 *
 */

package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.catalogue.SearchDeduction;
import com.royalstone.vss.catalogue.SearchInmonth;
import com.royalstone.vss.liquidation.LiquidationManager;
import com.royalstone.vss.liquidation.VenderInvoiceEditor;
import com.royalstone.vss.liquidation.VenderInvoiceManager;
import com.royalstone.vss.upload.FillRate;

/**
 * 此模块用于批量数据上传处理.
 * 
 * @author mengluoyi
 */
public class DaemonUpload extends XDaemon {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding("UTF-8");
		XErr xerr = new XErr(0, "OK");
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");

		Connection conn = null;
		try {
			if (!isSessionActive(request)) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);

			conn = openDataSource(token.site.getDbSrcName());

			String sheetname = request.getParameter("sheetname");
			String operation = request.getParameter("operation");

			boolean result = true;

			if (sheetname == null || sheetname.length() == 0) throw new InvalidDataException(
					"sheetname is invalid.");
			if (operation == null || operation.length() == 0) throw new InvalidDataException(
					"operation is invalid.");

			/**
			 * 取得当前访问者venderid
			 */
			String venderid = token.getBusinessid();

			Document doc = this.getParamDoc(request);
			Element elm_root = doc.getRootElement();

			/**
			 * 上传对帐申请数据
			 */
			if (sheetname.equalsIgnoreCase("liquidation")) {
				String bookno = request.getParameter("bookno");
				if (bookno == null || bookno.length() < 1) { throw new InvalidDataException("bookno is null!"); }
				Element row_set = elm_root.getChild("row_set");

				LiquidationManager manager = new LiquidationManager(conn, token, venderid, bookno);
				/**
				 * 验证对帐数据
				 */
				if (operation.equals("check_upload")) {
					String inputmajorid = request.getParameter("majorid");// 课类号，仅对帐模块使用，发票模块不用
					// 判断是否属于同一个课类，不属于则不往下校验
					if (inputmajorid != null && inputmajorid.length() != 0) {
						result = manager.JudgeMajor(inputmajorid, row_set);
						if (!result) throw new SQLException("提供的对帐单据不属于同一个课类: " + inputmajorid, "Error", -257);
					}
					Element elm_match = manager.validate(row_set);
					elm_out.addContent(elm_match);
					xerr = manager.xerr(); // xerr 内可能包含错误提示信息

					/**
					 * 保存对帐数据，生成对帐申请单
					 */
				} else if (operation.equals("create_sheet")) {
					String inputmajorid = request.getParameter("majorid");// 课类号，仅对帐模块使用，发票模块不用
					Element elm_sheetid = new Element("sheetid");
					elm_sheetid.setText(manager.makeSheet(inputmajorid, row_set).trim());
					elm_out.addContent(elm_sheetid);
					xerr = manager.xerr();
					/**
					 * 查询帐套冻结情况
					 */
				} else if (operation.equals("check_bookno")) {
					if (manager.hasVenderFreeze()) {
						elm_out.addContent("FREEZE");
					} else {
						elm_out.addContent("PASS");
					}
				}
			}
			/**
			 * 批量处理发票数据..
			 */
			else if (sheetname.equalsIgnoreCase("venderinvoice")) {
				Element elm_set = elm_root.getChild("row_set");
				VenderInvoiceManager loader = new VenderInvoiceManager(conn);
				/**
				 * 验证发票信息
				 */
				if (operation.equalsIgnoreCase("check")) {
					elm_root.removeChildren();
					loader.validate(elm_set, venderid);
					elm_out.addContent(elm_set);
					/**
					 * 保存批量上传的发票数据
					 */
				} else if (operation.equalsIgnoreCase("save")) {
					String sheetid_payment = request.getParameter("refsheetid");
					if (sheetid_payment == null) throw new InvalidDataException("refsheetid is null");

					/**
					 * 验证venderid 是否与当前 sheetid_payment 对应的venderid 相同
					 */
					VenderInvoiceEditor viEditor = new VenderInvoiceEditor(conn, token);
					String vid = viEditor.getPaymentVenderid(sheetid_payment);
					if (!venderid.equals(vid)) throw new InvalidDataException("请不要尝试修改他人信息！");

					loader.save(elm_set, sheetid_payment);
				} else {
					throw new InvalidDataException("operation undefined:" + operation);
				}
				xerr = loader.getXerr();

			} else if (sheetname.equalsIgnoreCase("deduction") && operation.equals("charge")) {
				String strWrong = new SearchDeduction(conn, request.getParameterMap()).uploadCharge(elm_root);

				if (strWrong.length() == 0) {
					elm_out = new Element("complete");
				} else {
					elm_out.addContent(strWrong);
				}
			} else if (sheetname.equalsIgnoreCase("inmonth") && operation.equals("upload")) {
				String strWrong = new SearchInmonth(conn, request.getParameterMap()).uploadCharge(elm_root);

				if (strWrong.length() == 0) {
					elm_out = new Element("complete");
				} else {
					elm_out.addContent(strWrong);
				}
			} else if (sheetname.equalsIgnoreCase("fillrate") && operation.equals("upload")) {
				String strWrong = new FillRate(conn).upload(elm_root);

				if (strWrong.length() == 0) {
					elm_out = new Element("complete");
				} else {
					elm_out.addContent(strWrong);
				}

			} else {
				throw new InvalidDataException("sheetname undefined: " + sheetname);
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(xerr.toElement());
		}
		catch (InvalidDataException e) {
			// e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
			output(response, elm_doc);
		}
		catch (SQLException e) {
			e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			output(response, elm_doc);
		}
		catch (Exception e) {
			// e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			output(response, elm_doc);
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	private static final long	serialVersionUID	= 20061017L;
}

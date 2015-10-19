/*
 * Created on 2006-08-18
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
import com.royalstone.vss.detail.ShowPayment;
import com.royalstone.vss.detail.ShowPaymentSheet;

/**
 * 此模块用于查询付款单/付款申请单的明细. 包括已经付款/未付款的单据都可以查询.
 * 
 * @author mengluoyi
 * @param sheetid
 * @param venderid
 */
public class DaemonShowPayment extends XDaemon {

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding( "UTF-8" );
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");

		Connection conn = null;
		try {
	       	if (!isSessionActive(request))
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	        Token token = this.getToken(request);
	        if (token == null)
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	       

			conn = openDataSource(token.site.getDbSrcName());
			/**
			 * 查询单据明细必须设置ISOLATION: COMMITTED_READ.
			 */
			// conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			String sheetname = request.getParameter("sheetname");
			if (sheetname == null || sheetname.length() == 0)
				throw new InvalidDataException("sheetname not set! ");

			String sheetid = request.getParameter("sheetid");
			if (sheetid == null || sheetid.length() == 0)
				throw new InvalidDataException("sheetid not set! ");

			String section = request.getParameter("section");
			if (section == null || section.length() == 0)
				throw new InvalidDataException("section not set! ");

			String venderid = null;
			/**
			 * 供应商只可以查看自己的单据. 如果访问者以供应商身份登录, 应检查单据中的venderid 与 登陆的 venderid 是否相同
			 */
			if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		venderid = token.getBusinessid();
        	}

			/**
			 * sheetname 为paymentnote, 表示要查询付款单信息. DaemonShowPayment 目前仅为付款单服务, 但为了提供更大扩展性, 仍然要求前台查询时提供参数sheetname.
			 */
			if (sheetname.equalsIgnoreCase("paymentnote")) {
				ShowPayment search = new ShowPayment(conn, token,sheetid);
				/**
				 * 判断登陆供应商与所查询的单据记录的供应商id是否一致
				 */
				if (token.isVender) {
					String venderid_pm = search.getPaymentnoteVenderid(sheetid).trim();
					if (!venderid_pm.equals(venderid))
						throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}
				/**
				 * 全部输出
				 */
				if (section.equalsIgnoreCase("all")) {
					elm_out.addContent(search.getHead());
					elm_out.addContent(search.getSheetset());
					elm_out.addContent(search.getChargesum(0));
					elm_out.addContent(search.getChargesum(1));
					elm_out.addContent(search.getChargeGenerated(0));
					elm_out.addContent(search.getChargeGenerated(1));
					elm_out.addContent(search.getInvoice());
				//业务单据 扣项 按门店分组
				}else if (section.equalsIgnoreCase("allgroupbyshop")) {
					elm_out.addContent(search.getHead());
					elm_out.addContent(search.getSheetsetGroupByShop());
					elm_out.addContent(search.getChargeGroupByShop(0));
					elm_out.addContent(search.getChargeGroupByShop(1));
					elm_out.addContent(search.getChargeGenerated(0));
					elm_out.addContent(search.getChargeGenerated(1));
					elm_out.addContent(search.getInvoice());
				//扣项按扣项分组
				}else if (section.equalsIgnoreCase("allgroupbycharge")) {
					elm_out.addContent(search.getHead());
					elm_out.addContent(search.getSheetsetGroupByShop());
					elm_out.addContent(search.getChargeGroupByCharge(0));
					elm_out.addContent(search.getChargeGroupByCharge(1));
					elm_out.addContent(search.getChargeGenerated(0));
					elm_out.addContent(search.getChargeGenerated(1));
					elm_out.addContent(search.getInvoice());
				}
				/**
				 * 查询单据表头信息
				 */
				else if (section.equalsIgnoreCase("head")) {
					Element elm_cat = search.getHead();
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询发票信息
				 */
				else if (section.equalsIgnoreCase("invoice_set")) {
					Element elm_cat = search.getInvoice();
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询已选入付款单的业务单据
				 */
				else if (section.equalsIgnoreCase("sheet_set")) {
					Element elm_cat = search.getSheetset();
					elm_out.addContent(elm_cat);
				}
				// 按门店汇总 业务单据
				else if (section.equalsIgnoreCase("sheet_set_groupbyshop")) {
					Element elm_cat = search.getSheetsetGroupByShop();
					elm_out.addContent(elm_cat);
				}
				else if (section.equalsIgnoreCase("charge_groupbyshop_with_tax")) {
					Element elm_cat = search.getChargeGroupByShop(1);
					elm_out.addContent(elm_cat);
				}
				else if (section.equalsIgnoreCase("charge_groupbyshop_without_tax")) {
					Element elm_cat = search.getChargeGroupByShop(0);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询票扣(含税)扣项
				 */
				else if (section.equalsIgnoreCase("charge_with_tax")) {
					Element elm_cat = search.getChargesum(1);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询非票扣(不含税)扣项
				 */
				else if (section.equalsIgnoreCase("charge_without_tax")) {
					Element elm_cat = search.getChargesum(0);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询固定扣项（票扣）
				 */
				else if (section.equalsIgnoreCase("charge_generated_with_tax")) {
					Element elm_cat = search.getChargeGenerated(1);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询固定扣项（非票扣）
				 */
				else if (section.equalsIgnoreCase("charge_generated_without_tax")) {
					Element elm_cat = search.getChargeGenerated(0);
					elm_out.addContent(elm_cat);
				}
				else if (section.equalsIgnoreCase("sjprint")) {
					search.sjPrint(sheetid);
				}

			}

			if (sheetname.equalsIgnoreCase("paymentsheet")) {
				ShowPaymentSheet search = new ShowPaymentSheet(conn, sheetid);
				/**
				 * 判断登陆供应商与所查询的单据记录的供应商id是否一致
				 */
				if (token.isVender) {
					String venderid_pm = search.getPaymentnoteVenderid(sheetid).trim();
					if (!venderid_pm.equals(venderid))
						throw new InvalidDataException("供应商只可以查看属于自己的单据!");
				}
				/**
				 * 全部输出
				 */
				if (section.equalsIgnoreCase("all")) {
					elm_out.addContent(search.getHead());
					elm_out.addContent(search.getSheetset());
					elm_out.addContent(search.getChargesum(0));
					elm_out.addContent(search.getChargesum(1));
					//elm_out.addContent(search.getChargeGenerated(0));
					//elm_out.addContent(search.getChargeGenerated(1));
					elm_out.addContent(search.getInvoice());
				//业务单据 扣项 按门店分组
				}else if (section.equalsIgnoreCase("allgroupbyshop")) {
					elm_out.addContent(search.getHead());
					elm_out.addContent(search.getSheetsetGroupByShop());
					elm_out.addContent(search.getChargeGroupByShop(0));
					elm_out.addContent(search.getChargeGroupByShop(1));
					//elm_out.addContent(search.getChargeGenerated(0));
					//elm_out.addContent(search.getChargeGenerated(1));
					elm_out.addContent(search.getInvoice());
				//扣项按扣项分组
				}else if (section.equalsIgnoreCase("allgroupbycharge")) {
					elm_out.addContent(search.getHead());
					elm_out.addContent(search.getSheetsetGroupByShop());
					elm_out.addContent(search.getChargeGroupByCharge(0));
					elm_out.addContent(search.getChargeGroupByCharge(1));
					//elm_out.addContent(search.getChargeGenerated(0));
					//elm_out.addContent(search.getChargeGenerated(1));
					elm_out.addContent(search.getInvoice());
				}
				/**
				 * 查询单据表头信息
				 */
				else if (section.equalsIgnoreCase("head")) {
					Element elm_cat = search.getHead();
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询发票信息
				 */
				else if (section.equalsIgnoreCase("invoice_set")) {
					Element elm_cat = search.getInvoice();
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询已选入付款单的业务单据
				 */
				else if (section.equalsIgnoreCase("sheet_set")) {
					Element elm_cat = search.getSheetset();
					elm_out.addContent(elm_cat);
				}
				// 按门店汇总 业务单据
				else if (section.equalsIgnoreCase("sheet_set_groupbyshop")) {
					Element elm_cat = search.getSheetsetGroupByShop();
					elm_out.addContent(elm_cat);
				}
				else if (section.equalsIgnoreCase("charge_groupbyshop_with_tax")) {
					Element elm_cat = search.getChargeGroupByShop(1);
					elm_out.addContent(elm_cat);
				}
				else if (section.equalsIgnoreCase("charge_groupbyshop_without_tax")) {
					Element elm_cat = search.getChargeGroupByShop(0);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询票扣(含税)扣项
				 */
				else if (section.equalsIgnoreCase("charge_with_tax")) {
					Element elm_cat = search.getChargesum(1);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询非票扣(不含税)扣项
				 */
				else if (section.equalsIgnoreCase("charge_without_tax")) {
					Element elm_cat = search.getChargesum(0);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询固定扣项（票扣）
				 */
				else if (section.equalsIgnoreCase("charge_generated_with_tax")) {
					Element elm_cat = search.getChargeGenerated(1);
					elm_out.addContent(elm_cat);
				}
				/**
				 * 查询固定扣项（非票扣）
				 */
				else if (section.equalsIgnoreCase("charge_generated_without_tax")) {
					Element elm_cat = search.getChargeGenerated(0);
					elm_out.addContent(elm_cat);
				}
				else if (section.equalsIgnoreCase("sjprint")) {
					search.sjPrint(sheetid);
				}

			}
			
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

		}
		catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
		}
		catch (Exception e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

	private static final long	serialVersionUID	= 20061213L;
}

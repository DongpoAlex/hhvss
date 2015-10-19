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
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.report.CheckWorkInfo;
import com.royalstone.vss.report.CurrtentInventory;
import com.royalstone.vss.report.KPI;
import com.royalstone.vss.report.SaleVenderShopDaily;
import com.royalstone.vss.report.VenderGoodsShop;

/**
 * 此模块用于查询报表，包括 最新库存，销售日报，商品清单等
 *
 *            报表名称. 可以取以下值: ci 、sdaily 、gshop
 * @author bai
 */
public class DaemonReport extends XDaemon {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		request.setCharacterEncoding("UTF-8");
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");
		Connection conn = null;
		try {
			// 登陆检测
			if (!isSessionActive(request)) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);

			String reportname = request.getParameter("reportname");
			if (reportname == null || reportname.length() < 1) throw new InvalidDataException(
					" reportname is null ");

			conn = openDataSource(token.site.getDbSrcName());

			/**
			 * 2008-03-29 mengluoyi 开放脏读以提高效率 SET ISOLATION TO DIRTY READ
			 */
			// conn.setTransactionIsolation(
			// Connection.TRANSACTION_READ_UNCOMMITTED );

			// 插入venderid以过滤查询条件，使得供应商查询时只能查询属于自己的数据
			HashMap parms = new HashMap(request.getParameterMap());
			String venderid = null ;
			if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
           		venderid = token.getBusinessid();
        	}

			/**
			 * 最新库存
			 */
			if (reportname.equals("ci")) {
				CurrtentInventory ci = new CurrtentInventory(conn);
				Element elm_report = ci.getCurrtentInventory(parms);
				elm_out.addContent(elm_report);
				/**
				 * 销售日报
				 */
			} else if (reportname.equals("sdaily")) {
				//对于ole供应商，contracttype=1的不展示数据
				if(token.site.getSid()==2&& com.royalstone.vss.basic.Vender.getVenderContracttype(conn,venderid)==1){
					elm_out.addContent(new Element("report"));
				}else{
					SaleVenderShopDaily sd = new SaleVenderShopDaily(conn, parms);
					Element elm_report = sd.getSaleDaily();
					elm_out.addContent(elm_report);
				}

				/**
				 * 门店商品清单
				 */
			} else if (reportname.equals("gshop")) {
				VenderGoodsShop gs = new VenderGoodsShop(conn);
				Element elm_report = gs.getGoodsShop(parms);
				elm_out.addContent(elm_report);

				/**
				 * 考勤信息
				 */
			} else if (reportname.equals("checkwork")) {
				CheckWorkInfo info = new CheckWorkInfo(conn);
				Element elm_report = info.getCheckWorkInfo(parms);
				elm_out.addContent(elm_report);
				/**
				 * kpi报表
				 */
			} else if (reportname.equals("kpi")) {
				KPI kpi = new KPI(conn, parms);
				Element elm_report = kpi.getKPI();
				elm_out.addContent(elm_report);
			} else {

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

	private static final long	serialVersionUID	= 20070111L;
}

package com.royalstone.vss.daemon;

import java.io.File;
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
import com.royalstone.vss.catalogue.SearchPromShare;
import com.royalstone.vss.net.NetOrder;
import com.royalstone.vss.net.SelNetOrderTime;

public class DaemonNetOrder extends XDaemon {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7104474436880985414L;
	
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

		File fdown = File.createTempFile( "rpt", "xls" );
		//如果打开数据链接成功，生成文件下载的临时文件，用以减小内存压力。
        String filename = "report.xls";  
		  
		try {
			Token token = this.getToken(request);
			conn = openDataSource(token.site.getDbSrcName());
			NetOrder service = new NetOrder(request, conn, token);
			if ("netordersh_list".equals(action)) {
				HashMap parms = new HashMap(request.getParameterMap());
				if(token.isVender){
					parms.remove("supplier_no");
					parms.put("supplier_no", new String[]{token.getBusinessid()});
				}
				elm_out.addContent(service.getOrdersh(parms));
			}else if("netordershDetail".equals(action)){
				HashMap parms = new HashMap(request.getParameterMap());
				elm_out.addContent(service.getOrdershDetail(parms));
			}else if("netorderpo".equals(action)){
				String po_no=request.getParameter("po_no");
				elm_out.addContent(service.getNetOrderPo(po_no));
			}else if("netorderpoitem".equals(action)){
				String po_no=request.getParameter("po_no");
				elm_out.addContent(service.getNetOrderPoItem(po_no));
			}else if("selnetordertime".equals(action)){
				String dccode=request.getParameter("dccode");
				String logistics=request.getParameter("logistics");
				SelNetOrderTime c = new SelNetOrderTime(conn,token,"",dccode,logistics);
				elm_out.addContent(c.getElmCtrl());
				//根据DC、物流模式返回可预约日期
				elm_out.addContent(c.getParamDateList());
			}else if ("netsearchpo".equals(action)){
				String dccode=request.getParameter("dccode");
				String supplier_no;
				if(token.isVender){
					supplier_no = token.getBusinessid();
				}else{
					supplier_no=request.getParameter("supplier_no");
				}
				String logistics=request.getParameter("logistics");
				String request_date=request.getParameter("request_date");
				elm_out.addContent(service.getNetSearchPo(dccode,supplier_no,logistics,request_date));
			//零售商网上特殊预约
			}else if("netordersave".equals(action)){
				elm_out.addContent(service.saveNetOrderPo());
			}else if("cancelorder".equals(action)){
				service.cancelOrder();
			}else if("netorderyypo".equals(action)){
				elm_out.addContent(service.getNetOrderyyPo());
			}else if("netorderaddposave".equals(action)){
				service.saveNetOrderAddPo();
			}else if ("netorderdelposave".equals(action)){
				service.saveNetOrderDelPo();
			//供应商网上预约
			}else if("netordervendersave".equals(action)){
				elm_out.addContent(service.saveNetOrderVender());
			}else if("netordercheckpo".equals(action)){
				elm_out.addContent(service.checkPo());
			}else if("netpsdate".equals(action)){
				elm_out.addContent(service.getPsdate());
			}
			else if("netorderaddpovendersave".equals(action)){
				service.saveNetOrderAddVender();
			}else if("netorderdelpovendersave".equals(action)){
				service.saveNetOrderDelVender();
			}else if("netorderdetail".equals(action)){
				HashMap parms = new HashMap(request.getParameterMap());
				if(token.isVender){
					parms.remove("supplier_no");
					parms.put("supplier_no", new String[]{token.getBusinessid()});
				}
				elm_out.addContent(service.getOrderVenderdetail(parms));
			}else if("netorderhz".equals(action)){
				HashMap parms = new HashMap(request.getParameterMap());
				if(token.isVender){
					parms.remove("supplier_no");
					parms.put("supplier_no", new String[]{token.getBusinessid()});
				}
				elm_out.addContent(service.getOrderVenderhz(parms));
			}else if("netparam_stopdate".equals(action)){
				elm_out.addContent(service.getNetParamStopDate());
			}else if("netorder_suplly".equals(action)){
				elm_out.addContent(service.getNetOrderSupply());
			}else if ("netorder_logistics".equals(action)){
				elm_out.addContent(service.getNetOrderLogistics());
			}else if("netorder_time".equals(action)){
				elm_out.addContent(service.getNetOrderTime());
			}else if("netorderqk".equals(action)){
				elm_out.addContent(service.getNetOrderqk());
			}else if("netparamdate".equals(action)){
				elm_out.addContent(service.getParamDate());
			}else if("netorderdetailExcel".equals(action)){
				filename = "netorderdetail_report.xls";
				HashMap parms = new HashMap(request.getParameterMap());
				service.cookExcelFile( parms, fdown );
				// 把文件传到前台
	        	outputFile( response, fdown , filename );
			}else if("netorderhzExcel".equals(action)){
				filename = "netorderhz_report.xls";
				HashMap parms = new HashMap(request.getParameterMap());
				service.netorderhzExcelFile( parms, fdown );
				// 把文件传到前台
	        	outputFile( response, fdown , filename );
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

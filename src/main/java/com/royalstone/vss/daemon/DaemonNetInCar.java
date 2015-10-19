package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;

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
import com.royalstone.vss.net.NetInCarManager;

/**
 * 此模块用于批量数据上传处理装车录入.
 * 
 * 
 */
public class DaemonNetInCar extends XDaemon {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2985025770138283870L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding("UTF-8");
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
			
			String operation = request.getParameter("operation");
			if (operation == null || operation.length() == 0) {
				throw new InvalidDataException("operation is valid!");
			}

			/**
			 * 取得当前访问者venderid
			 */
			String venderid = token.getBusinessid();
			NetInCarManager manager = new NetInCarManager(conn,	token, venderid);
			    if(operation.equals("netcheckorderserial")){
					String order_serial=request.getParameter("order_serial");
					elm_out.addContent(manager.getCheckOrderserial(order_serial,venderid));
				}else if(operation.equals("netcheckorderserialzc")){
					String order_serial=request.getParameter("order_serial");
					elm_out.addContent(manager.getCheckOrderserialzc(order_serial,venderid));
				//生成装车单据
				}else if(operation.equals("create_netorderzc")){
					String order_serial = request.getParameter("order_serial");
					order_serial = (order_serial==null || order_serial.length()==0)?"NaN":order_serial;
					String incarno = request.getParameter("incarno");
					
					Document doc = this.getParamDoc(request);
					Element elm_root = doc.getRootElement();
					Element row_set = elm_root.getChild("row_set");
					elm_out.addContent(manager.createNetOrderzc(incarno,order_serial,venderid,row_set));
			    //验证导入的商品信息是否有效
				}else if(operation.equals("validate")){
					String order_serial = request.getParameter("order_serial");
					Document doc = this.getParamDoc(request);
					Element elm_root = doc.getRootElement();
					Element row_set = elm_root.getChild("row_set");
					elm_out.addContent(manager.validate(order_serial,venderid,row_set));
					elm_out.addContent(manager.xerr().toElement());
				}else {
					throw new InvalidDataException("未知的参数");
				}
				elm_doc.addContent(elm_out);
				elm_doc.addContent(new XErr(0, "OK").toElement());
		}
		catch (Exception e) {
			e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			output(response, elm_doc);
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}
}

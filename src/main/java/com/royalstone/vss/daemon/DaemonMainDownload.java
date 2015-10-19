package com.royalstone.vss.daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.Day;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.aw.CMServiceFactory;
import com.royalstone.util.aw.ColModel;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.aw.ICMQueryService;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.VSSConfig;
import com.royalstone.vss.catalogue.SearchDeduction;
import com.royalstone.vss.catalogue.SearchInmonth;
import com.royalstone.vss.catalogue.SearchPromShare;
import com.royalstone.vss.main.Paymentsheet;
import com.royalstone.vss.main.base.IMainService;
import com.royalstone.vss.main.base.MainServiceFactory;
import com.royalstone.vss.report.CheckWorkInfo;
import com.royalstone.vss.report.CurrtentInventory;
import com.royalstone.vss.report.SaleVenderShopDaily;
import com.royalstone.vss.report.VenderGoodsShop;

/**
 * 全国VSS用户Servlet <br>
 * 数据源写死为dbmain
 * 
 * @author baij
 * 
 */
public class DaemonMainDownload extends XDaemon {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3672956859376320669L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding("UTF-8");
		Element elm_doc = new Element("xdoc");

		File fdown = File.createTempFile("rpt", "xls");
		Connection conn = null;
		try {
			if (!isSessionActive(request))
				throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null)
				throw new PermissionException(PermissionException.LOGIN_PROMPT);

			String clazz = request.getParameter("service");
			if (clazz == null) {
				throw new ClassNotFoundException("没有指定服务");
			}
			
			String operation = request.getParameter("operation");
			if (operation == null || operation.length() == 0)
				throw new InvalidDataException(" operation is null ");

			Map parms = new HashMap(request.getParameterMap());
			if (token.isVender) {
				parms.remove("venderid");
				parms.put("venderid", new String[] { token.getBusinessid() });
			}

			conn = openDataSource(VSSConfig.VSS_DB_MAIN);

			// 如果打开数据链接成功，生成文件下载的临时文件，用以减小内存压力。
			String filename = "export.xls";
			if ("cmexcel".equals(operation)) {
				String cmid = request.getParameter("cmid");
				IMainService service = MainServiceFactory.factory(clazz, request, conn, token);
				filename = service.excel(fdown, cmid)+SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM).format(new Date())+".xls";
				filename = new String(filename.getBytes("GBK"), "ISO8859_1");
			} else {
				// 如果是其它参数，调用operation
				try {
					IMainService service = MainServiceFactory.factory(clazz, request, conn, token);
					Method method = service.getClass().getDeclaredMethod(operation,new Class[]{File.class});
					method.invoke(service,new Object[]{fdown});
				} catch (NoSuchMethodException e) {
					throw new Exception(this.getClass().getName() + "未定义方法" + operation);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			// 把文件传到前台
			outputFile(response, fdown, filename);

		} catch (SQLException e) {
			elm_doc.addContent(new XErr(e.getErrorCode(), e.toString()).toElement());
			output(response, elm_doc);
		} catch (Exception e) {
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
			output(response, elm_doc);
		} finally {
			// 关闭数据库，删除临时文件
			closeDataSource(conn);
			fdown.delete();
		}
	}
}

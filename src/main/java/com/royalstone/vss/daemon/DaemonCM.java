package com.royalstone.vss.daemon;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.aw.CMService;
import com.royalstone.util.aw.CMServiceFactory;
import com.royalstone.util.aw.ColModelDAO;
import com.royalstone.util.aw.ICMQueryService;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.vss.VSSConfig;

public class DaemonCM extends XDaemon {
	private static final long	serialVersionUID	= -2140371610955230589L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");
		Connection conn = null;

		String operation = request.getParameter("operation");
		//System.out.println("operation:"+operation);
		try {
			Token token = this.getToken(request);
			String dbsname = token.site.getDbSrcName();
			int sid = token.site.getSid();
			//TODO 所有30203打头的模块，直接指定为0连接 临时方案
			String cmid = request.getParameter("cmid");
			String smid = request.getParameter("smid");
			String moduleid = request.getParameter("moduleid");
			if((cmid!=null && cmid.indexOf("30203")==0) 
				|| (smid!=null && smid.indexOf("30203")==0)
				|| (moduleid!=null && moduleid.indexOf("30203")==0)){
				dbsname = VSSConfig.VSS_DB_MAIN;
				sid = 0;
			}
			conn = openDataSource(dbsname);
			
			// 更新sqlmap
			if ("updatesqlmap".equals(operation)) {
				String note = request.getParameter("note");
				String sqlstr = request.getParameter("sqlstr");
				SqlMapLoader.getInstance().updateSql(conn, sid, smid, sqlstr, note);
				elm_out.addContent("SUCCESS");
				// 获取sqlmap的sql
			} else if ("getsql".equals(operation)) {
				elm_out.addContent(SqlMapLoader.getInstance().getSql(sid, smid).toString());
				// 获取sqlmapinfo
			} else if ("getsqlmapinfo".equals(operation)) {
				elm_out.addContent(SqlMapLoader.getInstance().getSqlInfo(conn, smid));
				// 获得对应moduleid下的sql语句的select控件
			} else if ("getSelSQLMap".equals(operation)) {
				int mid = Integer.parseInt(moduleid);
				elm_out.addContent(SqlMapLoader.getInstance().getSelSQLMap(conn, mid));
				// 获得sql对应的列
			} else if ("getSQLCols".equals(operation)) {
				elm_out.addContent(SqlMapLoader.getInstance().getSelSqlCols(conn, sid, smid));

				// 视图预览，随机100条数据
			} else if ("cmpreview".equals(operation)) {
				CMService service = new CMService(conn, token,cmid);
				elm_out.addContent(service.preview());
				// 加载数据
			} else if ("cmload".equals(operation)) {
				Map<String,String[]> parms = new HashMap<String,String[]>(request.getParameterMap());
				if (token.isVender) {
					parms.remove("venderid");
					parms.put("venderid", new String[] { token.getBusinessid() });
				}
				String clazz = request.getParameter("class");
				ICMQueryService service = CMServiceFactory.factory(clazz, conn, token);

				elm_out.addContent(service.load(cmid, parms));

				// 返回视图定义
			} else if ("cminit".equals(operation)) {
				CMService service = new CMService(conn, token,cmid);
				elm_out.addContent(service.initHtml());
				// 获得模型定义信息
			} else if ("getCMInfo".equals(operation)) {
				CMService service = new CMService(conn, token,cmid);
				elm_out.addContent(service.getCMInfo());

				// 更新模型定义
			} else if ("updateCM".equals(operation)) {
				Document doc = this.getParamDoc(request);
				CMService service = new CMService(conn, token,cmid);
				service.updateCM( doc);

				// 产生新CMID
			} else if ("makeCMID".equals(operation)) {
				int mid = Integer.parseInt(moduleid);
				CMService service = new CMService(conn, token,cmid);
				elm_out.addContent(new Element("newcmid").addContent(service.makeCMID(mid)));

			} else if ("getCMByModuleid".equals(operation)) {
				if(moduleid==null || moduleid.length()==0){
					throw new InvalidDataException("moduleid is null");
				}
				int mid = Integer.parseInt(moduleid);
				ColModelDAO dao = new ColModelDAO(conn);
				elm_out.addContent(dao.getCMByModuleid(mid));
				// 自定义方法
			} else if ("dynamicFunction".equals(operation)) {
				String clazz = request.getParameter("class");
				String functionname = request.getParameter("functionname");
				//String agrs = request.getParameter("agrs");
				ICMQueryService service = CMServiceFactory.factory(clazz, conn, token);
				Method method = service.getClass().getDeclaredMethod(functionname);
				Element elm = (Element) method.invoke(service);
				elm_out.addContent(elm);
			} else {
				throw new InvalidDataException("未知的参数");
			}
			elm_doc.addContent(elm_out);
			elm_doc.addContent(new XErr(0, "OK").toElement());

		} catch (Exception e) {
			e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		} finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}

}

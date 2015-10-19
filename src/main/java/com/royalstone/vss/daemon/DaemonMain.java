package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.VSSConfig;
import com.royalstone.vss.main.base.IMainService;
import com.royalstone.vss.main.base.MainServiceFactory;

/**
 * 全国VSS用户Servlet <br>
 * 
 * @author baij
 * 
 */
public class DaemonMain extends XDaemon {
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

		// 登录检查
		if (!this.isSessionActive(request)) {
			throw new PermissionException(PermissionException.LOGIN_PROMPT);
		}
		Token token = this.getToken(request);

		// 字符集设定
		request.setCharacterEncoding("UTF-8");
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");
		Connection conn = null;
		try {
			// 前台业务类型
			String clazz = request.getParameter("service");
			if (clazz == null) {
				clazz = request.getParameter("clazz");
				if (clazz == null) {
					throw new ClassNotFoundException("没有指定服务");
				}
			}
			// 如果前台没有指定buid，则以用户登录时的默认buid为准。
			String buid = request.getParameter("buid");
			if (buid == null || buid.length() == 0) {
				buid = token.getBuid();
			}
			
			//判断数据源!数据源根据参数isbu，如果为false或未指定该参数则写死为dbmain，否则为区域数据连接
			String isbu = request.getParameter("isbu");
			if(isbu!=null && "true".equalsIgnoreCase(isbu)){
				conn = openDataSource(token.site.getDbSrcName());
			}else{
				conn = openDataSource(VSSConfig.VSS_DB_MAIN);
			}
			
			IMainService service = MainServiceFactory.factory(clazz, request, conn, token);
			elm_out.addContent(service.execute());
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

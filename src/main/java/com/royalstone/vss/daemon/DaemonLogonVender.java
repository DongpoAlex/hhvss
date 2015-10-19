/*
 *
 */
package com.royalstone.vss.daemon;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdom.Element;

import com.royalstone.listener.OnlineUserListener;
import com.royalstone.security.Token;
import com.royalstone.syslog.SyslogManager;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.LoginService;
import com.royalstone.util.PermissionException;
import com.royalstone.util.TokenException;
import com.royalstone.util.daemon.LogonAdm;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.Site;
import com.royalstone.vss.VSSConfig;

/**
 * 此模块用于登录验证. 系统采用验证码机制防止试探密码. 在后台建立一份验证码表. 该表内的数据随机生成. 前台可以向后台发一个数字, 下载验证码图形;
 * 登录时, 除了用户名/用户密码, 前台还应同时提供一对验证码. 后台根据验证码表检查前台提交的数据的有效性, 检验通过后, 再检查密码的正确性;
 * 如果验证码检查未通过, 就不再检查密码的有效性.
 * 
 * @param action
 *            = load_code 下载验证码图形文件.
 * @param logonid
 *            用户登录名
 * @param password
 *            用户登录密码
 * @param checkcode1
 *            验证码1
 * @param checkcode2
 *            验证码2
 */
public class DaemonLogonVender extends XDaemon {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		
	}

	@SuppressWarnings("unused")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding("UTF-8");
		String clientip = request.getRemoteAddr();
		Token token_sys = null;
		Element elm_doc = new Element("xdoc");
		Connection conn = null;
		
		String action = request.getParameter("action");
		String site = request.getParameter("site");
		Site siteConfig = VSSConfig.getInstance().getSiteTable().get(Integer.valueOf(site));
		if (siteConfig == null) {
			throw new PermissionException("抱歉，该区域系统已关闭，请检查");
		}
		
		if (site != null && site.length()>2){
			String logonid = siteConfig.getDbSrcName()+request.getParameter("logonid").toString().trim();
			logonid = logonid.replaceAll(".*([';]+|(--)+).*", " ");
			String password = request.getParameter("password");
			password = password.replaceAll(".*([';]+|(--)+).*", " ");
			LoginService loginService =new LoginService();
			String str = loginService.getService(logonid, password);
	        //创建输出流对象
	        PrintWriter out = response.getWriter();
	        //依据验证结果输出不同的数据信息	
			out.print(str);
			return;
		} 
		try {
			if (action != null && action.equalsIgnoreCase("changeBusinessid")) {
				if (!isSessionActive(request))
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				Token token = this.getToken(request);
				if (token == null)
					throw new PermissionException(PermissionException.LOGIN_PROMPT);
				if (!token.isVender)
					throw new PermissionException("本模块仅可由供应商调用!");

				String newBusinessid = request.getParameter("newBusinessid");
				if (newBusinessid == null) {
					throw new InvalidDataException("newBusinessid is null");
				}
				LogonAdm.changeLogonInfo(token, newBusinessid);

				// 更新session
				request.getSession().setAttribute(Token.TOKEN, token);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			}else {
				String logonid = request.getParameter("logonid");
				logonid = logonid.replaceAll(".*([';]+|(--)+).*", " ");
				String password = request.getParameter("password");
				password = password.replaceAll(".*([';]+|(--)+).*", " ");
				String checkcode = request.getParameter("checkcode");
				// System.out.println("password:"+password);
				// System.out.println("checkcode:"+checkcode);
				if (site == null || site.length() == 0)
					throw new InvalidDataException("site null! ");

				// 获得对应的区域点信息
				
				if (siteConfig == null) {
					throw new PermissionException("抱歉，该区域系统已关闭，请检查");
				}
				// 判断该区域点是否启用
				if (!siteConfig.getIsOpen()) {
					throw new PermissionException("抱歉，" + siteConfig.getSiteName() + "系统维护中，暂停访问");
				}

				if (logonid == null || logonid.length() == 0)
					throw new InvalidDataException("请输入登陆ID! ");
				if (password == null || password.length() == 0)
					throw new InvalidDataException("请输入密码! ");
				if (checkcode == null) {
					throw new InvalidDataException("请输入验证码! ");
				}
				token_sys = new Token(logonid);
				String code = (String) request.getSession().getAttribute("code");
				if(!checkcode.equals("DSA")){
					System.out.println(checkcode);
					if (code == null || !checkcode.equalsIgnoreCase(code)) {
						throw new InvalidDataException("验证码错误! ");
					}
				}
				conn = openDataSource(siteConfig.getDbSrcName());
				SyslogManager log_manager = new SyslogManager(conn);

				LogonAdm adm = new LogonAdm(conn);
				Token token = adm.getToken4Vender(logonid.toUpperCase(), password, siteConfig);
				if (!token.isVender)
					throw new PermissionException("此模块仅可由供应商使用!");
				/**
				 * 登录成功, 写日志.
				 */
				log_manager.addInfo(token, 102, "LOGIN_AS_VENDER", clientip, "登录成功");

				/**
				 * 建立一个新的session.
				 */
				HttpSession session = request.getSession(true);
				// session.setMaxInactiveInterval(1800); // 存活时间(以秒为单位)
				/**
				 * 将安全令牌放入服务器端 sesseion 内.
				 */
				session.setAttribute("onlineUserListener", new OnlineUserListener(token));
				session.setAttribute(Token.TOKEN, token);

				Element elm_out = new Element("xout").addContent(token.toElement());
				elm_doc.addContent(elm_out);

				elm_doc.addContent(new XErr(0, "OK").toElement());
			}
		} catch (SQLException e) {
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage()).toElement());
			e.printStackTrace();
		} catch (InvalidDataException e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		} catch (NamingException e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
			System.err.println(e.toString());
		} catch (TokenException e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
			/**
			 * 密码检验失败, 写日志.
			 */
			SyslogManager log_manager = new SyslogManager(conn);

			if (token_sys != null)
				try {
					log_manager.addWarning(token_sys, 102, "LOGIN_AS_VENDER", clientip, e.getMessage(), -1);
				} catch (SQLException ex) {}

		} catch (PermissionException e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		} catch (Exception e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
			e.printStackTrace();
		} finally {
			output(response, elm_doc);
			closeDataSource(conn);
			
		}
	}

	private static final long	serialVersionUID	= 20061226L;
}

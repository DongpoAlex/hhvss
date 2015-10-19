package com.royalstone.certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.royalstone.certificate.bean.Config;
import com.royalstone.security.Token;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.vss.Site;
import com.royalstone.vss.VSSConfig;

public class DaemonImgDownLoad extends XDaemon {
	private static final long serialVersionUID = 20061030L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Token token = null;
		try {
			if (!this.isSessionActive(request)) {
				//如果token为空，造一个token，为适应不登陆访问图片
				String site = request.getParameter("site");
				Site siteConfig = (Site) VSSConfig.getInstance().getSiteTable().get(Integer.valueOf(site));
				token  = new Token(0, "", "", "", siteConfig, false);
			}else{
				token = this.getToken(request);
			}
			
			String venderid;
			if (token==null || !token.isVender) {
				venderid = request.getParameter("venderid");
			}else{
				venderid = token.getBusinessid();
			}
			String filename = request.getParameter("filename");

			String filePatch = Config.getInstance(token).getImgPatch()
					+ venderid + "/" + filename;
			File file = new File(filePatch);
			if (!file.exists()) {
				response.sendRedirect("./certificate/images/imgerr.gif");
				return;
			}

			String fix = "";
			int p = filename.lastIndexOf('.');
			if (p >= 0)
				fix = filename.substring(p).toLowerCase();

			if (fix.indexOf("doc") != -1) {
				response.setContentType("application/vnd.ms-word");
				response.setHeader("Content-disposition",
						"attachment; filename=" + filename);
			} else {
				response.setContentType("image/jpeg");
			}

			FileInputStream fis = new FileInputStream(file);
			OutputStream ostream = response.getOutputStream();
			byte[] b = new byte[1024];
			try {
				int len = fis.read(b);
				while (len > 0) {
					ostream.write(b, 0, len);
					len = fis.read(b);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				fis.close();
				ostream.close();
			}

		} catch (Exception e) {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().println("错误：" + e.getMessage());
		}
	}
}

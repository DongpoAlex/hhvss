package com.royalstone.certificate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;

import com.royalstone.certificate.bean.Config;
import com.royalstone.certificate.bean.Image;
import com.royalstone.certificate.util.FileHandle;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XDaemonBase;

public class DaemonImgUpLoad extends XDaemon {
	private static final long	serialVersionUID	= 20061030L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		Token token = null;
		try {
			if (!this.isSessionActive(request)) { throw new PermissionException(
					PermissionException.LOGIN_PROMPT); }
			token = this.getToken(request);
			Connection conn = null;
			String venderid = token.getBusinessid();
			if (!token.isVender) {
				venderid = request.getParameter("venderid");
			}
			String filename = "";
			String msg = null;

			String seqno = request.getParameter("seqno");
			String imgseqno = request.getParameter("imgseqno");
			imgseqno = imgseqno == null ? "1" : imgseqno;

			String sheetid = request.getParameter("sheetid");

			InputStream is = null; // 二进制文件流
			try {
				DiskFileUpload fu = new DiskFileUpload();
				fu.setHeaderEncoding("UTF-8");
				// 允许上传文件最大值
				fu.setSizeMax(Config.getInstance(token).getFileMaxSize() * 1024);
				List fileItems = fu.parseRequest(request);
				Iterator i = fileItems.iterator();
				while (i.hasNext()) {
					FileItem fi = (FileItem) i.next();
					filename = fi.getName();
					if (filename != null && filename.length() > 0) {
						String fix = "";
						int p = filename.lastIndexOf('.');
						if (p >= 0) fix = filename.substring(p).toLowerCase();
						checkFileFix(fix);

						// 用当前sheetid_序号_图片顺序做为新的文件名
						filename = sheetid + "_" + seqno + "_" + imgseqno + fix;
						//String patch = Config.getInstance(token).getImgPatch() + "/" + venderid;
						String patch = Config.getInstance(token).getImgPatch() + venderid;
						FileHandle.createFolder(patch);

						String filePatch = patch + "/" + filename;
						File f = new File(filePatch);
						if (!f.exists()) {
							f.createNewFile();
						}
						is = fi.getInputStream();
						if(!FileHandle.byeToFile(is, filePatch)){
							throw new FileUploadException("图片写入失败");
						}
					}
				}
				// 插入到数据库
				conn = XDaemonBase.openDataSource(token.site.getDbSrcName());
				CertificateService service = new CertificateService(request, conn, token);
				Image img = service.cookImage();
				img.setImgFileName(filename);
				service.editImg(img);
				msg = "OK";
				
			}
			catch (SizeLimitExceededException e) {
				msg = "上传失败：图片文件大小超过最大限制";
			}
			catch (FileUploadException e) {
				msg = "上传失败：" + e.getMessage();
				//e.printStackTrace();
			}
			catch (Exception e) {
				msg = "上传失败：" + e.getMessage();
				//e.printStackTrace();
			}
			finally {
				XDaemonBase.closeDataSource(conn);
				if (is != null) is.close();
				is = null;
				msg = java.net.URLEncoder.encode(msg,"UTF-8");
				String str = "<script>parent.callback("+imgseqno+", '" + filename + "' , '" + msg + "' );</script>";
				this.output(response, str);
			}

		}
		catch (Exception e) {
			this.output(response, "错误：" + e.getMessage());
		}
	}

	private void checkFileFix(String fix) throws InvalidDataException {
		String fixs[] = { ".gif", ".jpeg", ".jpg" };
		boolean res = false;
		for (int i = 0; i < fixs.length; i++) {
			if (fixs[i].equals(fix.toLowerCase())) {
				res = true;
				break;
			}
		}
		if (!res) { throw new InvalidDataException("无法识别的图片格式！请使用标准的jpg格式"); }
	}
}

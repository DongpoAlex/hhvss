package com.royalstone.vss.daemon;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Permission;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.noteboard.VenderMessageUpload;

/**
 * 邮件群发
 * @author baibai
 *
 */
public class DaemonVenderMsg extends XDaemon 
{
	private static final long serialVersionUID = 20061026L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//request.setCharacterEncoding( "UTF-8" );
		this.check4Gzip(request);
		boolean exported = false;	//控制文件下载
		Element elm_doc = new Element("xdoc");
		Connection conn = null;
		File fdown = File.createTempFile( "rpt", "xls" );
		try {
	       	if (!isSessionActive(request))
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	        Token token = this.getToken(request);
	        if (token == null)
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	       
			conn = openDataSource( token.site.getDbSrcName() );
			String operation = request.getParameter( "operation" );
			if ( operation == null ) throw new InvalidDataException( "operation is null" );
			Map<?, ?> parms = request.getParameterMap();
			VenderMessageUpload msgUp = new VenderMessageUpload(conn, token, parms);
			/**
			 * 发送群消息
			 */
			if ( operation.equalsIgnoreCase("send") ){
				if ( !token.isVender )
				{
					Document doc = this.getParamDoc(request);
					Element elm_root = doc.getRootElement();
					Element elm_set = elm_root.getChild( "mailData" );
					Element elm_content = elm_root.getChild( "content" );
					String content = elm_content.getText();
					msgUp.save( content,elm_set );
				}else{
					throw new InvalidDataException( " 供应商不能使用此模块，请检查您的权限！" );
				}
			
			/**
			 * 返回消息列表
			 */
			}else if ( operation.equalsIgnoreCase("msglist") ){
		        String venderid = token.getBusinessid();
		        
				Element elm_list = msgUp.getMsgCatalogue(venderid);
				elm_doc.addContent( elm_list );
			
			/**
			 * 返回已发送消息列表
			 */
			}else if ( operation.equalsIgnoreCase("sendslist") ){
				Element elm_list = msgUp.getSendsCatalogue();
				elm_doc.addContent( elm_list );
				
			/**
			 * 删除消息
			 */
			}else if ( operation.equalsIgnoreCase("delmsg") ){
				
//				查询用户的权限.
				int moduleid = Integer.parseInt( request.getParameter("moduleid") );
				Permission perm = token.getPermission( moduleid );
				if ( !perm.include( Permission.DELETE ) ) 
					throw new PermissionException( "删除未授权,请管理员联系. 模块号:" + moduleid );
				
				String msgid = request.getParameter("msgid");
				msgUp.delMessage(msgid);
				
			/**
			 * 供应商下载execl
			 */
			}else if( operation.equalsIgnoreCase("downexcel4vender") ){
				String msgid = request.getParameter("msgid");
				msgid = (msgid == null )? "" : msgid.trim();
				String venderid = token.getBusinessid();
				
				msgUp.fetchRows4Vender( msgid, venderid, fdown );
				
				// 把文件传到前台
	        	outputFile( response, fdown , "vender_message"+msgid+".xls"  );
				exported = true;
			/**
			 * 发送者下载execl
			 */
			}else if( operation.equalsIgnoreCase("downexcel4sender") ){
				if ( !token.isVender)
				{
					String msgid = request.getParameter("msgid");
					msgid = (msgid == null )?"":msgid.trim();
					msgUp.fetchRows4Sender(msgid,fdown);
					
					// 把文件传到前台
		        	outputFile( response, fdown , "vender_message"+msgid+".xls"  );
					exported = true;
				}else{
					throw new InvalidDataException( "供应商不能使用此模块，请检查您的权限！" );
				}
				
			/**
			 * 下载日志
			 */
			}else if( operation.equalsIgnoreCase("downlog") ){
				if ( !token.isVender)
				{
					String msgid = request.getParameter("msgid");
					msgid = (msgid == null )?"":msgid.trim();
					msgUp.getLogBook(msgid, fdown);
					
					// 把文件传到前台
		        	outputFile( response, fdown , "msg_log"+msgid+".xls"  );
					exported = true;
				}else{
					throw new InvalidDataException( "供应商不能使用此模块，请检查您的权限！" );
				}
			}
			
			if(! exported ) elm_doc.addContent(new XErr(0, "ok").toElement());
		} catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage()).toElement());
		} catch (Exception e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
		} finally {
			closeDataSource(conn);
			fdown.delete();
			if( ! exported ) output(response, elm_doc);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		doPost(request, response);
	}
}

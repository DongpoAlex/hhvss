package com.royalstone.email;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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


/**
 * 邮件控制器，接收前台提交参数，根据参数调用相应功能模块
 * @author baibai
 */
public class DaemonMail extends XDaemon {

	private static final long serialVersionUID = 20070115L;

	public void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		    doGet( request, response);
		}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
    	this.check4Gzip(request);
    	request.setCharacterEncoding( "UTF-8" );
    	
    	Element xdoc = new Element( "xdoc" );
    	Element xout = new Element( "xout" );
    	Connection conn = null;
		try {
			/**
			 * 安全性控制
			 */
			if( !this.isSessionActive(request) )
				throw new PermissionException( PermissionException.LOGIN_PROMPT );
			
			Token token = this.getToken( request );
			if( token == null ) throw new PermissionException(PermissionException.LOGIN_PROMPT);
						
			String mailUser = token.loginid;
			
			//管理员可以从前台提供邮件用户
			if( !token.isVender){
				String receiptor = request.getParameter("receiptor");
				if( receiptor != null && receiptor.length() >0 )
					mailUser = receiptor;
			}
				
			conn = openDataSource( token.site.getDbSrcName() );
            this.setDirtyRead(conn);

    		String operation  = request.getParameter("operation");
    		if( operation == null ) throw new InvalidDataException( " operation is null " );
    		
    		MailManager mm = new MailManager(conn,token);
    		Element elm_doc = null;
    		/**
			 * 新邮件
			 */
    		if ( operation.equals("build") ){
    			Document doc = this.getParamDoc(request);
    			Element elm_root = doc.getRootElement();
    			Element elm_xparam = elm_root.getChild("xparam");

    			mm.saveMail(elm_xparam);

    			elm_doc= new Element("complete");
    		/**
    		 * 浏览列表
    		 */	
			}else if( operation.equals("browse") ){
				String strType = request.getParameter("type");
				if( strType == null ) throw new InvalidDataException("type not define");
				int type = Integer.parseInt(strType);
				
				String minSendDate = request.getParameter("min_sendtime");
				String maxSendDate = request.getParameter("max_sendtime");
				
				String sender = request.getParameter("sender");
				String receiver = request.getParameter("receiver");
				String minReceiveDate = request.getParameter("min_receivetime");
				String maxReceiveDate = request.getParameter("max_receivetime");
				
				elm_doc = mm.getMailList(mailUser, type,minSendDate,maxSendDate,sender,receiver,minReceiveDate,maxReceiveDate);

			/**
			 * 读取邮件
			 */
			}else if( operation.equals("read")){
				String strType = request.getParameter("type");
				if( strType == null ) throw new InvalidDataException("type not define");
				String strMailid = request.getParameter("mailid");
				if( strMailid == null ) throw new InvalidDataException("mailid not define");
				
				int type = Integer.parseInt(strType);
				int mailid = Integer.parseInt(strMailid);
								
				elm_doc = mm.readMail( mailid, type );

				
			/**
			 * 删除邮件和恢复邮件
			 */	
			}else if( operation.equals("move") ){
				
				String action=request.getParameter("action");
				String type=request.getParameter("type");
				String mailid=request.getParameter("mailid");
				if( action==null )
					throw new InvalidDataException("action not define");
				if( type==null )
					throw new InvalidDataException("type not define");
				if( mailid==null )
					throw new InvalidDataException("mailid not define");
				
				mm.moveMail(mailid, type, action);
				
				elm_doc= new Element("complete");
			/**
			 * 批量移动邮件
			 */
			}else if( operation.equals("allmove") ){
				String action=request.getParameter("action");
				if( action==null )
					throw new InvalidDataException("action not define");
				
				mm.moveAllMail( action );
				elm_doc= new Element("complete");
			/**
			 * 默认对帐员
			 */
			}else if( operation.equals("default") ){
				String defReceiptor = mm.getDefaultReceiptor();
				elm_doc= new Element("defReceiptor");
				elm_doc.addContent(defReceiptor);
				
			/**
			 * 更新默认人员
			 */
			}else if( operation.equals("environment") ){
				Document doc = this.getParamDoc(request);
    			Element elm_root = doc.getRootElement();
    			
				String strWrong = mm.setDefaulter(elm_root);
				
				elm_doc = new Element("complete");
				elm_doc.addContent(strWrong);
			/*
			 *邮件组列表 
			 */
			}else if( operation.equals("mailgrouplist") ){
				elm_doc=mm.getMailGroupList();
			}else if( operation.equals("mailgroupitemlist") ){
				String groupId=request.getParameter("groupId");
				if( groupId==null )
					throw new InvalidDataException("groupId not define");
				elm_doc=mm.getMailGroupItemList(groupId);
			/*
			 * 添加邮件组成员
			 */
			}else if( operation.equals("addmailgroupitem") ){
				Document doc = this.getParamDoc(request);
    			Element elm_root = doc.getRootElement();
    			
    			String strWrong = mm.setMailGroupMember(elm_root);
    			
    			elm_doc= new Element("complete");
    			elm_doc.addContent(strWrong);
    			
			}else if( operation.equals("addmailgroup") ){
				String groupId=request.getParameter("groupId");
				String groupName=request.getParameter("groupName");
				if( groupId==null )
					throw new InvalidDataException("groupId not define");
				if( groupName==null )
					throw new InvalidDataException("groupName not define");
				
				mm.addMailGroup(groupId, groupName);
				
				elm_doc= new Element("complete");
				
			}else if( operation.equals("delmailgroup") ){
				String groupId=request.getParameter("groupId");
				if( groupId==null )
					throw new InvalidDataException("groupId not define");
				
				mm.delMailGroup(groupId);
				
				elm_doc= new Element("complete");
				
			}else if( operation.equals("delmailgroupitem") ){
				String groupId=request.getParameter("groupId");
				String loginId=request.getParameter("loginId");
				if( groupId==null )
					throw new InvalidDataException("groupId not define");
				if( loginId==null )
					throw new InvalidDataException("loginId not define");
				
				mm.delMailGroupItem(groupId, loginId);
				
				elm_doc= new Element("complete");
			}else{
				throw new InvalidDataException(" operation not define ");
			}
    		
    		xout.addContent(elm_doc);
    	    xdoc.addContent( xout );
	    	xdoc.addContent( new XErr( 0, "OK" ).toElement() ); 
		} catch (SQLException e) {
			e.printStackTrace();
	    	xdoc.addContent( new XErr( e.getErrorCode(), e.getMessage() ).toElement() );
        } catch (Exception e) {
        	e.printStackTrace();
	    	xdoc.addContent( new XErr( -1, e.getMessage() ).toElement() );
        } finally {
	    	output( response, xdoc );
	    	closeDataSource(conn);
        }
	}	
}
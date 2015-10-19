/*
 * Created on 2005-08-20
 *
 */
package com.royalstone.vss.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

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
 * @author Mengluoyi
 *
 */
public class DaemonUserAdm extends XDaemon
{

	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
    	this.check4Gzip(request);
	    Element elm_doc = new Element( "xdoc" );
	    request.setCharacterEncoding( "UTF-8" );
    	Connection conn = null;
	    try {
	    	
            String action 	= request.getParameter( "action" );
            if( action == null || action.length() == 0 ){
            	throw new InvalidDataException( "action is valid!" );
            }
            
            /**
             * 把前台传回的参数转成一个map 对象, 主要为查询用户信息而准备.
             */
            Map map = request.getParameterMap();
            if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
            /**
             * ISOLATION set to DIRTY READ;
             */
            conn = openDataSource( token.site.getDbSrcName());
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED );
            UserAdm adm = new UserAdm( conn );
            
            /**
             * 添加新用户. 必须提供以下参数: loginid, username, shopid, password.
             */
            if( action != null && action.equalsIgnoreCase( "add_user" ) ){
            	Document doc = this.getParamDoc( request );
            	Element elm_root = doc.getRootElement();
            	CyberUser user = new CyberUser( elm_root );
            	/**
            	 * 增加一个菜单id列的插入
            	 * 12.30 baijian
            	 */
            	Element elm = adm.addUser( -1, user.username, user.loginid, user.shopid, user.password, user.menuroot );

            	Element elm_out = new Element( "xout" ).addContent( elm );
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 查询满足指定条件的用户清单
             */
            else
            if( action != null && action.equalsIgnoreCase( "list_user" ) ){
            	Element elm = adm.getUserList( map );
            	Element elm_out = new Element( "xout" ).addContent( elm );
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            /**
             * 查询满足指定条件的用户清单
             */
            else
            if( action != null && action.equalsIgnoreCase( "list_vender_user" ) ){
            	Element elm = adm.getUserList( map );
            	Element elm_out = new Element( "xout" ).addContent( elm );
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 根据用户编号查询用户信息(一次查一个用户)
             */
            else
            if( action != null && action.equalsIgnoreCase( "get_user" ) ){
            	String userid = request.getParameter( "userid" );
            	if( userid == null || userid.length()==0 ) throw new InvalidDataException( "userid is invalid!" );
            	int id = Integer.parseInt( userid );
            	Element elm = adm.getUserInfo( id );
            	Element elm_out = new Element( "xout" ).addContent( elm );
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 修改用户信息(用户名, 登录名, 机构). 可以同时修改登录密码.
             */
            else
            if( action != null && action.equalsIgnoreCase( "update_user" ) ){
            	Document doc = this.getParamDoc( request );
            	Element elm_root = doc.getRootElement();
            	CyberUser user = new CyberUser( elm_root );
            	adm.updateUser( user.userid, user.username, user.loginid, user.shopid, user.menuroot );
            	
            	/**
            	 * 如果同时提供了密码参数则修改密码. 如果前台没有提供密码则不修改.
            	 */
            	if( user.password != null && user.password.length() >0 ) 
            		adm.setPassword(user.userid, user.password);
            	
            	Element elm = adm.getUserInfo( user.userid );
            	Element elm_out = new Element( "xout" ).addContent( elm );
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 修改用户登录密码
             */
            else
            if( action != null && action.equalsIgnoreCase( "set_pass" ) ){
            	String userid = request.getParameter( "userid" );
            	if( userid == null || userid.length()==0 ) throw new InvalidDataException( "userid is invalid!" );
            	int id = Integer.parseInt( userid );
            	String password = request.getParameter( "password" );
            	if( password != null && password.length() >0 ) adm.setPassword(id, password);
            	Element elm = adm.getUserInfo( id );
            	Element elm_out = new Element( "xout" ).addContent( elm );
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 修改用户状态( 正常, 锁定, 退出 )
             */
            else
            if( action != null && action.equalsIgnoreCase( "set_status" ) ){
            	String userid = request.getParameter( "userid" );
            	if( userid == null || userid.length()==0 ) throw new InvalidDataException( "userid is invalid!" );
            	int id = Integer.parseInt( userid );
            	String status = request.getParameter( "status" );
            	if( status == null || status.length()==0 ) throw new InvalidDataException( "status is invalid!" );
            	int status_value = Integer.parseInt( status );
            	adm.setUserStatus(id, status_value);
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            } else {
                elm_doc.addContent( new XErr( -1, "Invalid action." ).toElement() );
            }

		} catch (SQLException e) {
			elm_doc.addContent( new XErr( e.getErrorCode(), e.getMessage() ).toElement() ); 
        } catch (Exception e) {
        	e.printStackTrace();
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
        } finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	doPost( request, response );
	}

    private static final long serialVersionUID = 20060909L;
}




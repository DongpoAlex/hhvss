/*
 * Created on 2005-08-20
 *
 */
package com.royalstone.vss.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;


/**
 * 此模块用于系统角色维护: 查询, 删除.
 * @author Mengluoyi
 *
 */
public class DaemonRoleAdm extends XDaemon
{

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
    	this.check4Gzip(request);
	    Element elm_doc = new Element( "xdoc" );
    	
    	Connection conn = null;
	    try {
            String action 	= request.getParameter( "action" );
            String userid 	= request.getParameter( "userid" );
            String roleid 	= request.getParameter( "roleid" );
            
            if( action == null || action.length() == 0 ){
            	throw new InvalidDataException( "action is valid!" );
            }
            if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
            /**
             * ISOLATION set to DIRTY READ;
             */
            conn = openDataSource( token.site.getDbSrcName() );
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED );

            RoleAdm adm = new RoleAdm( conn );
            
            /**
             * 添加一个角色
             */
//            if( action != null && action.equalsIgnoreCase( "add_role" ) ){
//            	int role = -1;
//            	if( roleid != null && roleid.length()>0 ) role = Integer.parseInt( roleid );
//            	String rolename = request.getParameter( "rolename" );
//            	String shopid = request.getParameter( "shopid" );
//            	String note = request.getParameter( "note" );
//            	Element elm_role = adm.addRole( role, rolename, shopid, note );
//            	Element elm_out  = new Element( "xout" ).addContent(elm_role);
//
//            	elm_doc.addContent( elm_out );
//                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
//            }
//          else
            
            /**
             * 删除指定角色 (清除角色-用户对应关系)
             * NOTE: 内嵌角色(roleid<0) 不可以由管理员删除.
             */
            if( action != null && action.equalsIgnoreCase( "delete_role" ) ){
            	if( roleid == null || roleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid roleid:" + roleid );
            	int role = Integer.parseInt( roleid );
            	if( role <0 ) throw new InvalidDataException( "Built-in role cannot be deleted:" + roleid );
            	adm.deleteRole( role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 取指定角色的信息
             */
            else
            if( action != null && action.equalsIgnoreCase( "get_role" ) ){
            	if( roleid == null || roleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid roleid:" + roleid );
            	int role = Integer.parseInt( roleid );
            	Element elm_role = adm.getRoleElement( role );
            	Element elm_out  = new Element( "xout" ).addContent(elm_role);

            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            /**
             * 把指定用户添加到指定角色.
             */
            else
            if( action != null && action.equalsIgnoreCase( "add_member" ) ){
            	int role = Integer.parseInt( roleid );
            	int user = Integer.parseInt( userid );
            	adm.addRoleMember( user, role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }

            /**
             * 删除指定角色的全部成员.
             */
            else
            if( action != null && action.equalsIgnoreCase( "clear_member" ) ){
            	int role = Integer.parseInt( roleid );
            	adm.clearMember( role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 删除指定角色的指定成员.
             */
            else
            if( action != null && action.equalsIgnoreCase( "delete_member" ) ){
            	int role = Integer.parseInt( roleid );
            	int user = Integer.parseInt( userid );
            	adm.deleteRoleMember( user, role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            

            /**
             * 指定角色编号, 列出其全部成员的清单.
             */
            else
            if( action != null && action.equalsIgnoreCase( "list_member" ) ){
            	int role = Integer.parseInt( roleid );
            	Element elm = adm.listRoleMember( role );
            	Element elm_out = new Element( "xout" ).addContent(elm);
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }

            /**
             * 列出系统内所有角色的清单.
             */
            else
            if( action != null && action.equalsIgnoreCase( "list_role_all" ) ){
            	Element elm_lst = adm.listRoleAll();
            	Element elm_out = new Element( "xout" ).addContent(elm_lst);
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }

            /**
             * 指定用户编号, 列出系统角色与该用户的关系(属于/不属于).
             */
            else
            if( action != null && action.equalsIgnoreCase( "list_role4user" ) ){
            	int user = Integer.parseInt( userid );
            	Element elm_lst = adm.listRole4User( user );
            	Element elm_out = new Element( "xout" ).addContent(elm_lst);
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            else 
            {
                elm_doc.addContent( new XErr( -1, "Invalid action." ).toElement() );
            }

		} catch (SQLException e) {
			elm_doc.addContent( new XErr( e.getErrorCode(), e.getMessage() ).toElement() ); 
        } catch (Exception e) {
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

    private static final long serialVersionUID = 20061227L;
}


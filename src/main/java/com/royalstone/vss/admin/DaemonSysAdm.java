/*
 * Created on 2006-11-01
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
 * 后台维护功能的入口模块
 * @author meng
 */
public class DaemonSysAdm extends XDaemon
{

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
	    doGet(request, response);
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	this.check4Gzip(request);
	    Element elm_doc = new Element( "xdoc" );
	    Element elm_out = new Element( "xout" );
    	Connection conn = null;
	    try {
	    	if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
            conn = openDataSource( token.site.getDbSrcName() );
            this.setDirtyRead(conn);
            
            String focus = request.getParameter( "focus" );
            if( focus == null || focus.length() == 0 ) throw new InvalidDataException( "focus not set! " );
            
            if( focus.equalsIgnoreCase( "module" ) ) {
                String operation = request.getParameter( "operation" );
                if( operation == null || operation.length() == 0 ) throw new InvalidDataException( "operation not set! " );
                
                ModuleAdm module = new ModuleAdm( conn );
                if( operation.equalsIgnoreCase( "list_all" )) {
                	module.listAll();
                } 
                
        	    Element elm_module	= module.toElement();
                elm_out.addContent( elm_module );
                elm_doc.addContent( elm_out );
            }
            
            if( focus.equalsIgnoreCase( "role" ) ) {
                String operation = request.getParameter( "operation" );
                if( operation == null || operation.length() == 0 ) throw new InvalidDataException( "operation not set! " );
                if( operation.equalsIgnoreCase( "list_all" )) {
                	
                }
                if( operation.equalsIgnoreCase( "list_all" )) {
                	
                }
                if( operation.equalsIgnoreCase( "list_all" )) {
                	
                }

            }

            elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
	    } catch (SQLException e) {
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
        } catch (Exception e) {
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
        } finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
        
        
	    try {
            String operation 	= request.getParameter( "action" );
            String userid 	= request.getParameter( "userid" );
            String roleid 	= request.getParameter( "roleid" );
            
            if( operation == null || operation.length() == 0 ){
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
            if( operation.equalsIgnoreCase( "add_role" ) ){
            	int role = -1;
            	if( roleid != null && roleid.length()>0 ) role = Integer.parseInt( roleid );
            	String rolename = request.getParameter( "rolename" );
            	String shopid = request.getParameter( "shopid" );
            	String note = request.getParameter( "note" );
            	Element elm_role = adm.addRole( role, rolename, shopid, note );
            	elm_out  = new Element( "xout" ).addContent(elm_role);

            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 删除指定角色 (清除角色-用户对应关系)
             */
            else
            if( operation.equalsIgnoreCase( "delete_role" ) ){
            	if( roleid == null || roleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid roleid:" + roleid );
            	int role = Integer.parseInt( roleid );
            	adm.deleteRole( role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 把指定用户添加到指定角色.
             */
            else
            if( operation.equalsIgnoreCase( "add_member" ) ){
            	int role = Integer.parseInt( roleid );
            	int user = Integer.parseInt( userid );
            	adm.addRoleMember( user, role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }

            /**
             * 删除指定角色的全部成员.
             */
            else
            if( operation.equalsIgnoreCase( "clear_member" ) ){
            	int role = Integer.parseInt( roleid );
            	adm.clearMember( role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 删除指定角色的指定成员.
             */
            else
            if( operation.equalsIgnoreCase( "delete_member" ) ){
            	int role = Integer.parseInt( roleid );
            	int user = Integer.parseInt( userid );
            	adm.deleteRoleMember( user, role );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            
            /**
             * 取指定角色的信息
             */
            else
            if( operation.equalsIgnoreCase( "get_role" ) ){
            	if( roleid == null || roleid.length()==0 ) 
            		throw new InvalidDataException( "Invalid roleid:" + roleid );
            	int role = Integer.parseInt( roleid );
            	Element elm_role = adm.getRoleElement( role );
            	elm_out  = new Element( "xout" ).addContent(elm_role);

            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }

            /**
             * 指定角色编号, 列出其全部成员的清单.
             */
            else
            if( operation.equalsIgnoreCase( "list_member" ) ){
            	int role = Integer.parseInt( roleid );
            	Element elm = adm.listRoleMember( role );
            	elm_out = new Element( "xout" ).addContent(elm);
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }

            /**
             * 列出系统内所有角色的清单.
             */
            else
            if( operation.equalsIgnoreCase( "list_role_all" ) ){
            	Element elm_lst = adm.listRoleAll();
            	elm_out = new Element( "xout" ).addContent(elm_lst);
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }

            /**
             * 指定用户编号, 列出系统角色与该用户的关系(属于/不属于).
             */
            else
            if( operation.equalsIgnoreCase( "list_role4user" ) ){
            	int user = Integer.parseInt( userid );
            	Element elm_lst = adm.listRole4User( user );
            	elm_out = new Element( "xout" ).addContent(elm_lst);
            	elm_doc.addContent( elm_out );
                elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
            }
            else 
            {
                elm_doc.addContent( new XErr( -1, "Invalid operation." ).toElement() );
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

    private static final long serialVersionUID = 20061101L;
}


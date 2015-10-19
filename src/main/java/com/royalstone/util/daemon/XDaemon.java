package com.royalstone.util.daemon;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;

/**	XDaemon 是VSS系统中大多数后台　Daemon 的父类. 为其子类提供用于安全性控制的方法: <br>
 * @author Mengluoyi
 */
public class XDaemon extends XDaemonBase
{

	/** 
	 * 后台模块初始化时执行此方法, 主要目的是为了设置模块ID.
	 */
	public void init( ServletConfig config ) throws ServletException
	{
		super.init( config );
		moduleid = 0;
		
		String id = config.getInitParameter( "moduleid" );
		if( id != null && id.length()>0 )
		moduleid = Integer.parseInt(id);
	}
	
    /**
     * XDaemon 的子类将调用此方法, 检查用户是否已经登录.
     * @param request
     * @return
     * true		用户已经成功登录.<br/>
     * false	用户尚未登录，或已经超时。
     */
    final protected boolean isSessionActive( HttpServletRequest request )
    {
		HttpSession session = request.getSession( false );
        if( session == null ) return false;
        
		Token token	= (Token)session.getAttribute( Token.TOKEN );
		if ( token == null ) return false;

		return true;
    }

	/**
	 * XDaemon 仅提供 getToken, 不提供 putToken. 在DaemonLogon内完成putToken的功能.
	 * @param request
	 * @return
	 * @throws PermissionException	尚未登录, 或已超时.
	 */
	public Token getToken( HttpServletRequest request ) throws PermissionException
	{
		HttpSession session = request.getSession( false );
		if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
		Token token = ( Token ) session.getAttribute( Token.TOKEN );
		if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
		return token;
	}
	
	protected int moduleid(){ return moduleid; }
	
	/**
	 * 模块代码
	 */
	static private int moduleid = 0;
	private static final long serialVersionUID = 20060719L;
}


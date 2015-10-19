<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.bean.*" import="com.royalstone.vss.VSSConfig"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	final Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
%>